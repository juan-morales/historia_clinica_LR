import { DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DatePipeFormat } from '@core/utils/date.utils';

@Component({
	selector: 'app-worklist',
	templateUrl: './worklist.component.html',
	styleUrls: ['./worklist.component.scss']
})
export class WorklistComponent {

	datePipeFormat = DatePipeFormat;

	@Input() worklist: Worklist;

	constructor(
		readonly datePipe: DatePipe,
	) { }

}

export interface Worklist {
	patientInformation: PatientInformation;
	state: State;
	date: Date;
	appointmentId: number;
}

interface PatientInformation {
	fullName: string;
	identification: string;
}

export interface State {
	description: string;
	color: string;
}