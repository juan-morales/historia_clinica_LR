import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DiagnosesGeneralStateDto, MasterDataInterface, NewDosageDto, PharmacoDto, PharmacoSummaryDto, QuantityDto, SharedSnomedDto, SnomedDto } from '@api-rest/api-model';
import { EIndicationStatus, EIndicationType } from '@api-rest/api-model';
import { dateDtoToDate, dateToDateDto, dateToDateTimeDtoUTC } from '@api-rest/mapper/date-dto.mapper';
import { InternacionMasterDataService } from '@api-rest/services/internacion-master-data.service';
import { SnomedService } from '@historia-clinica/services/snomed.service';
import { getError, hasError } from '@core/utils/form.utils';
import { isNumberOrDot } from '@core/utils/pattern.utils';
import { getMonth, getYear, isSameDay, isToday } from 'date-fns';
import { HOURS_LIST, INTERVALS_TIME, openConfirmDialog, OTHER_FREQUENCY, OTHER_INDICATION_ID } from '../../constants/internment-indications';
import { SearchSnomedConceptsPharmacoService } from '../../services/search-snomed-concepts-pharmaco.service';

@Component({
	selector: 'app-pharmaco',
	templateUrl: './pharmaco.component.html',
	styleUrls: ['./pharmaco.component.scss']
})
export class PharmacoComponent implements OnInit {
	pharmaco: PharmacoDto = null;
	form: UntypedFormGroup;
	searchSnomedConcept: SearchSnomedConceptsPharmacoService;
	indicationDate: Date;
	vias: MasterDataInterface<number>[] = [];
	units: MasterDataInterface<number>[] = [];
	diagnostics: DiagnosesGeneralStateDto[]
	hasError = hasError;
	getError = getError;
	readonly isNumberOrDot = isNumberOrDot;
	otherFrequency = OTHER_FREQUENCY;
	intervals = INTERVALS_TIME;
	hoursList = HOURS_LIST;
	OtherIndication = OTHER_INDICATION_ID;
	NEGATIVE_OPTION = 0;
	DOSE_MIN = 0;
	FAR_OPTION = 1;
	FAST_OPTION = 2;
	TIME_CORRECTION = 24;
	FREQUENCY_OPTION_INTERVAL = 0;
	FREQUENCY_OPTION_START_TIME = 1;
	FREQUENCY_OPTION_EVENT = 2;
	SOLVENT_UNIT = "ml";
	EVENT = "e";
	HOURS = "h";
	DOSAGE = "d";

	constructor(
		@Inject(MAT_DIALOG_DATA) public data: {
			entryDate: Date, actualDate: Date, patientId: number, professionalId: number, diagnostics: DiagnosesGeneralStateDto[], vias: MasterDataInterface<number>[],
			units: MasterDataInterface<number>[], pharmaco?: PharmacoSummaryDto
		},
		private readonly dialogRef: MatDialogRef<PharmacoComponent>,
		public formBuilder: UntypedFormBuilder,
		private readonly snomedService: SnomedService,
		private readonly internacionMasterdataService: InternacionMasterDataService,
		private readonly dialog: MatDialog,

	) {
		this.internacionMasterdataService.getViasPharmaco().subscribe(v => this.vias = v);
		this.internacionMasterdataService.getUnits().subscribe(u => this.units = u);
		this.searchSnomedConcept = new SearchSnomedConceptsPharmacoService(this.formBuilder, this.snomedService);
	}

	ngOnInit(): void {
		this.indicationDate = this.data.actualDate;
		this.diagnostics = this.data.diagnostics;
		this.setForm();

		this.form.controls.hasSolvent.valueChanges.subscribe((hasSolvent: boolean) => {
			if (!hasSolvent) {
				this.searchSnomedConcept.removeValidationSolventForm();
				this.form.controls.dosageSolvent.setValue(null);
				this.form.controls.dosageSolvent.setValidators(null);
				this.form.controls.dosageSolvent.removeValidators([Validators.required]);
				this.form.controls.dosageSolvent.updateValueAndValidity();
			}
		});

		this.searchSnomedConcept.solventForm.valueChanges.subscribe(solvent => {
			if (solvent) {
				this.form.controls.dosageSolvent.setValidators([Validators.required]);
				this.form.controls.dosageSolvent.updateValueAndValidity();
			} else {
				this.form.controls.dosageSolvent.setValidators(null);
				this.form.controls.dosageSolvent.removeValidators([Validators.required]);
				this.form.controls.dosageSolvent.updateValueAndValidity();
			}

		});

		this.form.controls.interval.valueChanges.subscribe((frequencyOption) => {
			if (frequencyOption === this.otherFrequency.value) {
				this.form.controls.frequencyHour.setValidators([Validators.required]);
				this.form.controls.frequencyHour.updateValueAndValidity();
			} else {
				this.form.controls.frequencyHour.setValue(null);
				this.form.controls.frequencyHour.removeValidators([Validators.required]);
				this.form.controls.frequencyHour.updateValueAndValidity();
			}
		});

		this.form.controls.frequencyOption.valueChanges.subscribe((frequencyOption) => {

			this.removeFormValidators();

			switch (frequencyOption) {
				case this.FREQUENCY_OPTION_INTERVAL: {
					this.form.controls.startTime.reset();

					this.form.controls.interval.setValidators([Validators.required]);
					this.form.controls.interval.updateValueAndValidity();
					this.form.controls.startTime.setValidators([Validators.required]);
					this.form.controls.startTime.updateValueAndValidity();

					this.form.controls.event.reset();

					break;
				}
				case this.FREQUENCY_OPTION_START_TIME: {
					this.form.controls.startTime.setValidators([Validators.required]);
					this.form.controls.startTime.updateValueAndValidity();

					this.form.controls.interval.reset();
					this.form.controls.startTime.reset();
					this.form.controls.event.reset();
					break;
				}
				case this.FREQUENCY_OPTION_EVENT: {
					this.form.controls.event.setValidators([Validators.required]);
					this.form.controls.event.updateValueAndValidity();

					this.form.controls.interval.reset();
					this.form.controls.startTime.reset();
					break;
				}
			}
		});
		if (this.data?.pharmaco)
			this.searchSnomedConcept.setForm(this.data?.pharmaco.snomed);
	}
	private setForm() {
		const pharmaco = this.data?.pharmaco;
		const dosage = pharmaco ? pharmaco.dosage.quantity.value : null;
		const unit = pharmaco ? pharmaco.dosage.quantity.unit : null;
		const via = pharmaco ? pharmaco.viaId : null;
		const frequencyOption = pharmaco?.dosage.periodUnit ? this.getPeriodUnit(pharmaco.dosage.periodUnit) : this.FREQUENCY_OPTION_INTERVAL;
		let interval = pharmaco?.dosage.frequency ? pharmaco.dosage.frequency : null;
		const startTime = pharmaco?.dosage.startDateTime ? this.hoursList.find(e => e === pharmaco.dosage.startDateTime.time.hours) : null;
		let frequencyHour = null;

		if ((frequencyOption === this.FREQUENCY_OPTION_INTERVAL) && interval && (!this.intervals.some(e => e === interval))) {
			frequencyHour = interval;
			interval = OTHER_FREQUENCY.value;
		}
		const event = pharmaco ? pharmaco.dosage.event : null;

		this.form = this.formBuilder.group({
			dosage: [dosage, Validators.required],
			unit: [unit, Validators.required],
			via: [via, Validators.required],
			hasSolvent: [false],
			dosageSolvent: [null],
			diagnoses: [this.setDefaultMainDiagnosis()?.id, Validators.required],
			foodRelation: [this.NEGATIVE_OPTION],
			patientProvided: [false],
			note: [null],
			frequencyOption: [frequencyOption],
			interval: [interval, this.isRequiredInterval(frequencyOption)],
			startTime: [startTime, this.isRequiredStartDateTime(frequencyOption)],
			frequencyHour: [frequencyHour],
			event: [event, this.isRequiredEvent(frequencyOption)],
			frequency: this.formBuilder.group({
				duration: this.formBuilder.group({
					hours: [null, [Validators.min(0), Validators.max(23)]],
					minutes: [null],
				}),
			}),
		});
	}

	private isRequiredInterval(frequencyOption: number): Validators {
		return frequencyOption === this.FREQUENCY_OPTION_INTERVAL ? Validators.required : [];
	}

	private isRequiredStartDateTime(frequencyOption: number): Validators {
		return (frequencyOption === this.FREQUENCY_OPTION_START_TIME || frequencyOption === this.FREQUENCY_OPTION_INTERVAL) ? Validators.required : [];
	}

	private isRequiredEvent(frequencyOption: number): Validators {
		return frequencyOption === this.FREQUENCY_OPTION_EVENT ? Validators.required : [];
	}

	setDefaultMainDiagnosis(): DiagnosesGeneralStateDto {
		return this.diagnostics.find(e => e.main === true);
	}

	setIndicationDate(d: Date) {
		this.indicationDate = d;
	}

	private removeFormValidators(): void {
		this.form.controls.interval.removeValidators([Validators.required]);
		this.form.controls.interval.updateValueAndValidity();

		this.form.controls.startTime.removeValidators([Validators.required]);
		this.form.controls.startTime.updateValueAndValidity();

		this.form.controls.event.removeValidators([Validators.required]);
		this.form.controls.event.updateValueAndValidity();
	}

	close() {
		const openDialogPharmacosFrequent = false;
		this.dialogRef.close({ openDialogPharmacosFrequent });
	}

	private toSharedSnomedDto(snomed: SnomedDto): SharedSnomedDto {
		return {
			sctid: this.data?.pharmaco ? this.data.pharmaco.snomed.sctid : snomed.sctid,
			pt: this.data?.pharmaco ? this.data.pharmaco.snomed.pt : snomed.pt,
			parentId: "",
			parentFsn: ""
		}
	}

	private toDosageDto(quantity: QuantityDto, periodUnit?: string): NewDosageDto {
		const year = getYear(this.indicationDate);
		const month = getMonth(this.indicationDate);
		const day = this.indicationDate.getDate();
		const startDateTime = this.form.value.startTime;
		return {
			frequency: this.form.controls.interval.value,
			diary: true,
			chronic: true,
			duration: 0,
			periodUnit: periodUnit ? periodUnit : this.loadPeriodUnit(),
			event: this.form.controls?.event.value,
			startDateTime: (startDateTime) ?
				dateToDateTimeDtoUTC(new Date(year, month, day, startDateTime))
				: null,
			quantity: {
				value: quantity.value,
				unit: quantity.unit
			},
		}
	}

	private toPharmacoDto(): PharmacoDto {
		return {
			id: 0,
			patientId: this.data.patientId,
			type: EIndicationType.PHARMACO,
			status: EIndicationStatus.INDICATED,
			professionalId: this.data.professionalId,
			createdBy: null,
			indicationDate: dateToDateDto(this.indicationDate),
			createdOn: null,
			snomed: this.toSharedSnomedDto(this.searchSnomedConcept.pharmacoSnomedConcept),
			dosage: this.toDosageDto({
				unit: this.form.controls.unit.value,
				value: this.form.controls.dosage.value
			}),
			solvent: (this.form.controls.hasSolvent.value && this.searchSnomedConcept?.solventSnomedConcept) ? {
				snomed: this.toSharedSnomedDto(this.searchSnomedConcept?.solventSnomedConcept),
				dosage: this.toDosageDto({
					unit: this.SOLVENT_UNIT,
					value: this.form.controls.dosageSolvent.value
				}, this.DOSAGE)
			} : null,
			healthConditionId: this.form.value.diagnoses,
			foodRelationId: this.form.controls.foodRelation.value,
			patientProvided: this.form.controls.patientProvided.value,
			viaId: this.form.controls?.via.value,
			note: this.form.controls?.note.value,
		}
	}

	private isValidForm(): boolean {
		return this.form.valid && this.searchSnomedConcept.pharmacoForm.valid;
	}

	save() {
		const openDialogPharmacosFrequent = false;
		if (this.isValidForm()) {
			if (this.form.controls.frequencyHour?.value)
				this.form.controls.interval.setValue(this.form.controls.frequencyHour.value);
			const pharmaco = this.toPharmacoDto();
			const pharmacoDate = dateDtoToDate(pharmaco.indicationDate);
			if (!isToday(pharmacoDate) && isSameDay(pharmacoDate, this.data.actualDate)) {
				openConfirmDialog(this.dialog, pharmacoDate).subscribe(confirm => {
					if (confirm) {
						this.dialogRef.close({ openDialogPharmacosFrequent, pharmaco });
					}
				});
			}
			else
				this.dialogRef.close({ openDialogPharmacosFrequent, pharmaco });
		} else {
			this.form.markAllAsTouched();
			this.searchSnomedConcept.solventForm.markAllAsTouched();
			this.searchSnomedConcept.pharmacoForm.markAllAsTouched();
		}
	}

	resetForm(): void {
		this.data.pharmaco = null;
		this.searchSnomedConcept.resetAllForms();
		this.form.reset();
		this.setDefault();
	}

	private setDefault(): void {
		this.form.controls.foodRelation.setValue(this.NEGATIVE_OPTION);
		this.form.controls.patientProvided.setValue(false);
		this.form.controls.frequencyOption.setValue(this.FREQUENCY_OPTION_INTERVAL);
	}

	resetSolventForm(): void {
		this.searchSnomedConcept.resetSolventForm();
		this.form.controls.dosageSolvent.setValue(null);
		this.form.controls.dosageSolvent.removeValidators([Validators.required]);
		this.form.controls.dosageSolvent.updateValueAndValidity();
	}

	clearFilterField(control: AbstractControl): void {
		control.reset();
	}

	private loadPeriodUnit(): string {
		switch (this.form.value.frequencyOption) {
			case (this.FREQUENCY_OPTION_INTERVAL):
				return "h";
			case (this.FREQUENCY_OPTION_START_TIME):
				return "d";
			case (this.FREQUENCY_OPTION_EVENT):
				return "e";
			default:
				return null;
		}
	}

	private getPeriodUnit(frequency: string): number {
		switch (frequency) {
			case ("h"):
				return this.FREQUENCY_OPTION_INTERVAL;
			case ("d"):
				return this.FREQUENCY_OPTION_START_TIME;
			case ("e"):
				return this.FREQUENCY_OPTION_EVENT;
			default:
				return null;
		}
	}


	returnToPharmacosDialog() {
		const openDialogPharmacosFrequent = true;
		this.dialogRef.close({ openDialogPharmacosFrequent });
	}
}

