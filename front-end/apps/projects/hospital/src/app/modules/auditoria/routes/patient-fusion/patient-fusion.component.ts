import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DuplicatePatientDto, IdentificationTypeDto, PatientPersonalInfoDto, PatientType } from '@api-rest/api-model';
import { PersonMasterDataService } from '@api-rest/services/person-master-data.service';
import { ContextService } from '@core/services/context.service';
import { PatientAuditService } from '../../services/patient-audit.service';
import { AuditPatientService } from '@api-rest/services/audit-patient.service';
import { Observable, of } from 'rxjs';
import { PatientMasterDataService } from '@api-rest/services/patient-master-data.service';

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
	patientFusion: PatientPersonalInfoDto;

	constructor(private router: Router, private contextService: ContextService, private personMasterDataService: PersonMasterDataService,
		private patientAuditService: PatientAuditService, private auditPatientService: AuditPatientService,
		private patientMasterDataService: PatientMasterDataService) {
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

	setValuesPatientFusion(key: any, value1: any, value2?: any) {
		switch (key) {
			case this.keyAttributes.PATIENT_ID:
				this.patientFusion.patientId = value1;
				break;
			case this.keyAttributes.BIRTHDATE:
				this.patientFusion.birthdate = value1;
				break;
			case this.keyAttributes.NAMES:
				this.patientFusion.firstName = value1;
				this.patientFusion.middleNames = value2;
				break;
			case this.keyAttributes.LASTNAMES:
				this.patientFusion.lastName = value1;
				this.patientFusion.otherLastNames = value2;
				break;
			case this.keyAttributes.IDENTIFICATION:
				this.patientFusion.identificationTypeId = value1;
				this.patientFusion.identificationNumber = value2;
				break;
		}
	}

}
export enum KeyAttributes {
	BIRTHDATE,
	NAMES,
	LASTNAMES,
	PATIENT_ID,
	IDENTIFICATION,
}
