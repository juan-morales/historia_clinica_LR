import { Component, ViewChild } from '@angular/core';
import { NewbornDto, ObstetricEventDto } from '@api-rest/api-model';
import { ObstetricFormService } from '../../services/obstetric-form.service';
import { FormDynamicNewBornComponent } from '../form-dynamic-new-born/form-dynamic-new-born.component';

@Component({
	selector: 'app-obstetric',
	templateUrl: './obstetric.component.html',
	styleUrls: ['./obstetric.component.scss']
})
export class ObstetricComponent {

	obstetricEvent: ObstetricEventDto;
	newborns: NewbornDto[];
	@ViewChild(FormDynamicNewBornComponent) formulario!: FormDynamicNewBornComponent;

	constructor(
		readonly obstetricFormService: ObstetricFormService,
	) { }

	setObstetricEvent(obstetricEvent: ObstetricEventDto) {
		this.obstetricEvent = obstetricEvent;
		this.obstetricFormService.obstetric = obstetricEvent;
	}

	getForm(): ObstetricEventDto {
		const form = { ...this.obstetricEvent, newborns: this.formulario.getForm() };
		return form
	}

	isValidForm(): boolean {
		return this.formulario.isValidForm();
	}

}
