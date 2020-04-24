import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SnomedDto, HealthHistoryConditionDto, MasterDataInterface } from '@api-rest/api-model';
import { MatTableDataSource } from '@angular/material/table';
import { InternacionMasterDataService } from '@api-rest/services/internacion-master-data.service';
import { DatePipe } from '@angular/common';

@Component({
	selector: 'app-antecentes-personales',
	templateUrl: './antecentes-personales.component.html',
	styleUrls: ['./antecentes-personales.component.scss']
})
export class AntecentesPersonalesComponent implements OnInit {

	current: any = {};
	form: FormGroup;
	today: Date = new Date();
	conditionsVerification: MasterDataInterface<string>[] = [{id: 'a', description: 'description a'}, {id: 'b', description: 'description b'}, {id: 'c', description: 'description c'}];
	conditionsClinicalStatus: MasterDataInterface<string>[] = [{id: 'a', description: 'description a'}, {id: 'b', description: 'description b'}, {id: 'c', description: 'description c'}];

	//Mat table
	columns = [
		{
			def: "problemType",
			header: 'internaciones.anamnesis.antecedentes-personales.table.columns.PROBLEM_TYPE',
			text: ap => ap.snomed.fsn
		},
		{
			def: 'date',
			header: 'internaciones.anamnesis.antecedentes-personales.table.columns.REGISTRY_DATE',
			text: ap => this.datePipe.transform(ap.date, 'dd/MM/yyyy')
		},
	];
	displayedColumns: string[] = [];
	apDataSource = new MatTableDataSource<any>([]);

	constructor(
		private formBuilder: FormBuilder,
		private datePipe: DatePipe,
		private internacionMasterDataService: InternacionMasterDataService
	)
	{
		this.displayedColumns = this.columns?.map(c => c.def).concat(['remove']);
	}

	ngOnInit(): void {
		this.form = this.formBuilder.group({
			date: [null, Validators.required],
			note: [null, Validators.required],
			verificationId: [null, Validators.required],
			statusId: [null, Validators.required],
			snomed: [null, Validators.required]
		});

		/*this.internacionMasterDataService.getHealthClinical().subscribe(healthClinical => {
			this.conditionsClinicalStatus = healthClinical;
		});

		this.internacionMasterDataService.getHealthVerification().subscribe(healthVerification => {
			this.conditionsVerification = healthVerification;
		});*/
	}

	chosenYearHandler(newDate: Date) {
		if (this.form.controls.date.value !== null) {
			const ctrlDate: Date = this.form.controls.date.value;
			ctrlDate.setFullYear(newDate.getFullYear());
			this.form.controls.date.setValue(ctrlDate);
		} else {
			this.form.controls.date.setValue(newDate);
		}
	}

	chosenMonthHandler(newDate: Date) {
		if (this.form.controls.date.value !== null) {
			const ctrlDate: Date = this.form.controls.date.value;
			ctrlDate.setMonth(newDate.getMonth());
			this.form.controls.date.setValue(ctrlDate);
		} else {
			this.form.controls.date.setValue(newDate);
		}
	}

	save() {
		console.log(this.form.value);
		let aux: HealthHistoryConditionDto = this.form.value;
		console.log('HHC', aux);
		this.add(aux);
	}

	setConcept(selectedConcept: SnomedDto): void {
		this.current.snomed = selectedConcept;
		this.form.controls.snomed.setValue(selectedConcept);
	}

	add(ap): void {
		// TODO validacion snomed requerido

		// had to use an assignment instead of push method to produce a change on the variable observed by mat-table (apDataSource)
		this.apDataSource.data = this.apDataSource.data.concat([ap]);
		this.current = {};
	}

	remove(ap: any): void {
		this.apDataSource.data = this.apDataSource.data.filter(_ap => _ap !== ap);
	}

}
