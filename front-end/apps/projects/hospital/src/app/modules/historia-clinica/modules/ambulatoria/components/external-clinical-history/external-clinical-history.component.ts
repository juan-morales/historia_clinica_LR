import { Component, Input, OnInit } from '@angular/core';
import { DateDto, ExternalClinicalHistorySummaryDto } from '@api-rest/api-model';

@Component({
	selector: 'app-external-clinical-history',
	templateUrl: './external-clinical-history.component.html',
	styleUrls: ['./external-clinical-history.component.scss']
})
export class ExternalClinicalHistoryComponent implements OnInit {

	@Input() externalClinicalHistory: ExternalClinicalHistorySummaryDto;
	public DATE_TO_VIEW: DateDto;

	constructor() { }

	ngOnInit(): void {
		this.DATE_TO_VIEW = this.externalClinicalHistory.consultationDate;
	}

}
