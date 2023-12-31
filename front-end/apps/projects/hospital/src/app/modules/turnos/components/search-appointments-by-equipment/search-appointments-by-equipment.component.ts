import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EquipmentDiaryDto, EquipmentDto, ModalityDto } from '@api-rest/api-model';
import { EquipmentService } from '@api-rest/services/equipment.service';
import { ModalityService } from '@api-rest/services/modality.service';
import { ContextService } from '@core/services/context.service';
import { DatePipeFormat } from '@core/utils/date.utils';
import { TypeaheadOption } from '@presentation/components/typeahead/typeahead.component';
import { isAfter, startOfToday, parseISO } from 'date-fns';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EquipmentDiaryOptionsData, SearchEquipmentDiaryService } from '../../services/search-equipment-diary.service';
import { MatOptionSelectionChange } from '@angular/material/core';

@Component({
	selector: 'app-search-appointments-by-equipment',
	templateUrl: './search-appointments-by-equipment.component.html',
	styleUrls: ['./search-appointments-by-equipment.component.scss'],
	providers: [SearchEquipmentDiaryService]
})
export class SearchAppointmentsByEquipmentComponent implements OnInit {

	@Input() selectedEquipment: EquipmentDto;
	@Input() editedDiary: EquipmentDiaryDto;
	
	modalities$: Observable<TypeaheadOption<ModalityDto>[]>;

	equipmentsTypeaheadList: TypeaheadOption<EquipmentDto>[];
	equipments: EquipmentDto[] = [];

	diarySelected: EquipmentDiaryDto;
	equipmentSelected: EquipmentDto;

	diaries: EquipmentDiaryDto[];
	activeDiaries: EquipmentDiaryDto[] = [];
	expiredDiaries: EquipmentDiaryDto[] = [];

	externalSelectedEquipment: TypeaheadOption<EquipmentDto>;

	readonly dateFormats = DatePipeFormat;

	constructor(
		private readonly modalityService: ModalityService,
		private readonly equipmentService: EquipmentService,
		private readonly router: Router,
		private contextService: ContextService,
		private readonly searchEquipmentService: SearchEquipmentDiaryService,
		private changeDetectorRef: ChangeDetectorRef,
	) { }

	ngOnInit(): void {
		this.modalities$ = this.setModalityTypeaheadOptions();
		this.setEquipments();
		this.searchEquipmentService.getAgendas$().subscribe((data: EquipmentDiaryOptionsData) => {
			if (data) {
				this.loadAgendas(data.diaries, data.idAgendaSelected);
				if (this.editedDiary) this.diarySelected = this.editedDiary;
			}
		});
	}

	changeDiarySelected(event: MatOptionSelectionChange, diary: EquipmentDiaryDto) {
		if (event.isUserInput) {
			this.diarySelected = diary;
		}
	}

	goToNewAgenda() {
		this.router.navigate([`institucion/${this.contextService.institutionId}/turnos/imagenes/nueva-agenda`]);
	}

	clear() {
		this.diarySelected = null;
		this.editedDiary = null;
		this.changeDetectorRef.detectChanges();
	}

	filterEquipmentsByModality(modality: ModalityDto) {
		if (modality) {
			const equipmentsFilteredByModality: EquipmentDto[] = this.equipments.filter(equipment => equipment.modalityId === modality.id);
			this.equipmentsTypeaheadList = equipmentsFilteredByModality.map(e => this.toEquipmentTypeaheadOptions(e));
		}
		else {
			this.equipmentsTypeaheadList = this.equipments.map(e => this.toEquipmentTypeaheadOptions(e));
		}
		this.equipmentSelected = null;
		this.clear();
	}

	loadDiaryByEquipment(e: EquipmentDto) {
		if (!e) {
			this.selectedEquipment = null;
			this.editedDiary = null;
		}
		this.equipmentSelected = e;
		this.searchEquipmentService.search(e?.id);
	}

	compareDiaries(diaryOne: EquipmentDiaryDto, diaryTwo: EquipmentDiaryDto): boolean {
		if (diaryOne && diaryTwo) {
			return diaryOne.id === diaryTwo.id;
		}
		return false;
	}

	private setEquipments() {
		this.equipmentService.getAll().subscribe((equipments: EquipmentDto[]) => {
			this.equipments = equipments;
			this.equipmentsTypeaheadList = equipments.map(e => this.toEquipmentTypeaheadOptions(e));
			this.setSelectedEquipmentIfDiaryWasEdited()
		});
	}

	private setSelectedEquipmentIfDiaryWasEdited() {
		if (this.selectedEquipment) {
			this.externalSelectedEquipment = this.toEquipmentTypeaheadOptions(this.selectedEquipment);
			this.loadDiaryByEquipment(this.selectedEquipment);
		}
	}

	private setModalityTypeaheadOptions(): Observable<TypeaheadOption<ModalityDto>[]> {
		return this.modalityService.getAll().pipe(map(toTypeaheadOptionList));

		function toTypeaheadOptionList(modalities: ModalityDto[]): TypeaheadOption<ModalityDto>[] {
			return modalities.map(toTypeaheadOption);

			function toTypeaheadOption(m: ModalityDto): TypeaheadOption<ModalityDto> {
				return {
					compareValue: m.description,
					value: m
				};
			}
		}
	}

	private toEquipmentTypeaheadOptions(equipment: EquipmentDto,): TypeaheadOption<EquipmentDto> {
		return {
			compareValue: equipment.name,
			value: equipment
		};
	}

	private loadAgendas(diaries: EquipmentDiaryDto[], diarySelectedId: number) {
		delete this.diaries;
		delete this.diarySelected;
		this.diaries = diaries;
		this.categorizeAgendas(diaries);
		if (diarySelectedId) {
			this.diarySelected = this.diaries.find(agenda => agenda.id === diarySelectedId);
		}
	}

	private categorizeAgendas(diaries: EquipmentDiaryDto[]) {
		this.expiredDiaries = [];
		this.activeDiaries = [];
		if (diaries?.length)
			diaries.forEach(diary =>
				isAfter(startOfToday(), parseISO(diary.endDate)) ? this.expiredDiaries.push(diary) : this.activeDiaries.push(diary)
			);
	}

	goToEditAgenda(){
		this.router.navigate([`institucion/${this.contextService.institutionId}/turnos/imagenes/agenda/${this.diarySelected.id}/editar`], { state : { selectedEquipment: this.equipmentSelected, selectedDiary: this.diarySelected}});
	}
}
