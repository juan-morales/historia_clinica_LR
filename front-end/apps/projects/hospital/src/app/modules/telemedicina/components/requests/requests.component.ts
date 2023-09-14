import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewTelemedicineRequestComponent } from '../../dialogs/new-telemedicine-request/new-telemedicine-request.component';
import { VirtualConstultationService } from '@api-rest/services/virtual-constultation.service';
import { ContextService } from '@core/services/context.service';
import { CareLineDto, ClinicalSpecialtyDto, EVirtualConsultationStatus, VirtualConsultationDto } from '@api-rest/api-model';
import { dateTimeDtotoLocalDate } from '@api-rest/mapper/date-dto.mapper';
import { timeDifference } from '@core/utils/date.utils';
import { statusLabel, mapPriority, status } from '../../virtualConsultations.utils';
import { Subscription } from 'rxjs';
import { VirtualConsultationsFacadeService } from '../../virtual-consultations-facade.service';
import { ConfirmDialogComponent } from '@presentation/dialogs/confirm-dialog/confirm-dialog.component';
import { CareLineService } from '@api-rest/services/care-line.service';
import { Option, filter } from '@presentation/components/filters-select/filters-select.component';
import { ClinicalSpecialtyService } from '@api-rest/services/clinical-specialty.service';
import { HealthcareProfessionalByInstitutionService } from '@api-rest/services/healthcare-professional-by-institution.service';

@Component({
	selector: 'app-requests',
	templateUrl: './requests.component.html',
	styleUrls: ['./requests.component.scss']
})
export class RequestsComponent implements OnInit {
	@Input() priorityOptions: Option[];
	@Input() availitibyOptions: Option[];
	virtualConsultations: VirtualConsultationDto[] = [];
	virtualConsultationsSubscription: Subscription;
	virtualConsultatiosStatus = status;
	initialResponsableStatus = false;
	careLinesOptions: CareLineDto[];
	specialitiesOptions: ClinicalSpecialtyDto[];
	professionalsOptions: Option[] = [];
	stateOptions: Option[] = [];
	filters: filter[] = [];

	constructor(
		private dialog: MatDialog,
		private virtualConsultationService: VirtualConstultationService,
		private contextService: ContextService,
		private readonly virtualConsultationsFacadeService: VirtualConsultationsFacadeService,
		private careLineService: CareLineService, private clinicalSpecialtyService: ClinicalSpecialtyService,
		private healthcareProfessionalByInstitucion: HealthcareProfessionalByInstitutionService,
	) {
		this.virtualConsultationService.getResponsibleStatus(this.contextService.institutionId).subscribe(
			status => this.initialResponsableStatus = status
		)
	}

	ngOnInit(): void {
		this.setStateOptions();
		this.getOptionsFilters();
		this.virtualConsultationsSubscription = this.virtualConsultationsFacadeService.virtualConsultationsRequest$
			.subscribe(virtualConsultations =>
				this.virtualConsultations = virtualConsultations.map(this.toVCToBeShown));
	}

	getOptionsFilters() {
		this.careLineService.getVirtualConsultationCareLinesByInstitutionId().subscribe(careLines => {
			this.careLinesOptions = careLines;
			this.clinicalSpecialtyService.getVirtualConsultationClinicalSpecialtiesByInstitutionId().subscribe(specialities => {
				this.specialitiesOptions = specialities;
				this.healthcareProfessionalByInstitucion.getVirtualConsultationHealthcareProfessionalsByInstitutionId().subscribe(professionals => {
					professionals.forEach(professional => {
						let option: Option = {
							id: professional.id,
							description: professional.firstName + ' ' + professional.lastName,
						}
						this.professionalsOptions.push(option);
					})
					this.prepareFilters();
				})
			})
		})
	}

	setStateOptions() {
		let state: Option = {
			id: EVirtualConsultationStatus.FINISHED,
			description: statusLabel[EVirtualConsultationStatus.FINISHED].description,
		}
		this.stateOptions.push(state);

		state = {
			id: EVirtualConsultationStatus.CANCELED,
			description: statusLabel[EVirtualConsultationStatus.CANCELED].description,
		}
		this.stateOptions.push(state);
		state = {
			id: EVirtualConsultationStatus.IN_PROGRESS,
			description: statusLabel[EVirtualConsultationStatus.IN_PROGRESS].description,
		}
		this.stateOptions.push(state);
		state = {
			id: EVirtualConsultationStatus.PENDING,
			description: statusLabel[EVirtualConsultationStatus.PENDING].description,
		}
		this.stateOptions.push(state);
	}

	private toVCToBeShown(vc: VirtualConsultationDto) {
		return {
			...vc,
			statusLabel: statusLabel[vc.status],
			priorityLabel: mapPriority[vc.priority],
			waitingTime: timeDifference(dateTimeDtotoLocalDate(vc.creationDateTime))
		}
	}

	openAddRequest() {
		this.dialog.open(NewTelemedicineRequestComponent, {
			disableClose: true,
			width: '700px',
			height: '700px',
		})
	}

	availabilityChanged(available: boolean) {
		this.virtualConsultationService.changeResponsibleAttentionState(this.contextService.institutionId, available).subscribe();
	}

	confirm(virtualConsultationId: number) {
		const ref = this.dialog.open(ConfirmDialogComponent, {
			data: {
				showMatIconError: true,
				title: `Confirmar atención`,
				cancelButtonLabel: 'NO, REGRESAR',
				okButtonLabel: 'SI, CONFIRMAR ATENCIÓN',
				content: `Si confirma la atención se asume que se efectuó una teleconsulta satisfactoriamente y la solicitud se quitará de la lista de espera. ¿Está seguro que desea confirmarla?`,
			},
			width: '33%'
		});

		ref.afterClosed().subscribe(
			closed => {
				if (closed) {
					this.virtualConsultationService.changeVirtualConsultationState(virtualConsultationId, { status: EVirtualConsultationStatus.FINISHED }).subscribe()
				}
			}
		)
	}

	cancel(virtualConsultationId: number) {
		const ref = this.dialog.open(ConfirmDialogComponent, {
			data: {
				showMatIconError: true,
				title: `Cancelar solicitud`,
				cancelButtonLabel: 'NO, REGRESAR',
				okButtonLabel: 'SI, CANCELAR ATENCIÓN',
				content: `Si cancela la solicitud la misma ya no se verá en el listado de espera de los profesionales para ser atendida. <strong>¿Está seguro que desea cancelarla?</strong>`,
				okBottonColor: 'warn'
			},
			width: '33%'
		});

		ref.afterClosed().subscribe(
			closed => {
				if (closed) {
					this.virtualConsultationService.changeVirtualConsultationState(virtualConsultationId, { status: EVirtualConsultationStatus.CANCELED }).subscribe()
				}
			}
		)
	}

	prepareFilters() {
		let filters = [];
		let filterCareLines: filter = {
			key: 'careLine',
			name: "'telemedicina.request.form.CARELINE'",
			options: this.careLinesOptions,
		}
		filters.push(filterCareLines);

		let filterSpecialities: filter = {
			key: 'speciality',
			name: "'telemedicina.request.form.SPECIALTY'",
			options: this.specialitiesOptions,
		}
		filters.push(filterSpecialities);

		let filterPriority: filter = {
			key: 'priority',
			name: "'telemedicina.request.form.PRIORITY'",
			options: this.priorityOptions,
		}
		filters.push(filterPriority);

		let filterProfessionals: filter = {
			key: 'professional',
			name: "'telemedicina.request.form.APPLICANT_PROFESSIONAL'",
			options: this.professionalsOptions,
		}
		filters.push(filterProfessionals);

		let filterAvailability: filter = {
			key: 'availability',
			name: "'telemedicina.request.form.AVAILABILITY'",
			options: this.availitibyOptions,
		}
		filters.push(filterAvailability);

		let filterState: filter = {
			key: 'state',
			name: 'telemedicina.request.form.STATE',
			options: this.stateOptions,
		}
		filters.push(filterState);

		this.filters = filters;
	}
}

