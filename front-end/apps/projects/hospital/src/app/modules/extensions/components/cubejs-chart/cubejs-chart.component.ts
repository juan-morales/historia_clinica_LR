import { Component, Input, OnDestroy } from '@angular/core';
import { ReplaySubject, Subscription } from 'rxjs';
import { ChartDefinitionService } from '@extensions/services/chart-definition.service';
import { ChartDefinitionDto, UIComponentDto } from '@extensions/extensions-model';


const toUIComponentDto = (error: any): UIComponentDto => ({
	type: 'json',
	args: {
		content: error
	}
});
@Component({
	selector: 'app-cubejs-chart',
	templateUrl: './cubejs-chart.component.html',
	styleUrls: ['./cubejs-chart.component.scss']
})
export class CubejsChartComponent implements OnDestroy {

	@Input() dateFormat?: string;
	@Input() showLegend?: true;
	error: UIComponentDto = undefined;
	chartType = new ReplaySubject<any>(1);
	cubeQuery = new ReplaySubject<any>(1);
	pivotConfig = new ReplaySubject<any>(1);
	chartTitle: string;
	enableFilter = false;

	@Input() listOnTab: string = null;

	private chartDefinitionSubscription: Subscription;

	constructor(
		private chartDefinitionService: ChartDefinitionService,
	) { }

	@Input()
	set query(queryName: string) {
		if (!queryName) {
			console.warn('Chart with undefined queryStream');
			return;
		}
		this.chartDefinitionSubscription = this.chartDefinitionService.queryStream$(queryName).subscribe(
			(queryStream: ChartDefinitionDto) => {
				this.error = undefined;
				this.chartType.next(queryStream.chartType);
				this.cubeQuery.next(queryStream.cubeQuery);
				this.pivotConfig.next(queryStream.pivotConfig);
				this.cleanFilters(queryStream);
			},
			(error: any) => this.error = toUIComponentDto(error),
		)
		this.setChartTitle(queryName);
	}

	ngOnDestroy() {
		this.chartDefinitionSubscription?.unsubscribe();
	}

	setChartTitle(queryName: string) {
		switch (queryName) {
			case 'cantidadConsultasAmbulatorias': {
				this.chartTitle = 'Evolución de consultas del año actual'
				break;
			}
			case 'cantidadConsultasPorEspecialidad': {
				this.chartTitle = 'Consultas por especialidad'
				this.enableFilter = true;
				break;
			}
			case 'cantidadConsultasAmbulatoriasEspecialidadProfesional': {
				this.chartTitle = 'Consultas por especialidad y profesional del último trimestre'
				break;
			}
			case 'cantidadTurnos': {
				this.chartTitle = 'Evolución de turnos del año actual'
				break;
			}
			case 'cantidadTurnosPorEspecialidad': {
				this.chartTitle = 'Turnos por especialidad del año actual'
				break;
			}
			case 'cantidadTurnosPorProfesional': {
				this.chartTitle = 'Turnos por profesional del año actual'
				break;
			}
		}
	}

	cleanFilters(queryStream) {
		if (!this.enableFilter)
			queryStream.cubeQuery.filters = [];
	}
}
