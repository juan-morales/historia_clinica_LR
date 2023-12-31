import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import {
	DateTimeDto, MasterDataInterface,
	NewDosageDto,
	OtherPharmacoDto,
	ParenteralPlanDto,
	QuantityDto,
	SharedSnomedDto,
	SnomedDto,
	TimeDto
} from "@api-rest/api-model";
import { EIndicationStatus, EIndicationType } from "@api-rest/api-model";
import { dateDtoToDate, dateToDateDto, dateToDateTimeDtoUTC } from "@api-rest/mapper/date-dto.mapper";
import { InternacionMasterDataService } from "@api-rest/services/internacion-master-data.service";
import { SnowstormService } from "@api-rest/services/snowstorm.service";
import { getError, hasError } from '@core/utils/form.utils';
import { isNumberOrDot } from '@core/utils/pattern.utils';
import { HOURS_LIST, openConfirmDialog } from "@historia-clinica/modules/ambulatoria/modules/indicacion/constants/internment-indications";
import { SearchSnomedConceptsParenteralPlanService } from "@historia-clinica/modules/ambulatoria/modules/indicacion/services/search-snomed-concepts-parenteral-plan.service";
import { SnomedService } from "@historia-clinica/services/snomed.service";
import { SnackBarService } from "@presentation/services/snack-bar.service";
import { getMonth, getYear, isSameDay, isToday } from "date-fns";

@Component({
	selector: 'app-parenteral-plan',
	templateUrl: './parenteral-plan.component.html',
	styleUrls: ['./parenteral-plan.component.scss'],
})
export class ParenteralPlanComponent implements OnInit {

	parenteralPlanForm: UntypedFormGroup;
	searchSnomedConcept: SearchSnomedConceptsParenteralPlanService;
	indicationDate: Date;
	vias: MasterDataInterface<number>[] = [];
	hasError = hasError;
	getError = getError;
	readonly isNumberOrDot = isNumberOrDot;

	HOURS_LIST = HOURS_LIST;

	constructor(
		@Inject(MAT_DIALOG_DATA) public data: { entryDate: Date, actualDate: Date, patientId: number, professionalId: number, parenteralPlan?: ParenteralPlanDto },
		private readonly dialogRef: MatDialogRef<ParenteralPlanComponent>,
		private readonly formBuilder: UntypedFormBuilder,
		private readonly snowstormService: SnowstormService,
		private readonly snomedService: SnomedService,
		private readonly snackBarService: SnackBarService,
		private readonly internacionMasterdataService: InternacionMasterDataService,
		private readonly dialog: MatDialog,
	) {
		this.searchSnomedConcept = new SearchSnomedConceptsParenteralPlanService(this.formBuilder, this.snowstormService, this.snomedService, this.snackBarService);
	}

	ngOnInit(): void {
		this.indicationDate = this.data.actualDate;
		this.internacionMasterdataService.getVias().subscribe(v => this.vias = v);
		const pharmaco = this.data?.parenteralPlan;
		const dosage = pharmaco ? pharmaco.dosage.quantity.value : null;
		const via = pharmaco ? this.data?.parenteralPlan.via : null;

		this.parenteralPlanForm = this.formBuilder.group({
			volumen: [dosage, [Validators.required]],
			via: [via],
			frequency: this.formBuilder.group({
				duration: this.formBuilder.group({
					hours: [null, [Validators.min(0), Validators.max(23)]],
					minutes: [null, [Validators.max(59)]],
				}),
				flow: this.formBuilder.group({
					velocity: [null, [Validators.required]],
					drops: [null, [Validators.required]],
				}),
				volumen: [null],
			}),
			startTime: [null],
		});

		if (this.data?.parenteralPlan)
			this.searchSnomedConcept.setForm(this.data?.parenteralPlan.snomed);
	}

	clear(control: AbstractControl): void {
		control.reset();
	}

	resetForm(): void {
		this.searchSnomedConcept.resetAllForms();
		this.parenteralPlanForm.reset();
	}

	save() {
		const formsValid = this.parenteralPlanForm.valid && this.searchSnomedConcept.pharmacoForm.valid && this.searchSnomedConcept.salineForm.valid;
		if (formsValid) {
			const parenteralPlanDto = this.toParenteralPlanDto();
			const parenteralIndicationDate = dateDtoToDate(parenteralPlanDto.indicationDate);
			const response = { openDialogPharmacosFrequent:false, pharmaco:parenteralPlanDto };
			if (!isToday(parenteralIndicationDate) && isSameDay(parenteralIndicationDate, this.data.actualDate)) {
				openConfirmDialog(this.dialog, parenteralIndicationDate).subscribe(confirm => {
					if (confirm) {
						this.dialogRef.close(response);
					}
				});
			}
			else
				this.dialogRef.close(response);
		}
		else {
			this.parenteralPlanForm.markAllAsTouched();
			this.searchSnomedConcept.salineForm.markAllAsTouched();
			this.searchSnomedConcept.pharmacoForm.markAllAsTouched();
		}
	}

	close() {
		const openDialogPharmacosFrequent = false;
		this.dialogRef.close({ openDialogPharmacosFrequent });
	}

	private getFrequencyForm(): UntypedFormGroup {
		return <UntypedFormGroup>this.parenteralPlanForm.controls.frequency
	}

	private getDurationForm(): UntypedFormGroup {
		return <UntypedFormGroup>this.getFrequencyForm().controls.duration
	}

	private getFlowForm(): UntypedFormGroup {
		return <UntypedFormGroup>this.getFrequencyForm().controls.flow
	}

	toParenteralPlanDto(): ParenteralPlanDto {
		return {
			id: 0,
			createdBy: null,
			createdOn: null,
			indicationDate: dateToDateDto(this.indicationDate),
			patientId: this.data.patientId,
			professionalId: this.data.professionalId,
			status: EIndicationStatus.INDICATED,
			type: EIndicationType.PARENTERAL_PLAN,
			dosage: this.toNewDosageDto(this.parenteralPlanForm.value.volumen, this.parenteralPlanForm.value.startTime),
			frequency: {
				id: 0,
				duration: this.getDurationForm() ? this.toTimeDto() : null,
				flowDropsHour: this.getFlowForm().value.drops,
				flowMlHour: this.getFlowForm().value.velocity,
				dailyVolume: this.getFrequencyForm().value.volumen
			},
			snomed: this.toSharedSnomedDto(this.searchSnomedConcept.salineSnomedConcept),
			via: this.parenteralPlanForm.value.via,
			pharmacos: this.loadPharmacos()
		}
	}

	toNewDosageDto(quantityValue: number, startTime?: number): NewDosageDto {
		return {
			chronic: false,
			diary: false,
			startDateTime: setStartTime(this.indicationDate),
			quantity: this.toQuantityDto(quantityValue),
			periodUnit: startTime ? "d" : null
		}

		function setStartTime(indicationDate: Date): DateTimeDto {
			if (startTime) {
				const year = getYear(indicationDate);
				const month = getMonth(indicationDate);
				const day = indicationDate.getDate();
				return dateToDateTimeDtoUTC(new Date(year, month, day, startTime));
			}
			return null;
		}
	}

	toQuantityDto(quantityValue: number): QuantityDto {
		return { unit: 'ml', value: quantityValue }
	}

	toSharedSnomedDto(snomed: SnomedDto): SharedSnomedDto {
		return {
			parentFsn: "",
			parentId: "",
			pt: snomed.pt,
			sctid: snomed.sctid
		}
	}

	setIndicationDate(d: Date) {
		this.indicationDate = d;
	}

	toTimeDto(): TimeDto {
		return {
			hours: this.getDurationForm().value.hours,
			minutes: this.getDurationForm().value.minutes
		}
	}

	loadPharmacos(): OtherPharmacoDto[] {
		const pharmacos: UntypedFormArray = this.searchSnomedConcept.pharmacos;
		const otherPharmaco: OtherPharmacoDto[] = [];
		for (let i = 0; i < pharmacos.length; i++) {
			const snomedForm: UntypedFormGroup = this.searchSnomedConcept.getSnomed(i);
			const snomed = { pt: snomedForm.value.pt, sctid: snomedForm.value.sctid };
			otherPharmaco.push({
				dosage: this.toNewDosageDto(pharmacos.at(i).value.dose),
				snomed: this.toSharedSnomedDto(snomed)
			})
		}
		return otherPharmaco
	}

	returnToMostFrequentDialog(){
		const openDialogPharmacosFrequent = true;
		this.dialogRef.close({ openDialogPharmacosFrequent });
	}
}
