import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClinicalSpecialtyDto, HealthcareProfessionalCompleteDto, HealthcareProfessionalSpecialtyDto, ProfessionalProfessionsDto, ProfessionalSpecialtyDto } from '@api-rest/api-model';
import { hasError } from '@core/utils/form.utils';

@Component({
	selector: 'app-edit-prefessions-specialties',
	templateUrl: './edit-prefessions-specialties.component.html',
	styleUrls: ['./edit-prefessions-specialties.component.scss']
})
export class EditPrefessionsSpecialtiesComponent implements OnInit {
	form: UntypedFormGroup;
	hasError = hasError;
	confirmationValidation: boolean = false;
	ownProfessionsAndSpecialties: ProfessionalProfessionsDto[] = null;
	constructor(
		@Inject(MAT_DIALOG_DATA) public data: { personId: number; specialtyId?: number[], id?: number, allSpecialties: ClinicalSpecialtyDto[], allProfessions: ProfessionalSpecialtyDto[], ownProfessionsAndSpecialties: ProfessionalProfessionsDto[] },

		public dialog: MatDialogRef<EditPrefessionsSpecialtiesComponent>,
		private changeDetectorRef: ChangeDetectorRef,
		private formBuilder: UntypedFormBuilder,

	) { }

	ngOnInit(): void {
		this.ownProfessionsAndSpecialties = this.data.ownProfessionsAndSpecialties.map(e => e);

		this.form = this.formBuilder.group({
			professionalProfessions: new UntypedFormArray([])
		});

		this.setCombos(this.data.ownProfessionsAndSpecialties?.length);

		this.changeDetectorRef.detectChanges();
	}

	private setCombos(comboUnits: number) {
		if (comboUnits === 0)
			this.addCombo();
		else {
			while (comboUnits > 0) {
				this.addCombo();
				comboUnits = comboUnits - 1;
			}
		}
	}

	add(): UntypedFormGroup {
		return new UntypedFormGroup({
			combo: new UntypedFormControl(null, [Validators.required]),
		});
	}

	addCombo(): void {
		const array = this.form.get('professionalProfessions') as UntypedFormArray;
		array.push(this.add());
	}

	getCtrl(key: string, form: UntypedFormGroup): any {
		return form.get(key);
	}

	isDisableConfirmButton(): boolean {
		const array = this.form.get('professionalProfessions') as UntypedFormArray;
		return array.at(array.length - 1)?.value.combo === null;
	}

	save(): void {
		this.confirmationValidation = !this.form.valid;
		if (this.form.valid) {
			const professional: HealthcareProfessionalCompleteDto = this.buildCreateProfessionalDto();
			this.dialog.close(professional);
		}
	}

	private filterEmtySpecialty(array: ProfessionalProfessionsDto[]): ProfessionalProfessionsDto[] {
		array.forEach(
			pp => {
				pp.specialties = this.updateSpecialties(pp.specialties)
			}
		)
		return array;
	}

	private updateSpecialties(specialties: HealthcareProfessionalSpecialtyDto[]): HealthcareProfessionalSpecialtyDto[] {

		return !specialties[specialties.length - 1].clinicalSpecialty.id ?
			specialties.slice(0,specialties.length - 1) : specialties
	}

	deleteCombo(pointIndex: number): void {
		const array = this.form.get('professionalProfessions') as UntypedFormArray;
		array.removeAt(pointIndex);
	}

	private buildCreateProfessionalDto(): HealthcareProfessionalCompleteDto {
		const array = this.form.get('professionalProfessions') as UntypedFormArray;
		const refArray = array.value;
		return {
			id: null,
			personId: (this.data?.personId) ? (this.data?.personId) : null,
			professionalProfessions: this.filterEmtySpecialty(refArray.map(e => e.combo))
		}
	}

}
