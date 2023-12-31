import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppFeature, DuplicatePatientDto, IdentificationTypeDto, PatientPersonalInfoDto, PatientToMergeDto, PatientType } from '@api-rest/api-model';
import { PersonMasterDataService } from '@api-rest/services/person-master-data.service';
import { AuditPatientService } from '@api-rest/services/audit-patient.service';
import { Observable, of } from 'rxjs';
import { PatientMasterDataService } from '@api-rest/services/patient-master-data.service';
import { PatientMergeService } from '@api-rest/services/patient-merge.service';
import { MatDialog } from '@angular/material/dialog';
import { WarningFusionComponent } from '../../dialogs/warning-fusion/warning-fusion.component';
import { ConfirmedFusionComponent } from '../../dialogs/confirmed-fusion/confirmed-fusion.component';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { PAGE_MIN_SIZE } from '@historia-clinica/modules/ambulatoria/modules/indicacion/constants/internment-indications';
import { PAGE_SIZE_OPTIONS } from '@historia-clinica/modules/ambulatoria/modules/indicacion/constants/internment-indications';
import { Filters } from '../control-patient-duplicate/control-patient-duplicate.component';
import { PatientProfilePopupComponent } from '../../dialogs/patient-profile-popup/patient-profile-popup.component';
import { FeatureFlagService } from '@core/services/feature-flag.service';

const ROUTE_CONTROL_PATIENT_DUPLICATE = "home/auditoria/control-pacientes-duplicados"
const REJECTTED = "Rechazado";
@Component({
	selector: 'app-patient-fusion',
	templateUrl: './patient-fusion.component.html',
	styleUrls: ['./patient-fusion.component.scss']
})
export class PatientFusionComponent implements OnInit {
	readonly pageSizeOptions: number[] = PAGE_SIZE_OPTIONS;
	listPatientData$: Observable<PatientPersonalInfoDto[]>;
	listPatientData: PatientPersonalInfoDto[];
	identificationTypeList: IdentificationTypeDto[];
	patientToAudit: DuplicatePatientDto;
	patientsTypes: PatientType[];
	keyAttributes = KeyAttributes;
	oldPatientsIds: number[] = [];
	numberOfPatients = 0;
	pageSlice: PatientPersonalInfoDto[];
	initialSize: Observable<any>;
	filterBy: Filters;
	infoPatientToAudit: string;
	filters = Filters;
	validationTwoSelectedPatients: boolean = false;
	validationColumns: boolean = false;
	nameSelfDeterminationFF: boolean;
	rejectedId: number;
	patientId: number;
	patientToMergeAuxKeyId: any = {
		names: null,
		identification: null,
		lastNames: null,
		birthDate: null,
		nameSelfDetermination: null
	}
	patientToMerge: PatientToMergeDto = {
		activePatientId: null,
		oldPatientsIds: null,
		registrationDataPerson: {
			genderId: null,
			nameSelfDetermination: null,
			phonePrefix: null,
			birthDate: null,
			firstName: null,
			identificationNumber: null,
			identificationTypeId: null,
			lastName: null,
			middleNames: null,
			otherLastNames: null,
			phoneNumber: null,
		},
	}
	isLoadingRequestMerge: boolean = false;

	constructor(private router: Router, private personMasterDataService: PersonMasterDataService,
		private auditPatientService: AuditPatientService,
		private patientMasterDataService: PatientMasterDataService, private patientMergeService: PatientMergeService, private dialog: MatDialog,
		private readonly snackBarService: SnackBarService,
		private readonly featureFlagService: FeatureFlagService) {
	}

	ngOnInit(): void {
		this.patientToAudit = JSON.parse(localStorage.getItem('patientToAudit'));
		this.filterBy = JSON.parse(localStorage.getItem('filter'));

		this.personMasterDataService.getIdentificationTypes()
			.subscribe(identificationTypes => {
				this.identificationTypeList = identificationTypes;

			});

		this.patientMasterDataService.getTypesPatient().subscribe((patientsTypes: PatientType[]) => {
			this.patientsTypes = patientsTypes;
			this.rejectedId = this.patientsTypes.find(type => type.description === REJECTTED).id;
		})

		this.getListPatientData();

		this.featureFlagService.isActive(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS).subscribe(isOn => {
			this.nameSelfDeterminationFF = isOn
		});
	}

	getListPatientData() {
		this.auditPatientService.getPatientPersonalInfo(this.patientToAudit).subscribe((patientPersonalData: PatientPersonalInfoDto[]) => {
			this.listPatientData = this.setListPatientData(patientPersonalData);
			this.listPatientData$ = of(this.listPatientData);

			this.setInitPage();
			this.setInfo();
		})
	}

	setInitPage() {
		this.pageSlice = this.listPatientData.slice(0, PAGE_MIN_SIZE);
		this.numberOfPatients = this.listPatientData.length || 0;
		this.initialSize = of(PAGE_MIN_SIZE);
	}

	setInfo() {
		this.infoPatientToAudit = this.patientToAudit?.firstName ? this.patientToAudit.firstName + " " : '';
		if (this.patientToAudit?.middleNames) {
			this.infoPatientToAudit += this.patientToAudit.middleNames + " ";
		}
		this.infoPatientToAudit += this.patientToAudit?.lastName ? this.patientToAudit.lastName + " " : '';
		if (this.patientToAudit?.otherLastNames) {
			this.infoPatientToAudit += this.patientToAudit.otherLastNames;
		}
		switch (this.filterBy) {
			case this.filters.FILTER_FULLNAME_DNI:
				this.infoPatientToAudit += " | " + this.getIdentificationType(this.patientToAudit?.identificationTypeId) + " " + this.patientToAudit.identificationNumber;
				break;
			case this.filters.FILTER_FULLNAME_BIRTHDATE:
				this.infoPatientToAudit += " | Fecha Nac. " + this.patientToAudit?.birthdate;
				break;
			case this.filters.FILTER_FULLNAME_BIRTHDATE_DNI:
				this.infoPatientToAudit += " | " + this.getIdentificationType(this.patientToAudit?.identificationTypeId) + " " + this.patientToAudit.identificationNumber + " | Fecha Nac. " + this.patientToAudit?.birthdate;
				break;
			case this.filters.FILTER_DNI:
				this.infoPatientToAudit += this.getIdentificationType(this.patientToAudit?.identificationTypeId) + " " + this.patientToAudit.identificationNumber;
				break;
		}
	}

	setListPatientData(patientPersonalData): PatientPersonalInfoDto[] {
		patientPersonalData.forEach(data => {
			data.selected = false;
		})
		return patientPersonalData
	}

	goToBack() {
		this.router.navigate([ROUTE_CONTROL_PATIENT_DUPLICATE])
	}

	getIdentificationType(value: number) {
		return this.identificationTypeList?.find(type => type.id === value).description
	}

	getPatientType(value: number) {
		return this.patientsTypes?.find(type => type.id === value).description
	}

	setSelectedPatient(patient: any) {
		if (patient.selected) {
			this.deleteDataSelected(patient.patientId);
			patient.selected = false;
			let index = this.oldPatientsIds?.indexOf(patient.patientId)
			this.oldPatientsIds.splice(index, 1);
		} else {
			patient.selected = true;
			this.oldPatientsIds.push(patient.patientId);
		}

	}

	setValuesPatientFusion(key: any, value1: any, value2?: any, id?: number) {
		switch (key) {
			case this.keyAttributes.PATIENT_ID:
				this.patientToMerge.activePatientId = value1;
				break;
			case this.keyAttributes.BIRTHDATE:
				this.patientToMerge.registrationDataPerson.birthDate = value1;
				this.patientToMergeAuxKeyId.birthDate = id;
				break;
			case this.keyAttributes.NAMES:
				this.patientToMerge.registrationDataPerson.firstName = value1;
				this.patientToMerge.registrationDataPerson.middleNames = value2;
				this.patientToMergeAuxKeyId.names = id;
				break;
			case this.keyAttributes.LASTNAMES:
				this.patientToMerge.registrationDataPerson.lastName = value1;
				this.patientToMerge.registrationDataPerson.otherLastNames = value2;
				this.patientToMergeAuxKeyId.lastNames = id;
				break;
			case this.keyAttributes.IDENTIFICATION:
				this.patientToMerge.registrationDataPerson.identificationTypeId = value1;
				this.patientToMerge.registrationDataPerson.identificationNumber = value2;
				this.patientToMergeAuxKeyId.identification = id;
				break;
			case this.keyAttributes.NAMESELFDETERMINATION:
				this.patientToMerge.registrationDataPerson.nameSelfDetermination = value1;
				this.patientToMergeAuxKeyId.nameSelfDetermination = id;
		}
	}

	deleteDataSelected(id: any) {
		if (this.patientToMergeAuxKeyId.birthDate === id) {
			this.patientToMerge.registrationDataPerson.birthDate = null;
		}
		if (this.patientToMergeAuxKeyId.names === id) {
			this.patientToMerge.registrationDataPerson.firstName = null;
			this.patientToMerge.registrationDataPerson.middleNames = null;
		}
		if (this.patientToMergeAuxKeyId.lastNames === id) {
			this.patientToMerge.registrationDataPerson.lastName = null;
			this.patientToMerge.registrationDataPerson.otherLastNames = null;
		}
		if (this.patientToMergeAuxKeyId.identification === id) {
			this.patientToMerge.registrationDataPerson.identificationTypeId = null;
			this.patientToMerge.registrationDataPerson.identificationNumber = null;
		}
		if (this.patientToMerge.activePatientId === id) {
			this.patientToMerge.activePatientId = null;
		}
		if (this.patientToMergeAuxKeyId.nameSelfDetermination === id) {
			this.patientToMerge.registrationDataPerson.nameSelfDetermination = null;
		}
	}

	merge() {
		this.validateForm();
		if (!this.validationColumns && !this.validationTwoSelectedPatients) {
			const dialogRef = this.dialog.open(WarningFusionComponent, {
				data: {
					cant: this.oldPatientsIds.length,
					fullName: '-' + (this.patientToMerge.registrationDataPerson.firstName) + " " + (this.patientToMerge.registrationDataPerson.middleNames ? this.patientToMerge.registrationDataPerson.middleNames : '') + ' ' + (this.patientToMerge.registrationDataPerson.lastName) + " " + (this.patientToMerge.registrationDataPerson.otherLastNames ? this.patientToMerge.registrationDataPerson.otherLastNames : ''),
					identification: '-' + this.getIdentificationType(this.patientToMerge.registrationDataPerson.identificationTypeId) + ' ' + this.patientToMerge.registrationDataPerson.identificationNumber,
					birthDate: '- Fecha Nac. ' + this.patientToMerge.registrationDataPerson.birthDate,
					idPatient: '- ID ' + this.patientToMerge.activePatientId,
					nameSelfDetermination: this.nameSelfDeterminationFF ? this.patientToMerge.registrationDataPerson.nameSelfDetermination ? '- ' + this.patientToMerge.registrationDataPerson.nameSelfDetermination : "-" : null,
					labelButtonConfirm: 'pacientes.audit.BUTTON_CONFIRM'
				},
				disableClose: true,
				width: '35%',
				autoFocus: false
			})
			dialogRef.afterClosed().subscribe(confirmed => {
				if (confirmed) {
					this.isLoadingRequestMerge = true;
					this.completePatientDataToMerge();
					this.patientMergeService.merge(this.patientToMerge).subscribe(res => {
						this.isLoadingRequestMerge = false;
						const dialogRef2 = this.dialog.open(ConfirmedFusionComponent, {
							data: {
								idPatients: this.patientToMerge.activePatientId
							},
							disableClose: true,
							width: '35%',
							autoFocus: false
						})
						dialogRef2.afterClosed().subscribe(close => {
							this.goToBack();
						})
					}, error => {
						this.snackBarService.showError(error.text);
						this.oldPatientsIds.push(this.patientToMerge.activePatientId);
						this.isLoadingRequestMerge = false;
					})
				}
			});
		}
	}

	validateForm() {
		if (this.oldPatientsIds.length >= 2) {
			this.validationTwoSelectedPatients = false;
		} else {
			this.validationTwoSelectedPatients = true;
		}
		if (this.patientToMerge.activePatientId === null || this.patientToMerge.registrationDataPerson.birthDate === null || this.patientToMerge.registrationDataPerson.firstName === null
			|| this.patientToMerge.registrationDataPerson.identificationNumber === null || this.patientToMerge.registrationDataPerson.lastName === null || (this.nameSelfDeterminationFF && this.patientToMerge.registrationDataPerson.nameSelfDetermination === null)) {
			this.validationColumns = true;
		} else {
			this.validationColumns = false;
		}
	}

	completePatientDataToMerge() {
		let auxiliaryPatientList: PatientPersonalInfoDto[];
		this.listPatientData$.subscribe(list => {
			auxiliaryPatientList = list;
		})
		this.patientToMerge.registrationDataPerson.genderId = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).genderId;
		this.patientToMerge.registrationDataPerson.phonePrefix = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).phonePrefix;
		this.patientToMerge.registrationDataPerson.phoneNumber = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).phoneNumber;
		this.patientToMerge.oldPatientsIds = this.oldPatientsIds;
		if (!this.nameSelfDeterminationFF) {
			this.patientToMerge.registrationDataPerson.nameSelfDetermination = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).nameSelfDetermination;
		}
		let index = this.oldPatientsIds?.indexOf(this.patientToMerge.activePatientId);
		if (index !== -1) {
			this.oldPatientsIds.splice(index, 1);
		}
	}


	onPageChange($event: any): void {
		const page = $event;
		const startPage = page.pageIndex * page.pageSize;
		this.pageSlice = this.listPatientData.slice(startPage, $event.pageSize + startPage);
	}

	viewPatient(patient: any) {
		this.dialog.open(PatientProfilePopupComponent, {
			data: {
				patientId: patient.patientId,
				viewCardToAudit: true
			},
			height: "600px",
			width: '30%',
			disableClose: true,
			autoFocus: false,
			panelClass: 'mat-dialog-container-fusion'
		})
	}
}
export enum KeyAttributes {
	BIRTHDATE,
	NAMES,
	NAMESELFDETERMINATION,
	LASTNAMES,
	PATIENT_ID,
	IDENTIFICATION,
}
