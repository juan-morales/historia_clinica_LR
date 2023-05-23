import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AllergyConditionDto, SnomedDto } from '@api-rest/api-model';
import { SnomedECL } from '@api-rest/api-model';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { pushTo, removeFrom } from '@core/utils/array.utils';
import { SnomedSemanticSearch, SnomedService } from '@historia-clinica/services/snomed.service';
import { ComponentEvaluationManagerService } from '../../../../services/component-evaluation-manager.service';

@Component({
	selector: 'app-alergias',
	templateUrl: './alergias.component.html',
	styleUrls: ['./alergias.component.scss']
})
export class AlergiasComponent implements OnInit {

	private allergiesValue: AllergyConditionDto[];

	@Output() allergiesChange = new EventEmitter();
	@Input() showTitle = false;
	@Input()
	set allergies(allergies: AllergyConditionDto[]) {
		this.allergiesValue = allergies;
		this.allergiesChange.emit(this.allergiesValue);
	}

	get allergies(): AllergyConditionDto[] {
		return this.allergiesValue;
	}

	snomedConcept: SnomedDto;
	form: UntypedFormGroup;

	// Mat table
	columns = [
		{
			def: 'problemType',
			header: 'internaciones.anamnesis.alergias.table.columns.ALLERGY',
			text: a => a.snomed.pt
		}
	];
	displayedColumns: string[] = [];

	constructor(
		private formBuilder: UntypedFormBuilder,
		private snomedService: SnomedService,
		private readonly componentEvaluationManagerService: ComponentEvaluationManagerService,
	) {
		this.displayedColumns = this.columns?.map(c => c.def).concat(['remove']);
	}

	ngOnInit(): void {
		this.form = this.formBuilder.group({
			snomed: [null, Validators.required]
		});
	}

	addToList() {
		if (this.form.valid && this.snomedConcept) {
			const alergia: AllergyConditionDto = {
				categoryId: null,
				date: null,
				verificationId: null,
				id: null,
				snomed: this.snomedConcept,
				criticalityId: null,
				statusId: null
			};
			this.add(alergia);
			this.resetForm();
		}
	}

	setConcept(selectedConcept: SnomedDto): void {
		this.snomedConcept = selectedConcept;
		const pt = selectedConcept ? selectedConcept.pt : '';
		this.form.controls.snomed.setValue(pt);
	}

	resetForm(): void {
		delete this.snomedConcept;
		this.form.reset();
	}

	add(a: AllergyConditionDto): void {
		this.allergies = pushTo<AllergyConditionDto>(this.allergies, a);
		this.componentEvaluationManagerService.allergies = this.allergies;
	}

	remove(index: number): void {
		this.allergies = removeFrom<AllergyConditionDto>(this.allergies, index);
		this.componentEvaluationManagerService.allergies = this.allergies;
	}

	openSearchDialog(searchValue: string): void {
		if (searchValue) {
			const search: SnomedSemanticSearch = {
				searchValue,
				eclFilter: SnomedECL.ALLERGY
			};
			this.snomedService.openConceptsSearchDialog(search)
				.subscribe((selectedConcept: SnomedDto) => this.setConcept(selectedConcept));
		}
	}

}
