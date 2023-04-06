import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DuplicatePatientDto, IdentificationTypeDto, PatientPersonalInfoDto, PatientToMergeDto, PatientType } from '@api-rest/api-model';
import { PersonMasterDataService } from '@api-rest/services/person-master-data.service';
import { ContextService } from '@core/services/context.service';
import { PatientAuditService } from '../../services/patient-audit.service';
import { AuditPatientService } from '@api-rest/services/audit-patient.service';
import { Observable, of } from 'rxjs';
import { PatientMasterDataService } from '@api-rest/services/patient-master-data.service';
import { PatientMergeService } from '@api-rest/services/patient-merge.service';

const ROUTE_CONTROL_PATIENT_DUPLICATE = "auditoria/control-pacientes-duplicados"
@Component({
	selector: 'app-patient-fusion',
	templateUrl: './patient-fusion.component.html',
	styleUrls: ['./patient-fusion.component.scss']
})
export class PatientFusionComponent implements OnInit {
	private readonly routePrefix;
	listPatientData$: Observable<PatientPersonalInfoDto[]>;
	identificationTypeList: IdentificationTypeDto[];
	patientToAudit: DuplicatePatientDto;
	patientsTypes: PatientType[];
	keyAttributes = KeyAttributes;
	oldPatientsIds: number[]=[];
	patientToMerge: PatientToMergeDto = {
		activePatientId: null,
		oldPatientsIds:null,
		registrationDataPerson:  {
			genderId: null,
			nameSelfDetermination: null,
			phonePrefix: null,
			birthDate: null,
			firstName: null,
			identificationNumber: null,
			identificationTypeId:null,
			lastName: null,
			middleNames: null,
			otherLastNames: null,
			phoneNumber: null,
		},
	}

	constructor(private router: Router, private contextService: ContextService, private personMasterDataService: PersonMasterDataService,
		private patientAuditService: PatientAuditService, private auditPatientService: AuditPatientService,
		private patientMasterDataService: PatientMasterDataService, private patientMergeService: PatientMergeService) {
		this.routePrefix = `institucion/${this.contextService.institutionId}/`;
		this.personMasterDataService.getIdentificationTypes()
			.subscribe(identificationTypes => {
				this.identificationTypeList = identificationTypes;
			});
	}

	ngOnInit(): void {
		this.patientMasterDataService.getTypesPatient().subscribe((patientsTypes: PatientType[]) => {
			this.patientsTypes = patientsTypes;
		})
		this.patientAuditService.patientToAudit$.subscribe(patientToAudit => {
			this.patientToAudit = patientToAudit
		});

		this.auditPatientService.getPatientPersonalInfo(this.patientToAudit).subscribe((patientPersonalData: PatientPersonalInfoDto[]) => {
			this.listPatientData$ = of(this.setListPatientData(patientPersonalData));
		})
	}

	setListPatientData(patientPersonalData): PatientPersonalInfoDto[] {
		patientPersonalData.forEach(data => {
			data.selected = false;
		})
		return patientPersonalData
	}

	goToBack() {
		this.router.navigate([this.routePrefix + ROUTE_CONTROL_PATIENT_DUPLICATE])
	}

	getIdentificationType(value: number) {
		return this.identificationTypeList?.find(type => type.id === value).description
	}

	getPatientType(value: number) {
		return this.patientsTypes?.find(type => type.id === value).description
	}

	setSelectedPatient(patient: any) {
		patient.selected = !patient.selected;
		let index = this.oldPatientsIds?.indexOf(patient.patientId)
		if (index !== -1) {
			this.oldPatientsIds.splice(index, 1);
		} else {
			this.oldPatientsIds.push(patient.patientId);
		}
	}

	setValuesPatientFusion(key: any, value1: any, value2?: any) {
		switch (key) {
			case this.keyAttributes.PATIENT_ID:
				this.patientToMerge.activePatientId = value1;
				break;
			case this.keyAttributes.BIRTHDATE:
				this.patientToMerge.registrationDataPerson.birthDate = value1;
				break;
			case this.keyAttributes.NAMES:
				this.patientToMerge.registrationDataPerson.firstName = value1;
				this.patientToMerge.registrationDataPerson.middleNames = value2;
				break;
			case this.keyAttributes.LASTNAMES:
				this.patientToMerge.registrationDataPerson.lastName = value1;
				this.patientToMerge.registrationDataPerson.otherLastNames = value2;
				break;
			case this.keyAttributes.IDENTIFICATION:
				this.patientToMerge.registrationDataPerson.identificationTypeId = value1;
				this.patientToMerge.registrationDataPerson.identificationNumber = value2;
				break;
		}
	}

	merge() {
		this.completePatientDataToMerge();
		this.patientMergeService.merge(this.patientToMerge).subscribe(res => {

		})
	}

	completePatientDataToMerge() {
		let auxiliaryPatientList: PatientPersonalInfoDto[];
		this.listPatientData$.subscribe(list => {
			auxiliaryPatientList = list;
		})
		this.patientToMerge.registrationDataPerson.genderId = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).genderId;
		this.patientToMerge.registrationDataPerson.phonePrefix = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).phonePrefix;
		this.patientToMerge.registrationDataPerson.phoneNumber = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).phoneNumber;
		this.patientToMerge.registrationDataPerson.nameSelfDetermination = auxiliaryPatientList.find(patient => patient.patientId === this.patientToMerge.activePatientId).nameSelfDetermination;
		this.patientToMerge.oldPatientsIds = this.oldPatientsIds;

		this.oldPatientsIds.splice( this.oldPatientsIds?.indexOf(this.patientToMerge.activePatientId), 1);
	}

}
export enum KeyAttributes {
	BIRTHDATE,
	NAMES,
	LASTNAMES,
	PATIENT_ID,
	IDENTIFICATION,
}
