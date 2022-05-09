import { Component, Input } from '@angular/core';
import { DiagnosticReportInfoDto } from '@api-rest/api-model';
import { PATHOLOGIC_ANATOMY } from '../../constants/internment-studies';

@Component({
	selector: 'app-study-pathologic-anatomy-card',
	templateUrl: './study-pathologic-anatomy-card.component.html',
	styleUrls: ['./study-pathologic-anatomy-card.component.scss']
})
export class StudyPathologicAnatomyCardComponent {

	PATHOLOGIC_ANATOMY = PATHOLOGIC_ANATOMY;
	@Input() pathologicAnatomies: DiagnosticReportInfoDto[];
	@Input() patientId: number;

	constructor() { }

}
