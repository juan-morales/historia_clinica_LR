import { AfterViewInit, Component } from '@angular/core';
import { NewbornDto, ObstetricEventDto } from '@api-rest/api-model';
import { EBirthCondition } from '@api-rest/api-model';
import { EGender } from '@api-rest/api-model';
import { Subject } from 'rxjs/internal/Subject';
import { ControlDynamicFormService } from '../../services/control-dynamic-form.service';
import { TypeOfPregnancy } from '../type-of-pregnancy/type-of-pregnancy.component';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ObstetricFormService } from '../../services/obstetric-form.service';

@Component({
	selector: 'app-form-dynamic-new-born',
	templateUrl: './form-dynamic-new-born.component.html',
	styleUrls: ['./form-dynamic-new-born.component.scss']
})
export class FormDynamicNewBornComponent implements AfterViewInit {

	BORN_ALIVE = EBirthCondition.BORN_ALIVE;
	FETAL_DEATH = EBirthCondition.FETAL_DEATH;

	FEMALE = EGender.FEMALE;
	MALE = EGender.MALE;
	X = EGender.X;
	selectedOption = 0;
	form: FormGroup;
	disabledAddNewBorn = true;
	formSend = false;

	constructor(
		private controlService: ControlDynamicFormService,
		private formBuilder: FormBuilder,
		private obstetricFormService: ObstetricFormService,
	) {

		this.form = this.formBuilder.group({
			newBorns: new FormArray([])
		});

		this.obstetricFormService.getValue().subscribe((obstetricEvent: ObstetricEventDto) => {
			const array = obstetricEvent?.newborns;
			array?.forEach((newborn: NewbornDto) => {
				this.addChild(newborn);
			});

		});


	}

	ngAfterViewInit(): void {
		this.controlService.selectedOption$.subscribe((option: TypeOfPregnancy) => {
			const newBornsFormArray = this.form.get('newBorns') as FormArray;
			if (option === TypeOfPregnancy.UNDEFINED) {
				newBornsFormArray.clear();
				this.disabledAddNewBorn = true;
			} else {
				this.formSend = false;
				this.form.reset();
				this.disabledAddNewBorn = option === TypeOfPregnancy.SIMPLE;
				const desiredLength = option === TypeOfPregnancy.SIMPLE ? 1 : 2;
				while (newBornsFormArray.length > desiredLength) {
					newBornsFormArray.removeAt(newBornsFormArray.length - 1);
				}
				while (newBornsFormArray.length < desiredLength) {
					this.addChild();
				}
			}
		});
	}

	addChild(newBorn?: NewbornDto) {
		const arrayBorns = this.form.get('newBorns') as FormArray;
		const addNewBorn = this.addNewBorn(newBorn);
		arrayBorns.push(addNewBorn);
	}

	delete(i: number) {
		const arrayBorns = this.form.get('newBorns') as FormArray;
		arrayBorns.removeAt(i);
	}

	getCtrl(key: string, form: FormGroup): any {
		return form.get(key);
	}

	isValidForm(): boolean {
		this.formSend = true;
		this.form.markAllAsTouched();
		return this.form.valid
	}

	getForm(): NewbornDto[] {
		this.form.markAllAsTouched();
		const array = this.form.get('newBorns') as FormArray;
		const newborns = array?.getRawValue();
		return this.form.valid ? newborns : null
	}

	private addNewBorn(newBorn?: NewbornDto): FormGroup {
		return this.formBuilder.group({
			birthConditionType: new FormControl(newBorn?.birthConditionType || null, Validators.required),
			weight: new FormControl(newBorn?.weight || null, Validators.required),
			genderId: new FormControl(newBorn?.genderId || null, Validators.required),
			id: new FormControl(newBorn?.id || null)
		});
	}

}