import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import {
	ARTCoverageDto, CoverageDto,
	HealthInsuranceDto,
	MedicalCoverageDto,
	PrivateHealthInsuranceDto
} from '@api-rest/api-model';
import { HealthInsuranceService } from '@api-rest/services/health-insurance.service';
import { RenaperService } from '@api-rest/services/renaper.service';
import { DateFormat, momentFormat, momentParse, newMoment } from '@core/utils/moment.utils';
import { Moment } from 'moment';
import { MIN_DATE } from "@core/utils/date.utils";
import { EPatientMedicalCoverageCondition } from '@api-rest/api-model';
import { ArtCoverageService } from "@api-rest/services/art-coverage.service";
import { HealthInsuranceComponent } from "@pacientes/dialogs/health-insurance/health-insurance.component";
import { PrivateHealthInsuranceComponent } from "@pacientes/dialogs/private-health-insurance/private-health-insurance.component";
import { FormBuilder, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { MatOptionSelectionChange } from '@angular/material/core';

const DNI_TYPE_ID = 1;
@Component({
	selector: 'app-medical-coverage',
	templateUrl: './medical-coverage.component.html',
	styleUrls: ['./medical-coverage.component.scss']
})

export class MedicalCoverageComponent implements OnInit {

	patientMedicalCoverages: PatientMedicalCoverage[];
	loading = true;
	minDate = MIN_DATE;
	artCoverageForm: FormGroup;
	artCoverageFilteredMasterData: ARTCoverageDto[];
	
	private artCoverageMasterData: ARTCoverageDto[];
	private artCoverageToAdd: MedicalCoverage;
	private healthInsuranceMasterData: MedicalCoverageDto[];

	constructor(
		private formBuilder: FormBuilder,
		private renaperService: RenaperService,
		public readonly healthInsuranceService: HealthInsuranceService,
		public readonly artCoverageService: ArtCoverageService,
		public dialogRef: MatDialogRef<MedicalCoverageComponent>,
		@Inject(MAT_DIALOG_DATA) public personInfo: {
			genderId: number;
			identificationNumber: string;
			identificationTypeId: number;
			initValues: PatientMedicalCoverage[]
		},
		private readonly dialog: MatDialog
	) {
		this.patientMedicalCoverages = this.personInfo.initValues ? this.personInfo.initValues : [];
	}


	ngOnInit(): void {

		this.healthInsuranceService.getAll().subscribe((values: MedicalCoverageDto[]) => {
			this.healthInsuranceMasterData = values;

			if (this.personInfo.identificationTypeId === DNI_TYPE_ID && this.personInfo.genderId) {
				this.renaperService.getHealthInsurance
					({ genderId: this.personInfo.genderId, identificationNumber: this.personInfo.identificationNumber })
					.subscribe((healthInsurances: MedicalCoverageDto[]) => {
						if (healthInsurances) {
							healthInsurances.forEach(healthInsurance => {
								const patientMedicalCoverage = this.patientMedicalCoverages
									.find(patientHealthInsurance => (patientHealthInsurance.medicalCoverage as HealthInsurance).rnos === healthInsurance.rnos);
								if (!patientMedicalCoverage) {
									this.patientMedicalCoverages = this.patientMedicalCoverages.concat(this.fromRenaperToPatientMedicalCoverage(healthInsurance));
								} else if (healthInsurance.dateQuery) {
									patientMedicalCoverage.validDate = momentParse(healthInsurance.dateQuery, DateFormat.YEAR_MONTH);
								}
							});
						}
						this.loading = false;

					}, _ => this.loading = false);
			} else {
				this.loading = false;
			}
		}
		);

		this.artCoverageService.getAll().subscribe((values: ARTCoverageDto[]) => {
			this.artCoverageFilteredMasterData = values;
			this.artCoverageMasterData = values;
		});

	
		this.artCoverageForm = this.formBuilder.group({
			coverage: [null, Validators.required],
			cuit: []
		});
		this.artCoverageForm.controls.cuit.disable();

		this.artCoverageForm.controls.coverage.valueChanges.subscribe((newValue: string) => {
			if (newValue) {
				this.artCoverageFilteredMasterData =
					this.artCoverageMasterData
						.filter(data => data.name.toLowerCase().includes(newValue.toLowerCase()));
			} else {
				this.artCoverageFilteredMasterData = this.artCoverageMasterData;
			}
		});

	}

	selectARTCoverage(event: MatOptionSelectionChange, coverage: CoverageDto): void {
		if (event.isUserInput){
			this.artCoverageToAdd = this.fromARTCoverageMasterDataToMedicalCoverage(coverage);
			this.artCoverageForm.controls.cuit.setValue(this.artCoverageToAdd.cuit);
		}
	}

	getFullHealthInsuranceText(healthInsurance: MedicalCoverageDto): string {
		return [healthInsurance.acronym, healthInsurance.name].filter(Boolean).join(' - ');
	}

	getMedicalCoveragePlanText(patientMedicalCoverage: PatientMedicalCoverage): string {
		return [patientMedicalCoverage.planName, patientMedicalCoverage?.condition].filter(Boolean).join(' | ');
	}

	getDatesText(patientMedicalCoverage: PatientMedicalCoverage): string {
		const initText = patientMedicalCoverage.startDate ? 'desde ' +
			momentFormat(patientMedicalCoverage.startDate, DateFormat.VIEW_DATE) : '';

		const endText = patientMedicalCoverage.endDate ? ' hasta ' +
			momentFormat(patientMedicalCoverage.endDate, DateFormat.VIEW_DATE) : '';

		return initText + endText;
	}
	// -----------------------------------------------------------------------------------------------------------------------------

	addARTCoverage(formDirective: FormGroupDirective): void {
		if (this.artCoverageToAdd && this.artCoverageForm.valid) {
			const toAdd = this.getARTCoverageToAdd();
			this.patientMedicalCoverages = this.patientMedicalCoverages.concat(toAdd);
			formDirective.resetForm();
			this.artCoverageForm.reset();
			this.artCoverageToAdd = null;
		}
	}

	save() {
		this.dialogRef.close({
			patientMedicalCoverages: this.patientMedicalCoverages.filter(pmc => pmc.id || isNewAndNotDeleted(pmc))
		});

		function isNewAndNotDeleted(pmc: PatientMedicalCoverage): boolean {
			return !pmc.id && pmc.active;
		}
	}

	close() {
		this.dialogRef.close();
	}

	getPatientHealthInsurances(): PatientMedicalCoverage[] {
		return this.patientMedicalCoverages.filter(s => s.medicalCoverage.type === EMedicalCoverageType.OBRASOCIAL && s.active);
	}

	getPatientPrivateHealthInsurances(): PatientMedicalCoverage[] {
		return this.patientMedicalCoverages.filter(s => s.medicalCoverage.type === EMedicalCoverageType.PREPAGA && s.active);
	}

	getPatientARTCoverages(): PatientMedicalCoverage[] {
		return this.patientMedicalCoverages.filter(s => s.medicalCoverage.type === EMedicalCoverageType.ART && s.active);
	}

	private getARTCoverageToAdd(): PatientMedicalCoverage {
		const toAdd: PatientMedicalCoverage = {
			medicalCoverage: this.artCoverageToAdd,
			active: true
		};
		return toAdd;
	}

	private fromARTCoverageMasterDataToMedicalCoverage(coverage: CoverageDto): MedicalCoverage {
		const coverageId = this.artCoverageFilteredMasterData
			.filter((s: CoverageDto) => s.id === coverage.id)
			.map(s => s.id)[0];
		return new MedicalCoverage(coverageId, coverage.name, EMedicalCoverageType.ART, coverage.cuit);
	}

	private fromRenaperToPatientMedicalCoverage(healthInsurance: MedicalCoverageDto): PatientMedicalCoverage {
		const healthInsuranceId = this.healthInsuranceMasterData
			.filter((s: MedicalCoverageDto) => s.rnos === healthInsurance.rnos)
			.map(s => s.id)[0];
		const medicalCoverage = new HealthInsurance(healthInsurance.rnos, healthInsurance.acronym,
			healthInsuranceId, healthInsurance.name, EMedicalCoverageType.OBRASOCIAL);

		if(medicalCoverage.id === undefined) {
			this.healthInsuranceService.get(parseInt(healthInsurance.rnos))
				.subscribe(data => {
					medicalCoverage.id = data.id;
				})
		}

		return {
			affiliateNumber: null,
			medicalCoverage,
			validDate: healthInsurance.dateQuery ? momentParse(healthInsurance.dateQuery, DateFormat.YEAR_MONTH) : newMoment(),
			active: true
		};
	}

	openAddHealthInsurance() {
		const dialogRef = this.dialog.open(HealthInsuranceComponent, {
			autoFocus: true,
			disableClose: true,
			data: {
				healthInsuranceMasterData: this.healthInsuranceMasterData
			}
		});
		dialogRef.afterClosed().subscribe(healthInsurance => {
			if (healthInsurance) {
				this.patientMedicalCoverages = this.patientMedicalCoverages.concat(healthInsurance);
			}
		});
	}

	openAddPrivateHealthInsurance() {
		const dialogRef = this.dialog.open(PrivateHealthInsuranceComponent, {
			autoFocus: true,
			disableClose: true
		});
		dialogRef.afterClosed().subscribe( (privateHealthInsurance:PatientMedicalCoverage) => {
			if (privateHealthInsurance) {
				this.patientMedicalCoverages = this.patientMedicalCoverages.concat(privateHealthInsurance);
			}
		});
	}

}

export interface PatientMedicalCoverage {
	id?: number;
	affiliateNumber?: string;
	validDate?: Moment;
	medicalCoverage: HealthInsurance | PrivateHealthInsurance | MedicalCoverage;
	startDate?: Moment;
	endDate?: Moment;
	planId?: number;
	planName?: string;
	active: boolean;
	condition?: EPatientMedicalCoverageCondition;
}
export class MedicalCoverage {
	id?: number;
	name: string;
	type: number;
	cuit: string;
	constructor(id, name, type, cuit) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.cuit = cuit;
	}

	public toMedicalCoverageDto(): CoverageDto {
		return {
			id: this.id,
			name: this.name,
			cuit: this.cuit,
			type: EMedicalCoverageType.OBRASOCIAL,
		};
	}
}

export class HealthInsurance extends MedicalCoverage {
	rnos: string;
	acronym?: string;

	constructor(rnos: string, acronym: string, id, name, type) {
		super(id, name, type, null);
		this.rnos = rnos;
		this.acronym = acronym;
	}

	public toMedicalCoverageDto(): HealthInsuranceDto {
		return {
			id: this.id,
			acronym: this.acronym,
			name: this.name,
			cuit: this.cuit,
			rnos: Number(this.rnos),
			type: EMedicalCoverageType.OBRASOCIAL,
		};
	}
}

export class PrivateHealthInsurance extends MedicalCoverage {
	constructor(id, name, type, cuit) {
		super(id, name, type, cuit);
	}

	toMedicalCoverageDto(): PrivateHealthInsuranceDto {
		return {
			id: this.id,
			name: this.name,
			cuit: this.cuit,
			type: EMedicalCoverageType.PREPAGA
		};
	}
}

export enum EMedicalCoverageType {
	PREPAGA = 1, OBRASOCIAL, ART
	}

export function determineIfIsHealthInsurance(toBeDetermined: HealthInsurance | PrivateHealthInsurance): toBeDetermined is HealthInsurance {
	if ((toBeDetermined as HealthInsurance).type) {
		return true;
	}
	return false; // case PrivateHealthInsurance
}
