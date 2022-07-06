import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { AppointmentDailyAmountDto, AppointmentListDto, CompleteDiaryDto, DiaryOpeningHoursDto, MedicalCoverageDto } from '@api-rest/api-model';
import { ERole } from '@api-rest/api-model';
import { CalendarMonthViewBeforeRenderEvent, CalendarView, CalendarWeekViewBeforeRenderEvent, DAYS_OF_WEEK } from 'angular-calendar';
import {
	buildFullDate,
	DateFormat,
	dateToMoment,
	dateToMomentTimeZone,
	momentParseDate,
	momentParseTime,
	newMoment
} from '@core/utils/moment.utils';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { DiaryService } from '@api-rest/services/diary.service';
import { Moment } from 'moment';
import { NewAppointmentComponent } from '../../dialogs/new-appointment/new-appointment.component';
import { CalendarEvent, MonthViewDay, WeekViewHourSegment } from 'calendar-utils';
import { MEDICAL_ATTENTION } from '../../constants/descriptions';
import { AppointmentComponent } from '../../dialogs/appointment/appointment.component';
import { SnackBarService } from '@presentation/services/snack-bar.service';

import { AppointmentsFacadeService, getColor, getSpanColor, toCalendarEvent } from '@turnos/services/appointments-facade.service';

import { APPOINTMENT_STATES_ID, MINUTES_IN_HOUR } from '../../constants/appointment';
import { map, take } from 'rxjs/operators';
import { forkJoin, Observable, of, Subject, Subscription } from 'rxjs';
import { PermissionsService } from '@core/services/permissions.service';
import { HealthInsuranceService } from '@api-rest/services/health-insurance.service';
import { AppointmentsService } from '@api-rest/services/appointments.service';
import { AgendaSearchService } from '../../services/agenda-search.service';
import { ContextService } from '@core/services/context.service';
import { ConfirmBookingComponent } from '@turnos/dialogs/confirm-booking/confirm-booking.component';
import { DatePipeFormat } from '@core/utils/date.utils';

const ASIGNABLE_CLASS = 'cursor-pointer';
const AGENDA_PROGRAMADA_CLASS = 'bg-green';
const AGENDA_ESPONTANEA_CLASS = 'bg-yellow';
const ROLES_TO_CREATE: ERole[] = [ERole.ADMINISTRATIVO, ERole.ESPECIALISTA_MEDICO, ERole.PROFESIONAL_DE_SALUD, ERole.ENFERMERO, ERole.ESPECIALISTA_EN_ODONTOLOGIA];
@Component({
	selector: 'app-agenda',
	templateUrl: './agenda.component.html',
	styleUrls: ['./agenda.component.scss'],
})
export class AgendaComponent implements OnInit, OnDestroy, OnChanges {

	readonly calendarViewEnum = CalendarView;
	readonly MONDAY = DAYS_OF_WEEK.MONDAY;
	readonly dateFormats = DatePipeFormat;
	hasRoleToCreate: boolean;

	hourSegments: number;
	agenda: CompleteDiaryDto;

	viewDate: Date = new Date();
	loading = true;
	dayStartHour: number;
	dayEndHour: number;
	diaryOpeningHours: DiaryOpeningHoursDto[];

	enableAppointmentScheduling = true;
	appointments: CalendarEvent[];
	dailyAmounts: AppointmentDailyAmountDto[];
	dailyAmounts$: Observable<AppointmentDailyAmountDto[]>;
	appointmentSubscription: Subscription;
	refreshCalendar = new Subject<void>();

	private readonly routePrefix = 'institucion/' + this.contextService.institutionId;
	private patientId: number;
	@Input() professionalId: number;
	@Input() canCreateAppoinment = true;
	@Input() idAgenda: number;
	@Input() showAll = true;
	@Input() view: CalendarView = CalendarView.Week;

	constructor(
		private readonly dialog: MatDialog,
		private readonly diaryService: DiaryService,
		private readonly route: ActivatedRoute,
		private readonly router: Router,
		private snackBarService: SnackBarService,
		private readonly permissionsService: PermissionsService,
		public readonly appointmentFacade: AppointmentsFacadeService,
		private readonly appointmentsService: AppointmentsService,
		private readonly healthInsuranceService: HealthInsuranceService,
		private readonly agendaSearchService: AgendaSearchService,
		private readonly contextService: ContextService,
	) {
	}

	ngOnInit(): void {
		this.route.queryParams.subscribe(qp => {
			this.patientId = Number(qp.idPaciente);
			if (!this.professionalId)
				this.professionalId = Number(qp.idProfessional);
			this.appointmentFacade.setProfessionalId(this.professionalId);
		});
		this.dayStartHour = 8;
		this.dayEndHour = 21;
		this.loading = true;
		this.appointmentSubscription?.unsubscribe();
		this.appointmentFacade.clear();
		if (!this.idAgenda)
			this.route.paramMap.subscribe((params: ParamMap) => {
				this.idAgenda = Number(params.get('idAgenda'));
				this.getAgenda();
			});
		else
			this.getAgenda();

		this.appointmentSubscription = this.appointmentFacade.getAppointments().subscribe(appointments => {
			if (appointments) {
				this.appointments = this.unifyEvents(appointments);
				this.dailyAmounts$ = this.appointmentsService.getDailyAmounts(this.idAgenda);
				this.loading = false;
			}
		});
		this.appointmentFacade.setInterval();
		this.permissionsService.hasContextAssignments$(ROLES_TO_CREATE).subscribe(hasRole => this.hasRoleToCreate = hasRole);
	}

	ngOnDestroy() {
		this.agendaSearchService.setAgendaSelected(undefined);
	}

	ngOnChanges(changes: SimpleChanges) {
		if (changes.idAgenda.currentValue)
			this.getAgenda();
	}

	loadCalendar(renderEvent: CalendarWeekViewBeforeRenderEvent) {
		renderEvent.hourColumns.forEach((hourColumn) => {
			const openingHours: DiaryOpeningHoursDto[] = this._getOpeningHoursFor(hourColumn.date);
			if (openingHours.length) {
				hourColumn.hours.forEach((hour) => {
					hour.segments.forEach((segment) => {
						openingHours.forEach(openingHour => {
							const from: Moment = momentParseTime(openingHour.openingHours.from);
							const to: Moment = momentParseTime(openingHour.openingHours.to);
							if (isBetween(segment, from, to)) {
								segment.cssClass = this.getOpeningHoursCssClass(openingHour);
							}
						});
					});
				});
			}
		});

		function isBetween(segment: WeekViewHourSegment, from: Moment, to: Moment) {
			return ((segment.date.getHours() > from.hours()) ||
				(segment.date.getHours() === from.hours() && segment.date.getMinutes() >= from.minutes()))
				&& ((segment.date.getHours() < to.hours()) ||
					(segment.date.getHours() === to.hours() && segment.date.getMinutes() < to.minutes()));
		}
	}

	loadDailyAmounts(calendarMonthViewBeforeRenderEvent: CalendarMonthViewBeforeRenderEvent): void {
		const daysCells: MonthViewDay[] = calendarMonthViewBeforeRenderEvent.body;
		if (this.appointments) {
			if (agendaOverlapsWithViewRange(this.agenda.startDate, this.agenda.endDate)) {
				this.dailyAmounts$.subscribe(dailyAmounts => {
					if (dailyAmounts) {
						this.dailyAmounts = dailyAmounts;
						this.dailyAmounts$ = of(null);
					}
					this.setDailyAmounts(daysCells, this.dailyAmounts);
				});
			}
		}

		function agendaOverlapsWithViewRange(start: string, end: string): boolean {
			const viewPeriod = calendarMonthViewBeforeRenderEvent.period;
			const startAgenda = momentParseDate(start);
			const endAgenda = momentParseDate(end);

			const firstViewDay = dateToMomentTimeZone(viewPeriod.start);
			const lastViewDay = dateToMomentTimeZone(viewPeriod.end);

			return (startAgenda.isSameOrBefore(lastViewDay, 'days') ||
				firstViewDay.isSameOrBefore(endAgenda, 'days'));
		}

	}

	private setDailyAmounts(daysCells: MonthViewDay[], dailyAmounts: AppointmentDailyAmountDto[]) {
		daysCells.forEach((cell: MonthViewDay) => {
			const amount = getAmount(cell.date);
			if (amount) {
				cell.meta = {
					amount
				};
			}

			function getAmount(date: Date): AppointmentDailyAmountDto {
				return dailyAmounts
					.find(dailyAmount => {
						return dateToMoment(date).isSame(momentParseDate(dailyAmount.date), 'days');
					});
			}
		});
	}

	private getAgenda() {
		this.diaryService.get(this.idAgenda).subscribe(agenda => {
			this.setAgenda(agenda);
			this.agendaSearchService.setAgendaSelected(agenda);
		}, _ => {
			this.snackBarService.showError('turnos.home.AGENDA_NOT_FOUND');
			this.router.navigateByUrl(`${this.routePrefix}/turnos`);
		});
	}

	onClickedSegment(event) {
		this.appointmentsService.getList([this.agenda.id], this.professionalId)
			.subscribe((appointments: AppointmentListDto[]) => {
				const appointmentsCalendarEvents: CalendarEvent[] = appointments
					.map(appointment => {
						const from = momentParseTime(appointment.hour).format(DateFormat.HOUR_MINUTE);
						const to = momentParseTime(from).add(this.agenda.appointmentDuration, 'minutes').format(DateFormat.HOUR_MINUTE);
						return toCalendarEvent(from, to, momentParseDate(appointment.date), appointment, '');
					});

				this.appointmentFacade.loadAppointments();

				if (this.getOpeningHoursId(event.date) && this.enableAppointmentScheduling) {
					const clickedDate: Moment = dateToMomentTimeZone(event.date);
					const openingHourId: number = this.getOpeningHoursId(event.date);
					const diaryOpeningHourDto: DiaryOpeningHoursDto =
						this.diaryOpeningHours.find(diaryOpeningHour => diaryOpeningHour.openingHours.id === openingHourId);

					forkJoin([
						this.getAppointmentAt(event.date).pipe(take(1)),
						this.allOverturnsAssignedForDiaryOpeningHour(diaryOpeningHourDto, clickedDate).pipe(take(1))
					]).subscribe(([busySlot, numberOfOverturnsAssigned]) => {

						if (busySlot && busySlot.meta.appointmentStateId === APPOINTMENT_STATES_ID.BLOCKED) {
							this.snackBarService.showError('No es posible agragar un turno en un horario bloqueado');
							return;
						}
						const addingOverturn = !!busySlot;

						if (addingOverturn && (numberOfOverturnsAssigned === diaryOpeningHourDto.overturnCount)) {
							if (diaryOpeningHourDto.medicalAttentionTypeId !== MEDICAL_ATTENTION.SPONTANEOUS_ID) {
								this.snackBarService.showError('turnos.overturns.messages.ERROR');
							}
						} else {
							this.dialog.open(NewAppointmentComponent, {
								width: '35%',
								data: {
									date: clickedDate.format(DateFormat.API_DATE),
									diaryId: this.agenda.id,
									hour: clickedDate.format(DateFormat.HOUR_MINUTE_SECONDS),
									openingHoursId: openingHourId,
									overturnMode: addingOverturn,
									patientId: this.patientId,
								}
							});
						}
					});
				}

			});

	}

	viewAppointment(event: CalendarEvent): void {
		if (event.meta.appointmentStateId === APPOINTMENT_STATES_ID.BLOCKED) {
			return;
		}
		if (!event.meta.patient?.id) {
			this.dialog.open(ConfirmBookingComponent, {
				width: '30%',
				data: {
					date: event.meta.date.format(DateFormat.API_DATE),
					diaryId: this.agenda.id,
					hour: event.meta.date.format(DateFormat.HOUR_MINUTE_SECONDS),
					openingHoursId: this.getOpeningHoursId(event.meta.date.toDate()),
					overturnMode: false,
					identificationTypeId: event.meta.patient.typeId ? event.meta.patient.typeId : 1,
					idNumber: event.meta.patient.identificationNumber,
					appointmentId: event.meta.appointmentId,
					phoneNumber: event.meta.phoneNumber
				}
			});
		} else {
			if (event.meta.rnos) {
				this.healthInsuranceService.get(event.meta.rnos)
					.subscribe((medicalCoverageDto: MedicalCoverageDto) => {
						event.meta.healthInsurance = medicalCoverageDto;
						this.dialog.open(AppointmentComponent, {
							data: {
								appointmentData: event.meta,
								professionalPermissions: this.agenda.professionalAssignShift
							}
						});

					});
			} else {
				this.dialog.open(AppointmentComponent, {
					data: {
						appointmentData: event.meta,
						hasPermissionToAssignShift: this.agenda.professionalAssignShift
					},
				});
			}
		}
	}

	setAgenda(agenda: CompleteDiaryDto): void {
		//delete this.dayEndHour;
		//delete this.dayStartHour;
		this.agenda = agenda;
		this.setEnableAppointmentScheduling();
		this.viewDate = this._getViewDate();
		this.hourSegments = MINUTES_IN_HOUR / agenda.appointmentDuration;
		this.appointmentFacade.setValues(agenda.id, agenda.appointmentDuration);
		this.diaryOpeningHours = agenda.diaryOpeningHours;
		//this.setDayStartHourAndEndHour(agenda.diaryOpeningHours);

	}

	goToDayViewOn(date: Date) {
		this.viewDate = date;
		this.view = this.calendarViewEnum.Day;
	}

	goToDiary() {
		const url = `institucion/${this.contextService.institutionId}/turnos/agenda/${this.idAgenda}`;
		this.router.navigate([url]);
	}

	private setDayStartHourAndEndHour(openingHours: DiaryOpeningHoursDto[]) {
		openingHours.forEach(oh => {
			const from = momentParseTime(oh.openingHours.from).hour();
			if (this.dayStartHour === undefined || from < this.dayStartHour) {
				this.dayStartHour = (from > 0) ? from - 1 : from;
			}
			const to = momentParseTime(oh.openingHours.to).hour();
			const toMinutes = momentParseTime(oh.openingHours.to).minutes();
			if (this.dayEndHour === undefined || to >= this.dayEndHour) {
				this.dayEndHour = (toMinutes > 0) ? to : to - 1;
			}
		});
	}

	/**
	 * returns a Date that defines which week is going to show in the header of the calendar
	 *
	 */
	private _getViewDate(): Date {
		const momentStartDate = momentParseDate(this.agenda.startDate);
		const momentEndDate = momentParseDate(this.agenda.endDate);
		const today = newMoment();

		if (today.isBetween(momentStartDate, momentEndDate)) {
			return new Date();
		}
		if (today.isBefore(momentStartDate)) {
			return momentStartDate.toDate();
		}
		return momentEndDate.toDate();
	}

	private getOpeningHoursId(date: Date): number {
		const openingHoursSelectedDay = this._getOpeningHoursFor(date);

		const selectedOpeningHour = openingHoursSelectedDay.find(oh => {
			const hourFrom = momentParseTime(oh.openingHours.from);
			const hourTo = momentParseTime(oh.openingHours.to);
			const selectedHour = dateToMomentTimeZone(date).format(DateFormat.HOUR_MINUTE_SECONDS);

			return momentParseTime(selectedHour).isBetween(hourFrom, hourTo, null, '[)');
		});

		return selectedOpeningHour?.openingHours.id;
	}

	private existsAppointmentAt(date: Date): Observable<boolean> {
		return this.appointmentFacade.getAppointments().pipe(
			map(array => {
				return array.filter(appointment => appointment.start.getTime() === date.getTime()).length > 0;
			})
		);
	}
	private getAppointmentAt(date: Date): Observable<CalendarEvent> {
		return this.appointmentFacade.getAppointments().pipe(
			map(array => {
				return array.find(appointment => appointment.start.getTime() === date.getTime());
			})
		);
	}

	private allOverturnsAssignedForDiaryOpeningHour(diaryOpeningHourDto: DiaryOpeningHoursDto, clickedDate: Moment): Observable<number> {

		const openingHourStart = buildFullDate(diaryOpeningHourDto.openingHours.from, clickedDate);
		const openingHourEnd = buildFullDate(diaryOpeningHourDto.openingHours.to, clickedDate);

		return this.appointmentFacade.getAppointments().pipe(
			map((array: CalendarEvent[]) =>
				array.filter(event =>
					event.meta.overturn && dateToMoment(event.start).isBetween(openingHourStart, openingHourEnd, null, '[)')
				).length
			)
		);
	}

	private _getOpeningHoursFor(date: Date): DiaryOpeningHoursDto[] {
		const dateMoment = dateToMoment(date);
		const start = momentParseDate(this.agenda.startDate);
		const end = momentParseDate(this.agenda.endDate);

		if (dateMoment.isBetween(start, end, 'date', '[]')) {
			return this.diaryOpeningHours.filter(oh => oh.openingHours.dayWeekId === date.getDay());
		}
		return [];
	}

	private setEnableAppointmentScheduling(): void {
		if (this.agenda.professionalAssignShift) {
			this.enableAppointmentScheduling = true;
		} else {
			this.permissionsService.hasContextAssignments$([ERole.ADMINISTRATIVO, ERole.ADMINISTRADOR_AGENDA])
				.subscribe(hasAdministrativeRole => {
					this.enableAppointmentScheduling = hasAdministrativeRole;
				});
		}
	}

	private getOpeningHoursCssClass(openingHour: DiaryOpeningHoursDto): string {
		if (openingHour.medicalAttentionTypeId === MEDICAL_ATTENTION.SPONTANEOUS_ID) {
			return this.enableAppointmentScheduling ? `${AGENDA_ESPONTANEA_CLASS} ${ASIGNABLE_CLASS}` : AGENDA_ESPONTANEA_CLASS;
		} else {
			return this.enableAppointmentScheduling ? `${AGENDA_PROGRAMADA_CLASS} ${ASIGNABLE_CLASS}` : AGENDA_PROGRAMADA_CLASS;
		}
	}

	private updateAppoinment(appointmentInformation) {
		const appointment = this.appointments.find(appointment => appointment.meta.appointmentId === appointmentInformation?.id);
		appointment.meta.appointmentStateId = appointmentInformation.stateId;
		const color = getColor(appointmentInformation);
		appointment.color.primary = color;
		appointment.color.secondary = color;
		appointment.cssClass = getSpanColor(appointmentInformation.stateId);
		this.refreshCalendar.next();
	}

	private unifyEvents(events: CalendarEvent[]): CalendarEvent[] {
		const notUnifiedEvents = events.sort(
			(firstEvent, secondEvent) => firstEvent.start.getTime() - secondEvent.start.getTime()
		);
		let unifiedEvents = [];
		let processedEvents = [notUnifiedEvents[0]];
		for (let currentNotUnifiedEvent = 1; currentNotUnifiedEvent < notUnifiedEvents.length; currentNotUnifiedEvent++) {
			if (notUnifiedEvents[currentNotUnifiedEvent-1].end.getTime() === notUnifiedEvents[currentNotUnifiedEvent].start.getTime()
			&& notUnifiedEvents[currentNotUnifiedEvent-1].title === notUnifiedEvents[currentNotUnifiedEvent].title)
				processedEvents.push(notUnifiedEvents[currentNotUnifiedEvent]);
			else {
				unifiedEvents.push(this.unifyBlockedEvents(processedEvents));
				processedEvents = [notUnifiedEvents[currentNotUnifiedEvent]];
			}
		}
		unifiedEvents.push(this.unifyBlockedEvents(processedEvents));
		return unifiedEvents;
	}

	private unifyBlockedEvents(events: CalendarEvent[]): CalendarEvent {
		return {
			...events[0],
			start: events[0]?.start,
			end: events[events.length - 1]?.end,
		};
	}

}
