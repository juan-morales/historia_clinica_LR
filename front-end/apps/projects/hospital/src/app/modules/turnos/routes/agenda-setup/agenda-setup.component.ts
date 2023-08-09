import { ChangeDetectorRef, Component, ElementRef, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormControl, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { DAYS_OF_WEEK } from 'angular-calendar';
import { Observable, combineLatest, filter, switchMap } from 'rxjs';

import { getError, hasError, processErrors, scrollIntoError } from '@core/utils/form.utils';
import { ContextService } from '@core/services/context.service';
import { currentWeek, DateFormat, momentFormat, momentParseDate, momentParseTime } from '@core/utils/moment.utils';
import { ConfirmDialogComponent } from '@presentation/dialogs/confirm-dialog/confirm-dialog.component';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { SectorService } from '@api-rest/services/sector.service';
import { DoctorsOfficeService } from '@api-rest/services/doctors-office.service';
import { HealthcareProfessionalByInstitutionService } from '@api-rest/services/healthcare-professional-by-institution.service';
import {
	CareLineDto,
	CompleteDiaryDto,
	DiaryADto,
	DiaryDto,
	DoctorsOfficeDto,
	HierarchicalUnitDto,
	OccupationDto,
	ProfessionalDto,
} from '@api-rest/api-model';
import { DiaryOpeningHoursService } from '@api-rest/services/diary-opening-hours.service';
import { DiaryService } from '@api-rest/services/diary.service';
import { APPOINTMENT_DURATIONS, MINUTES_IN_HOUR } from '../../constants/appointment';
import { AgendaHorarioService } from '../../services/agenda-horario.service';
import { PatientNameService } from "@core/services/patient-name.service";
import { SpecialtyService } from '@api-rest/services/specialty.service';
import { CareLineService } from '@api-rest/services/care-line.service';
import { HierarchicalUnitsService } from '@api-rest/services/hierarchical-units.service';
import { HealthcareProfessionalService } from '@api-rest/services/healthcare-professional.service';

const ROUTE_APPOINTMENT = 'turnos';
const ROUTE_AGENDAS = "agenda";
const MAX_INPUT = 100;
const PATTERN = /^[0-9]\d*$/;

@Component({
	selector: 'app-agenda-setup',
	templateUrl: './agenda-setup.component.html',
	styleUrls: ['./agenda-setup.component.scss']
})
export class AgendaSetupComponent implements OnInit {

	readonly MONDAY = DAYS_OF_WEEK.MONDAY;
	readonly TODAY: Date = new Date();
	readonly PIXEL_SIZE_HEIGHT = 30;
	readonly TURN_STARTING_HOUR = 6;

	appointmentDurations = APPOINTMENT_DURATIONS;
	appointmentManagement = false;
	autoRenew = false;
	closingTime: number;
	defaultDoctorOffice: DoctorsOfficeDto;
	doctorOffices: DoctorsOfficeDto[];
	editMode = false;
	errors: string[] = [];
	form: UntypedFormGroup;
	holidayWork = false;
	hourSegments: number;
	minDate = new Date();
	openingTime: number;
	professionals: ProfessionalDto[];
	sectors;
	agendaHorarioService: AgendaHorarioService;
	professionalSpecialties: any[];
	specialityId: number;
	alias: string;
	hasError = hasError;
	getError = getError;
	careLines: CareLineDto[] = [];
	careLinesSelected: CareLineDto[] = [];
	hierarchicalUnits: HierarchicalUnitDto[] = [];
	private editingDiaryId = null;
	private readonly routePrefix;
	private mappedCurrentWeek = {};
	lineOfCareAndPercentageOfProtectedAppointmentsValid = true;
	loadSavedData = false;
	temporary = false;
	constructor(
		private readonly el: ElementRef,
		private readonly sectorService: SectorService,
		private translator: TranslateService,
		private dialog: MatDialog,
		private doctorsOfficeService: DoctorsOfficeService,
		private healthcareProfessionalService: HealthcareProfessionalByInstitutionService,
		private readonly contextService: ContextService,
		private readonly router: Router,
		private readonly cdr: ChangeDetectorRef,
		private readonly diaryService: DiaryService,
		private readonly snackBarService: SnackBarService,
		private readonly diaryOpeningHoursService: DiaryOpeningHoursService,
		private readonly route: ActivatedRoute,
		private readonly patientNameService: PatientNameService,
		private readonly specialtyService: SpecialtyService,
		private readonly carelineService: CareLineService,
		private readonly hierarchicalUnitsService: HierarchicalUnitsService,
		private readonly professionalService: HealthcareProfessionalService
	) {
		this.routePrefix = `institucion/${this.contextService.institutionId}/`;
		this.agendaHorarioService = new AgendaHorarioService(this.dialog, this.cdr, this.TODAY, this.MONDAY, snackBarService);
	}

	ngOnInit(): void {

		currentWeek().forEach(day => {
			this.mappedCurrentWeek[day.day()] = day;
		});

		this.form = new UntypedFormGroup({
			sectorId: new UntypedFormControl(null, [Validators.required]),
			doctorOffice: new UntypedFormControl(null, [Validators.required]),
			healthcareProfessionalId: new UntypedFormControl(null, [Validators.required]),
			professionalReplacedId: new UntypedFormControl(null),
			startDate: new UntypedFormControl(null, [Validators.required]),
			endDate: new UntypedFormControl(null, [Validators.required]),
			hierarchicalUnit: new UntypedFormControl(null),
			hierarchicalUnitTemporary: new UntypedFormControl(null),
			appointmentDuration: new UntypedFormControl(null, [Validators.required]),
			healthcareProfessionalSpecialtyId: new UntypedFormControl(null, [Validators.required]),
			conjointDiary: new UntypedFormControl(false, [Validators.nullValidator]),
			temporaryReplacement: new UntypedFormControl(false),
			alias: new UntypedFormControl(null, [Validators.nullValidator]),
			otherProfessionals: new UntypedFormArray([], [this.otherPossibleProfessionals()]),
			protectedAppointmentsPercentage: new UntypedFormControl({ value: 0, disabled: true }, [Validators.pattern(PATTERN), Validators.max(MAX_INPUT)]),
			careLines: new UntypedFormControl([null])
		});

		this.form.controls.appointmentDuration.valueChanges
			.subscribe(newDuration => this.hourSegments = MINUTES_IN_HOUR / newDuration);

		this.form.controls.temporaryReplacement.valueChanges.subscribe((temporaryReplacement: boolean) => {
			const hierarchicalUnitCtrl = this.form.controls.hierarchicalUnit;
			const hierarchicalUnitTemporaryCtrl = this.form.controls.hierarchicalUnitTemporary;
			const professionalReplacedIdCtrl = this.form.controls.professionalReplacedId;
			if (temporaryReplacement) {
				hierarchicalUnitTemporaryCtrl.setValue(null);
				professionalReplacedIdCtrl.setValidators([Validators.required]);
				hierarchicalUnitTemporaryCtrl.setValidators([Validators.required]);
				hierarchicalUnitCtrl.setValue(null);
			}
			else {
				professionalReplacedIdCtrl.clearValidators();
				hierarchicalUnitTemporaryCtrl.clearValidators();
				this.setHierarchicalUnits(this.form.value.healthcareProfessionalId);
			}
			professionalReplacedIdCtrl.updateValueAndValidity();
			hierarchicalUnitTemporaryCtrl.updateValueAndValidity();
		});

		this.form.controls.healthcareProfessionalId.valueChanges.pipe(filter(healthcareProfessionalId => !!healthcareProfessionalId)).subscribe((healthcareProfessionalId: number) => {
			if (!this.form.value.temporaryReplacement) {
				this.form.controls.hierarchicalUnitTemporary.setValue(null);
				this.setHierarchicalUnits(healthcareProfessionalId);
			}
		});

		this.form.controls.professionalReplacedId.valueChanges.pipe(filter(healthcareProfessionalId => !!healthcareProfessionalId)).subscribe((professionalReplacedId: number) => {
			this.form.controls.hierarchicalUnit.setValue(null);
			if (this.form.value.temporaryReplacement) {
				this.form.controls.hierarchicalUnitTemporary.setValue(null);
				this.setHierarchicalUnits(professionalReplacedId);
			}
		});


		combineLatest([
			this.form.controls.healthcareProfessionalId.valueChanges.pipe(filter(id => !!id)),
			this.form.controls.professionalReplacedId.valueChanges.pipe(filter(id => !!id))
		]).subscribe(([healthcareProfessionalId, professionalReplacedId]) => {
			const temporaryReplacement = this.form.value.temporaryReplacement;

			if (!temporaryReplacement) {
				this.updateHierarchicalUnitTemporary(null, healthcareProfessionalId);
			} else {
				this.updateHierarchicalUnitTemporary(null, professionalReplacedId);
			}

			if (!temporaryReplacement) {
				this.updateHierarchicalUnit(null, healthcareProfessionalId);
			}
		});


		this.route.data.subscribe(data => {
			if (data["editMode"]) {
				this.editMode = true;
				this.route.paramMap.subscribe((params) => {
					this.editingDiaryId = Number(params.get('agendaId'));
					this.diaryService.get(this.editingDiaryId).subscribe((diary: CompleteDiaryDto) => {
						this.minDate = momentParseDate(diary.startDate).toDate();
						this.setValuesFromExistingAgenda(diary);
						this.disableNotEditableControls();
						this.validateLineOfCareAndPercentageOfProtectedAppointments();
						if (this.lineOfCareAndPercentageOfProtectedAppointmentsValid) {
							this.form.controls.protectedAppointmentsPercentage.enable();
						}
					});

				});
			}
		});
		this.sectorService.getAll().subscribe(data => {
			this.sectors = data;
		});

		this.healthcareProfessionalService.getAll().subscribe(data => this.professionals = data);

	}

	private updateHierarchicalUnitTemporary(value: any, id: number): void {
		this.form.controls.hierarchicalUnitTemporary.setValue(value);
		this.setHierarchicalUnits(id);
	}

	private updateHierarchicalUnit(value: any, id: number): void {
		this.form.controls.hierarchicalUnit.setValue(value);
		this.setHierarchicalUnits(id);
	}
	get careLinesAssociated(): UntypedFormControl {
		return this.form.get('careLines') as UntypedFormControl;
	}

	private setValuesFromExistingAgenda(diary: CompleteDiaryDto): void {

		if (diary.predecessorProfessionalId) {
			this.temporary = true;
			this.form.controls.professionalReplacedId.setValue(diary.predecessorProfessionalId);
			this.form.controls.hierarchicalUnitTemporary.setValue(this.hierarchicalUnits.filter(e => e.name === diary.hierarchicalUnitAlias));
			this.form.controls.temporaryReplacement.setValue(true);
			this.form.get('professionalReplacedId').disable();
			this.form.get('hierarchicalUnit').disable();
		}
		this.form.controls.sectorId.setValue(diary.sectorId);
		this.doctorsOfficeService.getAll(diary.sectorId)
			.subscribe((doctorsOffice: DoctorsOfficeDto[]) => {
				this.doctorOffices = doctorsOffice;
				const office = doctorsOffice.find(o => o.id === diary.doctorsOfficeId);
				this.form.controls.doctorOffice.setValue(office);
				this.setDoctorOfficeRangeTime();
				this.setAllWeeklyDoctorsOfficeOcupation();
			});

		this.healthcareProfessionalService.getAll().subscribe(healthcareProfessionals => {
			this.professionals = healthcareProfessionals;
			const healthcareProfessionalId = healthcareProfessionals.find(professional => professional.id === diary.healthcareProfessionalId);
			this.form.controls.healthcareProfessionalId.setValue(healthcareProfessionalId.id);

			this.setHierarchicalUnitsByName(diary?.hierarchicalUnitAlias);

			this.specialtyService.getAllSpecialtyByProfessional(this.contextService.institutionId, healthcareProfessionalId.id)
				.subscribe(response => {
					this.professionalSpecialties = response;
					this.form.controls.healthcareProfessionalSpecialtyId.markAsTouched();
					if (this.professionalSpecialties.find(specialty => specialty.id === diary.clinicalSpecialtyId)) {
						this.form.controls.healthcareProfessionalSpecialtyId.setValue(diary.clinicalSpecialtyId);
						this.getCareLines();
					}

				})
		});

		this.form.controls.healthcareProfessionalId.setValue(diary.healthcareProfessionalId);
		this.form.controls.startDate.setValue(momentParseDate(diary.startDate));
		this.form.controls.endDate.setValue(momentParseDate(diary.endDate));
		this.form.controls.appointmentDuration.setValue(diary.appointmentDuration);

		this.agendaHorarioService.setAppointmentDuration(diary.appointmentDuration);

		this.appointmentManagement = diary.professionalAssignShift;
		this.autoRenew = diary.automaticRenewal;
		this.holidayWork = diary.includeHoliday;

		this.agendaHorarioService.setDiaryOpeningHours(diary.diaryOpeningHours);

		if (diary.associatedProfessionalsInfo.length > 0) {
			const professionalsReference = this.form.controls.otherProfessionals as UntypedFormArray;
			this.form.controls.conjointDiary.setValue(true);
			diary.associatedProfessionalsInfo.forEach(diaryAssociatedProfessional => {
				professionalsReference.push(this.initializeAnotherProfessional());
				professionalsReference.controls[professionalsReference.length - 1].setValue({ healthcareProfessionalId: diaryAssociatedProfessional.id });
			});
		}

		if (diary.alias) {
			this.form.controls.alias.setValue(diary.alias);
		}

		if (diary.protectedAppointmentsPercentage)
			this.form.controls.protectedAppointmentsPercentage.setValue(diary.protectedAppointmentsPercentage);

		if (diary.careLinesInfo.length)
			this.careLinesSelected = diary.careLinesInfo;

		this.setSpecialityId(diary.clinicalSpecialtyId);
		this.setAlias(diary.alias);
	}

	private setHierarchicalUnitsByName(name: string) {
		this.loadSavedData = true;
		this.form.controls.hierarchicalUnit.setValue(this.hierarchicalUnits.filter(e => e.name === name));
		this.form.controls.professionalReplacedId.updateValueAndValidity();
	}

	private setSpecialityId(healthcareProfesionalId) {
		this.specialityId = healthcareProfesionalId;
	}

	private setAlias(alias) {
		this.alias = alias;
	}

	private disableNotEditableControls(): void {
		this.form.get('healthcareProfessionalId').disable();
		this.form.get('appointmentDuration').disable();
		this.form.get('hierarchicalUnit').disable();

	}

	setDoctorOfficesAndResetFollowingControls(sectorId: number): void {
		this.doctorsOfficeService.getAll(sectorId).subscribe((data: DoctorsOfficeDto[]) => this.doctorOffices = data);
		this.form.get('doctorOffice').reset();
	}

	loadCalendar(): void {
		this.setDoctorOfficeRangeTime();
		this.setAllWeeklyDoctorsOfficeOcupation();
	}

	private setDoctorOfficeRangeTime() {
		this.openingTime = getHours(this.form.getRawValue().doctorOffice.openingTime);
		this.closingTime = getHours(this.form.getRawValue().doctorOffice.closingTime);
		function getHours(time: string): number {
			const hours = momentParseTime(time);
			return Number(hours.hours());
		}
	}

	getFullName(professional: ProfessionalDto): string {
		return `${professional.lastName} ${this.patientNameService.getPatientName(professional.firstName, professional.nameSelfDetermination)}`;
	}

	appointmentManagementChange(): void {
		this.appointmentManagement = !this.appointmentManagement;
	}

	autoRenewChange(): void {
		this.autoRenew = !this.autoRenew;
	}

	holidayWorkChange(): void {
		this.holidayWork = !this.holidayWork;
	}

	save(): void {
		if (this.form.valid && this.lineOfCareAndPercentageOfProtectedAppointmentsValid) {
			this.openDialog();
		} else {
			scrollIntoError(this.form, this.el);
		}
	}

	openDialog(): void {
		const confirmMessage = this.editMode ? 'turnos.agenda-setup.CONFIRM_EDIT_AGENDA' : 'turnos.agenda-setup.CONFIRM_NEW_AGENDA';
		this.translator.get(confirmMessage).subscribe((res: string) => {
			const dialogRef = this.dialog.open(ConfirmDialogComponent, {
				width: '450px',
				data: {
					title: this.editMode ? 'Editar agenda' : 'Nueva agenda',
					content: `${res}`,
					okButtonLabel: 'Confirmar'
				}
			});

			dialogRef.afterClosed().subscribe(confirmed => {
				if (confirmed) {
					this.errors = [];
					if (this.editMode) {
						const agendaEdit: DiaryDto = this.addAgendaId(this.buildDiaryADto());
						this.diaryService.updateDiary(agendaEdit)
							.subscribe((agendaId: number) => {
								this.processSuccess(agendaId);
							}, error => processErrors(error, (msg) => this.errors.push(msg)));
					} else {
						const agenda: DiaryADto = this.buildDiaryADto();
						this.diaryService.addDiary(agenda)
							.subscribe((agendaId: number) => {
								this.processSuccess(agendaId);
							}, error => processErrors(error, (msg) => this.errors.push(msg)));
					}
				}
			});
		});
	}

	setAllWeeklyDoctorsOfficeOcupation(): void {
		if (!this.form.controls['startDate'].valid && !this.form.controls['endDate'].valid) {
			return;
		}
		const formValue = this.form.getRawValue();
		if (!formValue.doctorOffice || !formValue.startDate || !formValue.endDate) {
			return;
		}

		const startDate = momentFormat(formValue.startDate, DateFormat.API_DATE);
		const endDate = momentFormat(formValue.endDate, DateFormat.API_DATE);

		const ocupations$: Observable<OccupationDto[]> = this.diaryOpeningHoursService
			.getAllWeeklyDoctorsOfficeOcupation(formValue.doctorOffice.id, this.editingDiaryId, startDate, endDate);

		this.agendaHorarioService.setWeeklyOcupation(ocupations$);
	}

	private processSuccess(agendaId: number) {
		if (agendaId) {
			this.snackBarService.showSuccess('turnos.agenda-setup.messages.SUCCESS');
			const url = `${this.routePrefix}${ROUTE_APPOINTMENT}/${ROUTE_AGENDAS}/${agendaId}`;
			this.router.navigate([url]);
		}
	}

	private addAgendaId(diary: any): DiaryDto {
		diary.id = this.editingDiaryId;
		return diary;
	}

	private buildDiaryADto(): DiaryADto {
		const percentage = this.form.value.protectedAppointmentsPercentage;
		return {

			appointmentDuration: this.form.getRawValue().appointmentDuration,
			healthcareProfessionalId: this.form.getRawValue().healthcareProfessionalId,
			doctorsOfficeId: this.form.getRawValue().doctorOffice.id,

			predecessorProfessionalId: this.form.value?.professionalReplacedId,
			hierarchicalUnitId: this.form.value.temporaryReplacement ? this.form.value?.hierarchicalUnitTemporary : this.form.value?.hierarchicalUnit,

			startDate: momentFormat(this.form.value.startDate, DateFormat.API_DATE),
			endDate: momentFormat(this.form.value.endDate, DateFormat.API_DATE),

			automaticRenewal: this.autoRenew,
			includeHoliday: this.holidayWork,
			professionalAssignShift: this.appointmentManagement,

			diaryOpeningHours: this.agendaHorarioService.getDiaryOpeningHours(),
			clinicalSpecialtyId: this.form.value.healthcareProfessionalSpecialtyId,
			alias: this.form.value.alias === "" ? null : this.form.value.alias,
			diaryAssociatedProfessionalsId: this.form.value.otherProfessionals.map(professional => professional.healthcareProfessionalId),
			careLines: this.careLinesSelected.map(careLine => { return careLine.id }),
			protectedAppointmentsPercentage: percentage ? percentage : 0
		};
	}

	scrollToDefaultStartingHour(): void {
		this.agendaHorarioService.setAppointmentDuration(this.form.getRawValue().appointmentDuration);

		const scrollbar = document.getElementsByClassName('cal-time-events')[0];
		scrollbar?.scrollTo(0, this.PIXEL_SIZE_HEIGHT * this.TURN_STARTING_HOUR * this.hourSegments);
	}

	setHierarchicalUnits(healthcareProfessionalId: number) {
		return this.professionalService.geUserIdByHealthcareProfessional(healthcareProfessionalId).pipe(
			switchMap((userId: number) =>
				this.hierarchicalUnitsService.fetchAllByUserIdAndInstitutionId(userId)
			)
		).subscribe(response => {
			this.hierarchicalUnits = response;
			if (this.hierarchicalUnits.length) {
				if (!this.form.value.temporaryReplacement) {
					this.form.controls.hierarchicalUnit.setValue(this.hierarchicalUnits[0].id);
					this.form.controls.hierarchicalUnit.updateValueAndValidity();
				}
				if (this.form.value.temporaryReplacement) {
					this.form.controls.hierarchicalUnitTemporary.setValue(this.hierarchicalUnits[0].id);
					this.form.controls.hierarchicalUnitTemporary.updateValueAndValidity();
				}
			}
		});
	}

	getProfessionalSpecialties() {
		this.resetValues();
		this.specialtyService.getAllSpecialtyByProfessional(this.contextService.institutionId, this.form.get("healthcareProfessionalId").value)
			.subscribe(response => this.professionalSpecialties = response)
	}

	getProfessionalReplacement() {
		this.specialtyService.getAllSpecialtyByProfessional(this.contextService.institutionId, this.form.get("healthcareProfessionalId").value)
			.subscribe(response => this.professionalSpecialties = response)
	}

	getControl(key: string): any {
		return this.form.get(key);
	}

	addAssociatedProfessional() {
		const currentOtherProfessionals = this.form.controls.otherProfessionals as UntypedFormArray;
		currentOtherProfessionals.push(this.initializeAnotherProfessional());
	}

	private initializeAnotherProfessional(): UntypedFormGroup {
		return new UntypedFormGroup({
			healthcareProfessionalId: new UntypedFormControl(null, [Validators.required]),
		});
	}

	clear(i: number): void {
		const professionalsReference = this.form.controls.otherProfessionals as UntypedFormArray;
		if (professionalsReference.length === 1) {
			professionalsReference.controls[0].setValue({ healthcareProfessionalId: null });
		}
		else {
			professionalsReference.removeAt(i);
		}
	}

	updateAssociatedProfessionalsForm() {
		let professionalsReference = this.form.controls.otherProfessionals as UntypedFormArray;
		if (this.form.value.conjointDiary === true) {
			professionalsReference.push(this.initializeAnotherProfessional());
		}
		else {
			professionalsReference.clear();
			this.form.controls.conjointDiary.setValue(false);
		}
	}

	updateTemporaryReplacementForm() {
		if (!this.form.value.temporaryReplacement) {
			this.form.controls.professionalReplacedId.setValue(null);
			this.form.controls.temporaryReplacement.setValue(false);
		}
	}

	private otherPossibleProfessionals(): ValidatorFn {
		return (control: UntypedFormArray): ValidationErrors | null => {
			return control.valid ? null : { validProfessionals: { valid: false } };
		};
	}

	validExtraProfessionalsList(): boolean {
		const professionalsReference = this.form.controls.otherProfessionals as UntypedFormArray;
		return professionalsReference.valid;
	}

	isProfessionalNonSelectable(professionalId: number): boolean {
		const professionalsReference = this.form.controls.otherProfessionals as UntypedFormArray;
		return professionalsReference.value.map(professional => professional.healthcareProfessionalId).includes(professionalId);
	}

	getNonResponsibleProfessional(): ProfessionalDto[] {
		return this.professionals?.filter(professional => professional.id !== this.form.controls.healthcareProfessionalId.value);
	}

	getCareLines() {
		const specialtyId = this.form.get("healthcareProfessionalSpecialtyId").value;
		this.carelineService.getCareLinesBySpecialty(specialtyId).subscribe(careLines => {
			this.careLines = careLines;
			this.checkCareLinesSelected();
		});
		if (specialtyId && specialtyId !== this.specialityId) {
			this.form.controls.alias.setValue('');
		} else {
			this.form.controls.alias.setValue(this.alias);
		}
	}

	validateLineOfCareAndPercentageOfProtectedAppointments() {
		if (this.form.controls.protectedAppointmentsPercentage.value !== 0 && !this.careLinesSelected.length) {
			this.lineOfCareAndPercentageOfProtectedAppointmentsValid = false;
			this.form.controls.protectedAppointmentsPercentage.enable();
		} else {
			this.lineOfCareAndPercentageOfProtectedAppointmentsValid = true;
		}
	}

	setCareLines(careLines: string[]) {
		if (careLines.length) {
			if (!this.form.controls.protectedAppointmentsPercentage.hasValidator(Validators.required)) {
				this.addValidators();
			}
			this.form.controls.protectedAppointmentsPercentage.enable();
			this.careLinesSelected = careLines.map(careLine => ({
				id: this.careLines.find(c => c.description === careLine).id,
				description: careLine,
				clinicalSpecialties: this.careLines.find(c => c.description === careLine).clinicalSpecialties
			}));
		}
		else {
			this.form.controls.protectedAppointmentsPercentage.removeValidators([Validators.required]);
			this.form.controls.protectedAppointmentsPercentage.disable();
			this.form.controls.protectedAppointmentsPercentage.setValue(0);
			this.careLinesSelected = [];
		}
		this.form.controls.protectedAppointmentsPercentage.updateValueAndValidity();
		this.validateLineOfCareAndPercentageOfProtectedAppointments();
	}

	private checkCareLinesSelected() {
		const careLinesToSelect: CareLineDto[] = [];
		this.careLinesSelected.forEach(careLineSelected => {
			if (this.careLines.some(careLine => careLine.id === careLineSelected.id)) {
				careLinesToSelect.push(careLineSelected);
			}
		});
		this.careLinesSelected = careLinesToSelect;
		const careLinesDescription = this.careLinesSelected.map(careLine => careLine.description);
		this.form.controls.careLines.patchValue(careLinesDescription);
	}

	private addValidators() {
		this.form.controls.protectedAppointmentsPercentage.addValidators([Validators.required]);
		this.form.controls.protectedAppointmentsPercentage.markAsTouched();
	}

	private resetValues() {
		this.form.controls.healthcareProfessionalSpecialtyId.reset();
		this.form.controls.careLines.reset();
		this.form.controls.protectedAppointmentsPercentage.removeValidators([Validators.required]);
		this.form.controls.protectedAppointmentsPercentage.updateValueAndValidity();
	}
}
