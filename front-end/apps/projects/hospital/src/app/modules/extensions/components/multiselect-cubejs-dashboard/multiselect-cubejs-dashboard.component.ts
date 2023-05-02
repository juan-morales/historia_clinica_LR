import {Component, Input} from '@angular/core';
import {UIComponentDto} from "@extensions/extensions-model";


@Component({
	selector: 'app-multiselect-cubejs-dashboard',
	templateUrl: './multiselect-cubejs-dashboard.component.html',
	styleUrls: ['./multiselect-cubejs-dashboard.component.scss']
})
export class MultiselectCubejsDashboardComponent {

	@Input() content: UIComponentDto[];
	public selectedTab: UIComponentDto[];

	constructor() {
	}

	onChange(category: any) {
		this.selectedTab = category.content;
	}

}
