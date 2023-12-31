import { Component, Input } from '@angular/core';
import { UIComponentDto } from '@extensions/extensions-model';

@Component({
	selector: 'app-ui-card',
	templateUrl: './ui-card.component.html',
	styleUrls: ['./ui-card.component.scss']
})
export class UiCardComponent {
	@Input() mode = '';
	@Input() title;
	@Input() content: UIComponentDto[];

	constructor() { }

	get cardClass(): string {
		return this.mode || 'flat';
	}
}
