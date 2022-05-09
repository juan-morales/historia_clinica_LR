import { Component, Input } from '@angular/core';
import { DiagnosticReportInfoDto } from '@api-rest/api-model';
import { HEMOTHERAPY } from '../../constants/internment-studies';

@Component({
	selector: 'app-study-hemotherapy-card',
	templateUrl: './study-hemotherapy-card.component.html',
	styleUrls: ['./study-hemotherapy-card.component.scss']
})
export class StudyHemotherapyCardComponent {

	HEMOTHERAPY = HEMOTHERAPY;
	@Input() hemotherapies: DiagnosticReportInfoDto[];
	@Input() patientId: number;

	constructor() { }

}
