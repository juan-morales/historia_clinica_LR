import {Component, Injector, Input, OnInit} from '@angular/core';
import {HCEPersonalHistoryDto} from '@api-rest/api-model';
import {SummaryHeader} from '@presentation/components/summary-card/summary-card.component';
import {InternacionMasterDataService} from '@api-rest/services/internacion-master-data.service';
import { AmbulatoriaSummaryFacadeService } from '@historia-clinica/modules/ambulatoria/services/ambulatoria-summary-facade.service';
import { Observable } from 'rxjs';
import { DockPopupRef } from '@presentation/services/dock-popup-ref';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '@presentation/dialogs/confirm-dialog/confirm-dialog.component';
import { ActivatedRoute } from '@angular/router';
import { DockPopupService } from '@presentation/services/dock-popup.service';
import { NuevaConsultaDockPopupComponent } from '@historia-clinica/modules/ambulatoria/dialogs/nueva-consulta-dock-popup/nueva-consulta-dock-popup.component';
import { SolveProblemComponent } from '@historia-clinica/dialogs/solve-problem/solve-problem.component';

@Component({
	selector: 'app-antecedentes-personales-summary',
	templateUrl: './antecedentes-personales-summary.component.html',
	styleUrls: ['./antecedentes-personales-summary.component.scss']
})
export class AntecedentesPersonalesSummaryComponent implements OnInit{

	readonly MILD_SEVERITY:string = 'LA6752-5';
	readonly MODERATE_SEVERITY:string = 'LA6751-7';
	readonly SEVERE_SEVERITY:string = 'LA6750-9';

	problems: Problem[] = [];
	hasNewConsultationEnabled$: Observable<boolean>;
	private patientId: number;
	private severityTypesMasterData: any[] = [];
	private nuevaConsultaAmbulatoriaRef: DockPopupRef;
	private nuevaConsultaFromProblemaRef: DockPopupRef;
	private dockPopupService: DockPopupService;
	@Input() personalHistoriesHeader: SummaryHeader;
	@Input()
	set personalHistory(personalHistory: HCEPersonalHistoryDto[]){
		if (personalHistory?.length){
			this.setSeverityMasterData(personalHistory);
			this.setProblems(personalHistory);
		}
	};

	constructor(
		private readonly internacionMasterDataService: InternacionMasterDataService,
		private ambulatoriaSummaryFacadeService: AmbulatoriaSummaryFacadeService,
		public dialog: MatDialog,
		private route: ActivatedRoute,
		private injector: Injector
	) {
		this.dockPopupService = this.injector.get<DockPopupService>(DockPopupService);
		this.route.paramMap.subscribe(
			(params) => this.patientId = Number(params.get('idPaciente')));
	}

	ngOnInit(): void {
		this.hasNewConsultationEnabled$ = this.ambulatoriaSummaryFacadeService.hasNewConsultationEnabled$;
	}

	getSeverityTypeDisplayByCode(severityCode): string {
		return (severityCode && this.severityTypesMasterData.length) ?
			this.severityTypesMasterData.find(severityType => severityType.code === severityCode).display
			: '';
	}

	getSeverityColor(severityCode): string {
		switch(severityCode) {
			case this.MILD_SEVERITY:
				return 'grey';
			case this.MODERATE_SEVERITY:
				return 'black';
			case this.SEVERE_SEVERITY:
				return 'warn';
		}
	}


	openNuevaConsulta(problem: Problem): void {
		if (!this.nuevaConsultaFromProblemaRef) {
			if (!this.nuevaConsultaAmbulatoriaRef) {
				this.openDockPopup(problem.data.id);
			} else {
				const confirmDialog = this.dialog.open(ConfirmDialogComponent, { data: getConfirmDataDialog() });
				confirmDialog.afterClosed().subscribe(confirmed => {
					if (confirmed) {
						this.openDockPopup(problem.data.id);
						this.nuevaConsultaAmbulatoriaRef.close();
					}
				});
			}
		}

		function getConfirmDataDialog() {
			const keyPrefix = 'ambulatoria.paciente.problemas.nueva_opened_confirm_dialog';
			return {
				title: `${keyPrefix}.TITLE`,
				content: `${keyPrefix}.CONTENT`,
				okButtonLabel: `${keyPrefix}.OK_BUTTON`,
				cancelButtonLabel: `${keyPrefix}.CANCEL_BUTTON`,
			};
		}
	}

	solveProblemPopUp(problem: Problem) {
		this.dialog.open(SolveProblemComponent, {
			data: {
				problema: problem.data,
				patientId: this.patientId
			}
		}).afterClosed().subscribe(submitted => {
			if (submitted) {
				this.ambulatoriaSummaryFacadeService.setFieldsToUpdate({ personalHistories: true, problems: true });
			}
		});
	}

	private openDockPopup(idProblema: number) {
		this.nuevaConsultaFromProblemaRef =
			this.dockPopupService.open(NuevaConsultaDockPopupComponent, { idPaciente: this.patientId, idProblema });
		this.nuevaConsultaFromProblemaRef.afterClosed().subscribe(fieldsToUpdate => {
			if (fieldsToUpdate) {
				this.ambulatoriaSummaryFacadeService.setFieldsToUpdate(fieldsToUpdate);
			}
			delete this.nuevaConsultaFromProblemaRef;
		});
	}

	private setProblems(problems: HCEPersonalHistoryDto[]){
		this.problems = problems.map(prob => {
			return this.mapToProblem(prob);
		});
	}

	private setSeverityMasterData(problems: HCEPersonalHistoryDto[]){
		if (!this.severityTypesMasterData.length){
			this.internacionMasterDataService.getHealthSeverity().subscribe(
				severityTypes => {
					this.severityTypesMasterData = severityTypes;
					this.setProblems(problems);
				}
			);
		}
	}

	private mapToProblem(problem: HCEPersonalHistoryDto): Problem {
		return {
			data: problem,
			severityName: this.getSeverityTypeDisplayByCode(problem.severity),
			severityColor: this.getSeverityColor(problem.severity)
		}
	}
}

interface Problem {
	data: HCEPersonalHistoryDto;
	severityName: string;
	severityColor: string;
}
