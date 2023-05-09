import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EmergencyCareEpisodeService } from '@api-rest/services/emergency-care-episode.service';
import {
	ApiErrorDto,
	AttentionPlacesQuantityDto,
	BedInfoDto,
	DateTimeDto,
	DoctorsOfficeDto, EmergencyCareEpisodeListTriageDto,
	EmergencyCareListDto,
	EmergencyCarePatientDto,
	MasterDataDto, MasterDataInterface,
	PatientPhotoDto,
	ProfessionalPersonDto,
} from '@api-rest/api-model';
import { ERole } from '@api-rest/api-model';
import { dateTimeDtoToDate } from '@api-rest/mapper/date-dto.mapper';
import { differenceInMinutes } from 'date-fns';
import { EstadosEpisodio, Triages } from '../../constants/masterdata';
import { ImageDecoderService } from '@presentation/services/image-decoder.service';
import { EpisodeStateService } from '../../services/episode-state.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '@presentation/dialogs/confirm-dialog/confirm-dialog.component';
import { TriageDefinitionsService } from '../../services/triage-definitions.service';
import { PatientService } from '@api-rest/services/patient.service';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { EpisodeFilterService } from '../../services/episode-filter.service';
import { TriageCategoryDto, TriageMasterDataService } from '@api-rest/services/triage-master-data.service';
import { EmergencyCareMasterDataService } from '@api-rest/services/emergency-care-master-data.service';
import { getError, hasError, processErrors } from '@core/utils/form.utils';
import { EmergencyCareEpisodeAdministrativeDischargeService } from '@api-rest/services/emergency-care-episode-administrative-service.service';
import { PatientNameService } from "@core/services/patient-name.service";
import { anyMatch } from '@core/utils/array.utils';
import { PermissionsService } from '@core/services/permissions.service';
import { GuardiaRouterService } from '../../services/guardia-router.service';
import { EmergencyCareStateChangedService } from '@historia-clinica/modules/ambulatoria/services/emergency-care-state-changed.service';
import { AttentionPlace, PatientType } from '@historia-clinica/constants/summaries';
import { AttentionPlaceDialogComponent } from '../../dialogs/attention-place-dialog/attention-place-dialog.component';
import { FormBuilder } from '@angular/forms';
import { BedAssignmentComponent } from '@historia-clinica/dialogs/bed-assignment/bed-assignment.component';
import { SectorService } from '@api-rest/services/sector.service';
import { OperationDeniedComponent } from '@historia-clinica/modules/ambulatoria/dialogs/diagnosis-required/operation-denied.component';

const TRANSLATE_KEY_PREFIX = 'guardia.home.episodes.episode.actions';
export const GUARDIA: number = 3;

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

	getError = getError;
	hasError = hasError;

	filterService: EpisodeFilterService;

	readonly estadosEpisodio = EstadosEpisodio;
	readonly triages = Triages;
	readonly PACIENTE_TEMPORAL = 3;
	readonly EMERGENCY_CARE_TEMPORARY = PatientType.EMERGENCY_CARE_TEMPORARY;

	loading = true;
	episodes: Episode[];
	episodesOriginal: Episode[];
	patientsPhotos: PatientPhotoDto[];

	triageCategories$: Observable<TriageCategoryDto[]>;
	emergencyCareTypes$: Observable<MasterDataInterface<number>[]>;

	hasEmergencyCareRelatedRole: boolean;

	private static calculateWaitingTime(dateTime: DateTimeDto): number {
		const creationDate = dateTimeDtoToDate(dateTime);
		const now = new Date();
		return differenceInMinutes(now, creationDate);
	}

	constructor(
		private router: Router,
		private emergencyCareEpisodeService: EmergencyCareEpisodeService,
		private imageDecoderService: ImageDecoderService,
		private snackBarService: SnackBarService,
		private readonly dialog: MatDialog,
		public readonly episodeStateService: EpisodeStateService,
		private readonly triageDefinitionsService: TriageDefinitionsService,
		private readonly patientService: PatientService,
		public readonly formBuilder: FormBuilder,
		public readonly triageMasterDataService: TriageMasterDataService,
		public readonly emergencyCareMasterDataService: EmergencyCareMasterDataService,
		private readonly emergencyCareEpisodeAdministrativeDischargeService: EmergencyCareEpisodeAdministrativeDischargeService,
		private readonly patientNameService: PatientNameService,
		private readonly permissionsService: PermissionsService,
		private readonly guardiaRouterService: GuardiaRouterService,
		private readonly emergencyCareStateChangedService: EmergencyCareStateChangedService,
		private readonly sectorService: SectorService
	) {
		this.filterService = new EpisodeFilterService(formBuilder, triageMasterDataService, emergencyCareMasterDataService);
	}

	ngOnInit(): void {
		this.loadEpisodes();
		this.triageCategories$ = this.filterService.getTriageCategories();
		this.emergencyCareTypes$ = this.filterService.getEmergencyCareTypes();
		this.permissionsService.contextAssignments$().subscribe((userRoles: ERole[]) => {
			this.hasEmergencyCareRelatedRole = anyMatch<ERole>(userRoles, [ERole.ESPECIALISTA_MEDICO, ERole.ENFERMERO, ERole.PROFESIONAL_DE_SALUD]);
		});
	}

	loadEpisodes(): void {
		this.emergencyCareEpisodeService.getAll()
			.pipe(
				map((episodes: EmergencyCareListDto[]) =>
					episodes.map(episode => this.setWaitingTime(episode))
				)
			)
			.subscribe((episodes: any[]) => {
				this.episodes = this.setPatientNames(episodes);
				this.episodesOriginal = episodes;
				this.loading = false;
				this.completePatientPhotos();
				this.episodes = this.episodesOriginal.filter(episode => this.filterService.filter(episode));
				this.episodes.forEach(episode => {
					if (episode.relatedProfessional)
						this.getFullProfessionalName(episode.relatedProfessional);
				});
			}, _ => this.loading = false);
	}

	goToEpisode(episode: Episode, patient: { typeId: number, id: number }) {
		this.guardiaRouterService.goToEpisode(episode.state.id, { id: patient.id, typeId: patient.typeId });
	}

	goToAdmisionAdministrativa(): void {
		this.router.navigate([`${this.router.url}/nuevo-episodio/administrativa`]);
	}

	atender(episode: Episode, patientId: number): void {

		this.sectorService.quantityAttentionPlacesBySectorType(GUARDIA).subscribe((quantity: AttentionPlacesQuantityDto) => {
			if (quantity.shockroom == 0 && quantity.doctorsOffice == 0 && quantity.bed == 0) {
				return this.dialog.open(OperationDeniedComponent, {
					data: {
						message: 'No existen lugares disponibles para realizar la atención'
					}
				})
			} 
			this.openPlaceAttendDialog(episode, quantity);
		});
	}
	
	private openPlaceAttendDialog(episode: Episode, quantity: AttentionPlacesQuantityDto) {
		const dialogRef = this.dialog.open(AttentionPlaceDialogComponent, {
			width: '35%',
			data: {
				quantity
			}
		});
	
		dialogRef.afterClosed().subscribe((attendPlace: AttendPlace) => {
			if (!attendPlace) return;
			
			if (attendPlace.attentionPlace === AttentionPlace.CONSULTORIO) {
				this.episodeStateService.atender(episode.id, attendPlace.id).subscribe(changed => {
					if (changed) {
						this.emergencyCareStateChangedService.emergencyCareStateChanged(EstadosEpisodio.EN_ATENCION);
						this.snackBarService.showSuccess(`${TRANSLATE_KEY_PREFIX}.atender.SUCCESS`);
						this.goToEpisode(episode, { typeId: episode.patient.typeId, id: episode.patient.id });
					} else {
						this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.atender.ERROR`);
					}
				}, (error: ApiErrorDto) => processErrors(error, (msg) => this.snackBarService.showError(msg)))
			}
	
			if (attendPlace.attentionPlace === AttentionPlace.SHOCKROOM) {
				this.episodeStateService.atender(episode.id, null, attendPlace.id).subscribe((response: boolean) => {
					if (!response) this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.atender.ERROR`);
	
					this.snackBarService.showSuccess(`${TRANSLATE_KEY_PREFIX}.atender.SUCCESS`);
					this.goToEpisode(episode, { typeId: episode.patient.typeId, id: episode.patient.id });
	
				}, (error: ApiErrorDto) => processErrors(error, (msg) => this.snackBarService.showError(msg)))
			}
	
			if (attendPlace.attentionPlace === AttentionPlace.HABITACION) {
				this.dialog.open(BedAssignmentComponent, {data: GUARDIA})
					.afterClosed()
					.subscribe((bed: BedInfoDto) => {
						if (!bed) return;
	
						this.episodeStateService.atender(episode.id, null, null, bed.bed.id).subscribe((response: boolean) => {
							if (!response) this.snackBarService.showError(`${TRANSLATE_KEY_PREFIX}.atender.ERROR`);
	
							this.snackBarService.showSuccess(`${TRANSLATE_KEY_PREFIX}.atender.SUCCESS`);
							this.goToEpisode(episode, { typeId: episode.patient.typeId, id: episode.patient.id });
	
						}, (error: ApiErrorDto) => processErrors(error, (msg) => this.snackBarService.showError(msg)))
					});
			}
		});
	}

	finalizar(episodeId: number): void {
		const dialogRef = this.dialog.open(ConfirmDialogComponent, {
			data: {
				title: 'guardia.home.episodes.episode.actions.finalizar_ausencia.TITLE',
				content: 'guardia.home.episodes.episode.actions.finalizar_ausencia.CONFIRM'
			}
		});

		dialogRef.afterClosed().subscribe(confirmed => {
			if (confirmed) {
				this.emergencyCareEpisodeAdministrativeDischargeService.newAdministrativeDischargeByAbsence(episodeId).subscribe(changed => {
					if (changed) {
						this.snackBarService
							.showSuccess(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.SUCCESS`);
						this.loadEpisodes();
					} else {
						this.snackBarService
							.showError(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.ERROR`);
					}
				}, _ => this.snackBarService
					.showError(`${TRANSLATE_KEY_PREFIX}.finalizar_ausencia.ERROR`)
				);
			}
		});
	}

	nuevoTriage(episode: EmergencyCareListDto): void {
		this.triageDefinitionsService.getTriagePath(episode.type?.id)
			.subscribe(({ component }) => {
				const dialogRef = this.dialog.open(component, { data: episode.id });
				dialogRef.afterClosed().subscribe(idReturned => {
					if (idReturned) {
						this.loadEpisodes();
					}
				});
			});
	}

	filter(): void {
		this.filterService.markAsFiltered();
		if (this.filterService.isValid()) {
			this.episodes = this.episodesOriginal
				.filter(episode => this.filterService.filter(episode));
		}
	}

	clear(control: string): void {
		this.filterService.clear(control);
		this.filter();
	}

	private completePatientPhotos() {
		if (this.patientsPhotos) {
			this.patientsPhotos.forEach(patientPhoto => {
				this.setEpisodePhoto(patientPhoto.patientId, patientPhoto.imageData);
			});
		} else {
			const patientsIds = getPatientsIds(this.episodes);
			if (patientsIds.length) {
				this.patientService.getPatientsPhotos(patientsIds).subscribe(patientsPhotos => {
					this.patientsPhotos = patientsPhotos;
					this.patientsPhotos.forEach(patientPhoto => {
						this.setEpisodePhoto(patientPhoto.patientId, patientPhoto.imageData);
					});
				});
			}
		}

		function getPatientsIds(episodes: any[]) {
			const ids = [];
			episodes.forEach(ep => {
				if (ep.patient?.id) {
					ids.push(ep.patient.id);
				}
			});
			return ids;
		}
	}

	private setEpisodePhoto(patientId: number, imageData: string) {
		const episode = this.episodes.find(ep => ep.patient?.id === patientId);
		if (episode) {
			episode.decodedPatientPhoto = this.imageDecoderService.decode(imageData);
		}
	}

	private setWaitingTime(episode: EmergencyCareListDto): Episode {
		const minWaitingTime = episode.state.id === this.estadosEpisodio.EN_ESPERA ?
			HomeComponent.calculateWaitingTime(episode.creationDate) : undefined;
		return {
			...episode,
			waitingTime: minWaitingTime,
			waitingHours: minWaitingTime ? Math.round(minWaitingTime / 60) : undefined
		};
	}

	setPatientNames(episodes: any[]) {
		return episodes.filter(e => {
			if (e.patient?.person)
				e.patient.person.firstName = this.patientNameService.getPatientName(e.patient.person.firstName, e.patient.person.nameSelfDetermination);
		})
	}

	private getFullProfessionalName(professional: ProfessionalPersonDto) {
		const professionalName = `${this.patientNameService.getPatientName(professional.firstName, professional.nameSelfDetermination)}`;
		professional.fullName = `${professionalName} ${professional.middleNames == null ? "" : professional.middleNames} ${professional.lastName} ${professional.otherLastNames == null ? "" : professional.otherLastNames}`;
	}

}

export interface Episode {
	waitingTime: number;
	waitingHours: number;
	decodedPatientPhoto?: Observable<string>;
	creationDate: DateTimeDto;
	doctorsOffice: DoctorsOfficeDto;
	id: number;
	patient: EmergencyCarePatientDto;
	state: MasterDataDto;
	triage: EmergencyCareEpisodeListTriageDto;
	type: MasterDataDto;
	relatedProfessional: ProfessionalPersonDto;
}

export interface AttendPlace {
	id: number,
	attentionPlace: AttentionPlace
}
