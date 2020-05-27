import { Component, Input, OnInit } from '@angular/core';
import { VACUNAS } from '../../constants/summaries';
import { TableModel } from '@presentation/components/table/table.component';
import { InmunizationDto } from '@api-rest/api-model';
import { InternmentStateService } from '@api-rest/services/internment-state.service';
import { DateFormat, momentFormat, momentParseDate } from '@core/utils/moment.utils';

@Component({
  selector: 'app-vacunas-summary',
  templateUrl: './vacunas-summary.component.html',
  styleUrls: ['./vacunas-summary.component.scss']
})
export class VacunasSummaryComponent implements OnInit {

	@Input() internmentEpisodeId: number;

	public readonly vacunasSummary = VACUNAS;

	tableModel: TableModel<InmunizationDto>;

	constructor(
		private internmentStateService: InternmentStateService
	) {
	}

	ngOnInit(): void {
		this.internmentStateService.getInmunizations(this.internmentEpisodeId).subscribe(
			data => this.tableModel = this.buildTable(data)
		);
	}

	private buildTable(data: InmunizationDto[]): TableModel<InmunizationDto> {
		return {
			columns: [
				{
					columnDef: 'vacuna',
					header: 'Vacuna',
					text: (row) => row.snomed.pt
				},
				{
					columnDef: 'fecha',
					header: 'Fecha',
					text: (row) => momentFormat(momentParseDate(row.administrationDate), DateFormat.VIEW_DATE)
				}
			],
			data
		};
	}

}
