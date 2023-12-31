import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ADVICE } from '../../constants/internment-studies';
import { DiagnosticReportInfoDto } from '@api-rest/api-model';

@Component({
	selector: 'app-study-advice-card',
	templateUrl: './study-advice-card.component.html',
	styleUrls: ['./study-advice-card.component.scss']
})
export class StudyAdviceCardComponent {

	ADVICE = ADVICE;
	@Input() advices: DiagnosticReportInfoDto[];
	@Input() patientId: number;
	@Input() categoryId: string;
	@Output() updateCurrentReportsEventEmitter = new EventEmitter<string>();

	constructor() { }

	notifyUpdateToParent() {
		this.updateCurrentReportsEventEmitter.emit(this.categoryId);
	}

}
