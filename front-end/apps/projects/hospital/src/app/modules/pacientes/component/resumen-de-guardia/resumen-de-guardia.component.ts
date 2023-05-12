import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ERole } from '@api-rest/api-model.d';
import { ResponseEmergencyCareDto, TriageListDto } from '@api-rest/api-model.d';
import { EmergencyCareEpisodeService } from '@api-rest/services/emergency-care-episode.service';
import { TriageService } from '@api-rest/services/triage.service';
import { PatientNameService } from '@core/services/patient-name.service';
import { TriageCategory } from '@historia-clinica/modules/guardia/components/triage-chip/triage-chip.component';
import { GuardiaMapperService } from '@historia-clinica/modules/guardia/services/guardia-mapper.service';
import { GUARDIA } from '@historia-clinica/constants/summaries';
import { SummaryHeader } from '@presentation/components/summary-card/summary-card.component';
import { RiskFactorFull, Triage } from '@historia-clinica/modules/guardia/components/triage-details/triage-details.component';
import { EmergencyCareTypes, EstadosEpisodio } from '@historia-clinica/modules/guardia/constants/masterdata';
import { MatDialog } from '@angular/material/dialog';
import { EpisodeStateService } from '@historia-clinica/modules/guardia/services/episode-state.service';
import { SelectConsultorioComponent } from '@historia-clinica/modules/guardia/dialogs/select-consultorio/select-consultorio.component';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { ContextService } from '@core/services/context.service';
import { Router } from '@angular/router';
import { EmergencyCareEpisodeStateService } from '@api-rest/services/emergency-care-episode-state.service';
import { ConfirmDialogComponent } from '@presentation/dialogs/confirm-dialog/confirm-dialog.component';
import { EmergencyCareEpisodeAdministrativeDischargeService } from '@api-rest/services/emergency-care-episode-administrative-service.service';
import { PermissionsService } from '@core/services/permissions.service';
import { anyMatch } from '@core/utils/array.utils';
import { NewTriageService } from '@historia-clinica/services/new-triage.service';
import { EmergencyCareStateChangedService } from '@historia-clinica/modules/ambulatoria/services/emergency-care-state-changed.service';
import { EmergencyCareEpisodeAttendService } from '@historia-clinica/services/emergency-care-episode-attend.service';
import { TriageDefinitionsService } from '@historia-clinica/modules/guardia/services/triage-definitions.service';

const TRANSLATE_KEY_PREFIX = 'guardia.home.episodes.episode.actions';

@Component({
	selector: 'app-resumen-de-guardia',
	templateUrl: './resumen-de-guardia.component.html',
	styleUrls: ['./resumen-de-guardia.component.scss']
})
export class ResumenDeGuardiaComponent implements OnInit {

	//En lugar de pasar el id puedo pasar el episodio entero porque ya lo voy a estar calculando desde antes en ambulatoria
	@Input() episodeId: number;
	@Output() triageRiskFactors = new EventEmitter<RiskFactorFull[]>();
	@Input() showNewTriage: boolean = false;

	guardiaSummary: SummaryHeader = GUARDIA;
	readonly STATES = EstadosEpisodio;
	episodeState: EstadosEpisodio;
	withoutMedicalDischarge: boolean;

	responseEmergencyCare: ResponseEmergencyCareDto;
	emergencyCareType: EmergencyCareTypes;
	doctorsOfficeDescription: string;
	shockroomDescription: string;
	bedDescription: string;

	triagesHistory: TriageReduced[];
	fullNamesHistoryTriage: string[];
	lastTriage: Triage;
	isEmergencyCareTemporalPatient = false;

	private hasEmergencyCareRelatedRole: boolean;
	private hasRoleAdministrative: boolean;

	availableActions: ActionInfo[] = [];

	constructor(
		private readonly emergencyCareEpisodeService: EmergencyCareEpisodeService,
		private readonly triageService: TriageService,
		private readonly guardiaMapperService: GuardiaMapperService,
		private readonly patientNameService: PatientNameService,
		private readonly dialog: MatDialog,
		private readonly episodeStateService: EpisodeStateService,
		private snackBarService: SnackBarService,
		private readonly contextService: ContextService,
		private readonly router: Router,
		private readonly emergencyCareEpisodeStateService: EmergencyCareEpisodeStateService,
		private readonly emergencyCareEpisodeAdministrativeDischargeService: EmergencyCareEpisodeAdministrativeDischargeService,
		private readonly permissionsService: PermissionsService,
		private readonly newTriageService: NewTriageService,
		private readonly emergencyCareStateChangedService: EmergencyCareStateChangedService,
		private readonly emergencyCareEpisodeAttend: EmergencyCareEpisodeAttendService,
		private readonly triageDefinitionsService: TriageDefinitionsService
	) {

	}


	ngOnInit(): void {
		this.permissionsService.contextAssignments$().subscribe(
			(userRoles: ERole[]) => {
				this.hasEmergencyCareRelatedRole = anyMatch<ERole>(userRoles, [ERole.ESPECIALISTA_MEDICO, ERole.ENFERMERO, ERole.PROFESIONAL_DE_SALUD]);
				this.hasRoleAdministrative = anyMatch<ERole>(userRoles, [ERole.ADMINISTRATIVO, ERole.ADMINISTRATIVO_RED_DE_IMAGENES]);

				this.setEpisodeState();
			}
		);

		this.loadEpisode()

		this.newTriageService.newTriage$.subscribe(
			_ => this.loadTriages()
		)

		this.emergencyCareEpisodeAttend.loadEpisode$.subscribe((response: boolean) => {
			if (!response) return;

			this.loadEpisode();
			this.setEpisodeState();
		});
	}

	newTriage() {
		this.triageDefinitionsService.getTriagePath(this.emergencyCareType)
			.subscribe(({ component }) => {
				const dialogRef = this.dialog.open(component, { data: this.episodeId });
				dialogRef.afterClosed().subscribe(idReturned => {
					if (idReturned) {
						this.loadTriages();
					}
				});
			});
	}

	cancelAttention() {
		const dialogRef = this.dialog.open(SelectConsultorioComponent, {
			width: '25%',
			data: { title: 'ambulatoria.paciente.guardia.CANCEL_BUTTON' }
		});

		dialogRef.afterClosed().subscribe(consultorio => {
			if (consultorio) {
				this.doctorsOfficeDescription = consultorio?.description;
				this.episodeStateService.cancelar(this.episodeId, consultorio.id).subscribe(changed => {
					if (changed) {
						this.emergencyCareStateChangedService.emergencyCareStateChanged(EstadosEpisodio.EN_ESPERA);
						this.snackBarService.showSuccess('ambulatoria.paciente.guardia.CANCEL_ATTENTION_SUCCESS');
						this.episodeState = EstadosEpisodio.EN_ESPERA;
						this.calculateAvailableActions();
					} else {
						this.snackBarService.showError('ambulatoria.paciente.guardia.CANCEL_ATTENTION_ERROR');
					}
				}, _ => this.snackBarService.showError('ambulatoria.paciente.guardia.CANCEL_ATTENTION_ERROR')
				);
			}
		});
	}

	goToMedicalDischarge() {
		if (!this.responseEmergencyCare?.patient) {
			this.snackBarService.showError('ambulatoria.paciente.guardia.PATIENT_REQUIRED');
		} else {
			this.router.navigate([`/institucion/${this.contextService.institutionId}/guardia/episodio/${this.episodeId}/alta-medica`]);
		}
	}

	goToAdministrativeDischarge() {
		this.router.navigate([`/institucion/${this.contextService.institutionId}/guardia/episodio/${this.episodeId}/alta-administrativa`]);
	}

	goToEditEpisode() {
		this.router.navigate([`/institucion/${this.contextService.institutionId}/guardia/episodio/${this.episodeId}/edit`]);
	}

	finalizar(): void {
		const dialogRef = this.dialog.open(ConfirmDialogComponent, {
			data: {
				title: 'guardia.home.episodes.episode.actions.finalizar_ausencia.TITLE',
				content: 'guardia.home.episodes.episode.actions.finalizar_ausencia.CONFIRM'
			}
		});

		dialogRef.afterClosed().subscribe(confirmed => {
			if (confirmed) {
				this.emergencyCareEpisodeAdministrativeDischargeService.newAdministrativeDischargeByAbsence(this.episodeId).subscribe(
					changed => {
						if (changed) {
							this.snackBarService.showSuccess(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.SUCCESS`);
							const currentUrl = this.router.url;
							this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
								this.router.navigate([currentUrl]);
							});
						}
						else {
							this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.ERROR`);
						}
					},
					_ => this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.ERROR`)
				);
			}
		});
	}

	attend() {
		this.emergencyCareEpisodeAttend.attend(this.episodeId, false);
	}

	private setEpisodeState() {
		this.emergencyCareEpisodeStateService.getState(this.episodeId).subscribe(
			state => {
				this.episodeState = state.id;
				this.calculateAvailableActions();
				this.withoutMedicalDischarge = (this.episodeState !== this.STATES.CON_ALTA_MEDICA);
			}
		);
	}

	private loadEpisode() {
		this.emergencyCareEpisodeService.getAdministrative(this.episodeId)
			.subscribe((responseEmergencyCare: ResponseEmergencyCareDto) => {
				this.responseEmergencyCare = responseEmergencyCare;
				this.emergencyCareType = responseEmergencyCare.emergencyCareType?.id;
				this.doctorsOfficeDescription = responseEmergencyCare.doctorsOffice?.description;
				this.shockroomDescription = responseEmergencyCare.shockroom?.description;
				this.bedDescription = responseEmergencyCare.bed?.bedNumber;
			});

		this.loadTriages();
	}

	private calculateAvailableActions() {
		this.availableActions = [];

		// Following code within this function must be in this order

		if (this.hasEmergencyCareRelatedRole && this.episodeState === this.STATES.EN_ATENCION) {
			let action: ActionInfo = {
				label: 'ambulatoria.paciente.guardia.MEDICAL_DISCHARGE_BUTTON',
				id: 'medical_discharge',
				callback: this.goToMedicalDischarge.bind(this)
			}
			this.availableActions.push(action);
		}

		if (this.hasRoleAdministrative && this.episodeState === this.STATES.CON_ALTA_MEDICA) {
			let action: ActionInfo = {
				label: 'ambulatoria.paciente.guardia.ADMINISTRATIVE_DISCHARGE_BUTTON',
				id: 'administrative_discharge',
				callback: this.goToAdministrativeDischarge.bind(this)
			}
			this.availableActions.push(action);
		}

		if (this.episodeState === this.STATES.EN_ATENCION || this.episodeState === this.STATES.EN_ESPERA) {
			let action: ActionInfo = {
				label: 'ambulatoria.paciente.guardia.EDIT_BUTTON',
				id: 'edit_episode',
				callback: this.goToEditEpisode.bind(this).bind(this)
			}
			this.availableActions.push(action);
		}

		if ((this.hasEmergencyCareRelatedRole || this.hasRoleAdministrative) && this.episodeState === this.STATES.EN_ESPERA) {
			let action: ActionInfo = {
				label: 'guardia.home.episodes.episode.actions.atender.TITLE',
				id: 'attend',
				callback: this.attend.bind(this)
			}
			this.availableActions.push(action);
		}

		if (this.episodeState === this.STATES.EN_ATENCION) {
			let action: ActionInfo = {
				label: 'ambulatoria.paciente.guardia.CANCEL_BUTTON',
				id: 'cancel_attention',
				callback: this.cancelAttention.bind(this)
			}
			this.availableActions.push(action);
		}

		if (this.hasRoleAdministrative && this.episodeState === this.STATES.EN_ESPERA) {
			let action: ActionInfo = {
				label: 'guardia.home.episodes.episode.actions.finalizar_ausencia.TITLE',
				id: 'finish_by_absence',
				callback: this.finalizar.bind(this)
			}
			this.availableActions.push(action);
		}
	}

	private loadFullNames() {
		this.fullNamesHistoryTriage = [];
		this.triagesHistory.forEach(
			(triage: TriageReduced) => {
				this.fullNamesHistoryTriage.push(this.getFullName(triage));
			}
		);
	}

	private getFullName(triage: TriageReduced): string {
		return `${this.patientNameService.getPatientName(triage.createdBy.firstName, triage.createdBy.nameSelfDetermination)}, ${triage.createdBy.lastName}`;
	}

	private loadTriages() {
		this.triageService.getAll(this.episodeId).subscribe((triages: TriageListDto[]) => {
			this.lastTriage = this.guardiaMapperService.triageListDtoToTriage(triages[0]);
			if (hasHistory(triages)) {
				this.triagesHistory = triages.map(this.guardiaMapperService.triageListDtoToTriageReduced);
				this.triagesHistory.shift();
				this.loadFullNames();
			}
		});

		function hasHistory(triages: TriageListDto[]) {
			return triages?.length > 1;
		}
	}





}

export interface TriageReduced {
	creationDate: Date,
	category: TriageCategory,
	createdBy: {
		firstName: string,
		lastName: string,
		nameSelfDetermination: string
	},
	doctorsOfficeDescription: string
}

interface ActionInfo {
	label: string,
	id: string,
	callback: Function
}
