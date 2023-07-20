import { Component, OnInit } from '@angular/core';
import { Worklist } from '../worklist/worklist.component';
import { FeatureFlagService } from '@core/services/feature-flag.service';
import { IdentificationTypeDto, MasterDataDto, ModalityDto, WorklistDto } from '@api-rest/api-model';
import { AppFeature } from '@api-rest/api-model';
import { ModalityService } from '@api-rest/services/modality.service';
import { Observable } from 'rxjs';
import { WorklistService } from '@api-rest/services/worklist.service';
import { PersonMasterDataService } from '@api-rest/services/person-master-data.service';
import { MatSelectChange } from '@angular/material/select';
import { dateTimeDtotoLocalDate } from '@api-rest/mapper/date-dto.mapper';
import { InformerStatus, mapToState } from '../../utils/study.utils';
import { Router } from '@angular/router';
import { ContextService } from '@core/services/context.service';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

const PAGE_SIZE_OPTIONS = [10];
const PAGE_MIN_SIZE = 10;

@Component({
	selector: 'app-worklist-by-informer',
	templateUrl: './worklist-by-informer.component.html',
	styleUrls: ['./worklist-by-informer.component.scss']
})
export class WorklistByInformerComponent implements OnInit {

	public modalitiesForm: UntypedFormGroup;
	worklists: Worklist[] = [];
	modalityId: number;
	nameSelfDeterminationFF = false;
	modalities$: Observable<ModalityDto[]>;
	identificationTypes: IdentificationTypeDto[] = [];
	worklistStatus: MasterDataDto[] = [];
	routePrefix: string;

	pageSlice = [];
	pageSizeOptions = PAGE_SIZE_OPTIONS;

	readonly COMPLETED = InformerStatus.COMPLETED;
	readonly PENDING = InformerStatus.PENDING;

	constructor(
		private readonly featureFlagService: FeatureFlagService,
		private readonly modalityService: ModalityService,
		private readonly worklistService: WorklistService,
		private readonly personMasterData: PersonMasterDataService,
		private readonly router: Router,
		private readonly contextService: ContextService,
		private readonly formBuilder: UntypedFormBuilder
	) { }

	ngOnInit(): void {
		this.routePrefix = `institucion/${this.contextService.institutionId}/imagenes/lista-trabajos`;
		this.personMasterData.getIdentificationTypes().subscribe(types => this.identificationTypes = types);
		this.modalities$ = this.modalityService.getModalitiesByStudiesCompleted();
		this.featureFlagService.isActive(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS).subscribe(isOn => {
			this.nameSelfDeterminationFF = isOn
		});
		this.worklistService.getWorklistStatus().subscribe(status => this.worklistStatus = status);
		this.modalitiesForm = this.formBuilder.group({
			modalities: []
		});
		this.worklistService.getByModalityAndInstitution().subscribe((worklist: WorklistDto[]) => {
			this.worklists = this.mapToWorklist(worklist);
			this.pageSlice = this.worklists.slice(0, PAGE_MIN_SIZE);
		});
	}

	setWorklist(modalitySelected: MatSelectChange) {
		this.worklists = [];
		this.modalityId = modalitySelected.value;
		this.worklistService.getByModalityAndInstitution(this.modalityId).subscribe((worklist: WorklistDto[]) => {
			this.worklists = this.mapToWorklist(worklist);
			this.pageSlice = this.worklists.slice(0, PAGE_MIN_SIZE);
		});
	}

	goToDetails(appointmentId: number) {
		this.router.navigate([`${this.routePrefix}/detalle-estudio/${appointmentId}`], );
	}

	cleanInput(){
		this.worklists = [];
		this.modalitiesForm.controls.modalities.setValue(null);
		this.modalityId = null;
		this.worklistService.getByModalityAndInstitution().subscribe((worklist: WorklistDto[]) => {
			this.worklists = this.mapToWorklist(worklist);
			this.pageSlice = this.worklists.slice(0, PAGE_MIN_SIZE);
		});
	}

	private mapToWorklist(worklist: WorklistDto[]): Worklist[] {
		return worklist.map(w => {
			return {
				patientInformation: {
					fullName: this.capitalizeName(w.patientFullName),
					identification: `${this.getIdentificationType(w.patientIdentificationTypeId)} ${w.patientIdentificationNumber} - ID ${w.patientId}`,
				},
				state: mapToState(w.statusId),
				date: dateTimeDtotoLocalDate(w.actionTime),
				appointmentId: w.appointmentId
			}
		})
	}

	private capitalizeName(name: string): string {
		let capitalizedName = '';
		name.split(" ").map(name => capitalizedName += this.capitalizeWords(name) + " ")
		return capitalizedName;
	}

	private capitalizeWords(sentence: string) {
        return sentence ? sentence
          .toLowerCase()
          .split(' ')
          .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
          .join(' ') : "";
    }

	private getIdentificationType(id: number): string {
		return this.identificationTypes.find(identificationType => identificationType.id === id).description
	}

	onPageChange($event: any) {
		const page = $event;
		const startPage = page.pageIndex * page.pageSize;
		this.pageSlice = this.worklists.slice(startPage, $event.pageSize + startPage);
	}
}
