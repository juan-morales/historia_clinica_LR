import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatRadioChange } from '@angular/material/radio';

@Component({
	selector: 'app-search-criteria',
	templateUrl: './search-criteria.component.html',
	styleUrls: ['./search-criteria.component.scss']
})
export class SearchCriteriaComponent implements OnInit {

	form: UntypedFormGroup;
	readonly searchCriteria = SearchCriteria;
	@Input() label: string;
	@Input() searchCriteryStyle?: string;
	@Output() selectedOption = new EventEmitter<SearchCriteria>();

	constructor(
		private readonly formBuilder: UntypedFormBuilder,
	) { }

	ngOnInit() {

		this.form = this.formBuilder.group({
			criteria: new UntypedFormControl(SearchCriteria.CONSULTATION),
		});

	}

	emit(searchCriteriaValue: MatRadioChange) {
		this.selectedOption.emit(searchCriteriaValue.value);
	}

}

export enum SearchCriteria {
	CONSULTATION,
	PRACTICES
}


