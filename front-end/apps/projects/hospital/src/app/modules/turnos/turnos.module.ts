import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarDateFormatter, CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
// deps
import { PresentationModule } from '@presentation/presentation.module';
import { LazyMaterialModule } from '../lazy-material/lazy-material.module';
// routing
import { TurnosRoutingModule } from './turnos-routing.module';
import { AgendaComponent } from './routes/agenda/agenda.component';
import { AgendaSetupComponent } from './routes/agenda-setup/agenda-setup.component';
import { HomeComponent } from './routes/home/home.component';
// components
import { CalendarProfessionalViewComponent } from '@turnos/components/calendar-professional-view/calendar-professional-view.component';
import { SelectAgendaComponent } from './components/select-agenda/select-agenda.component';
import { DateRangeTimeFormComponent } from './components/date-range-time-form/date-range-time-form.component';
import { SeachAppointmentsByProfessionalComponent } from './components/seach-appointments-by-professional/seach-appointments-by-professional.component';
import { SearchAppointmentsBySpecialtyComponent } from './components/search-appointments-by-specialty/search-appointments-by-specialty.component';
import { AppointmentDetailsComponent } from './components/appointment-details/appointment-details.component';

// dialogs
import { AppointmentComponent } from './dialogs/appointment/appointment.component';
import { BlockAgendaRangeComponent } from './dialogs/block-agenda-range/block-agenda-range.component';
import { CalendarProfessionalViewDockPopupComponent } from './dialogs/calendar-professional-view-dock-popup/calendar-professional-view-dock-popup.component';
import { CancelAppointmentComponent } from './dialogs/cancel-appointment/cancel-appointment.component';
import { ConfirmBookingComponent } from './dialogs/confirm-booking/confirm-booking.component';
import { NewAppointmentComponent } from './dialogs/new-appointment/new-appointment.component';
import { NewAttentionComponent } from './dialogs/new-attention/new-attention.component';
// services
import { CustomDateFormatter } from './services/custom-date-formatter.service';


@NgModule({
	declarations: [
		// routing
		AgendaComponent,
		AgendaSetupComponent,
		HomeComponent,
		// components
		CalendarProfessionalViewComponent,
		SelectAgendaComponent,
		BlockAgendaRangeComponent,
		DateRangeTimeFormComponent,
		SeachAppointmentsByProfessionalComponent,
		SearchAppointmentsBySpecialtyComponent,
		AppointmentDetailsComponent,
		// dialogs
		AppointmentComponent,
		CalendarProfessionalViewDockPopupComponent,
		CancelAppointmentComponent,
		ConfirmBookingComponent,
		NewAppointmentComponent,
		NewAttentionComponent,
	],
	imports: [
		CommonModule,
		CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
		// routing
		TurnosRoutingModule,
		// deps
		PresentationModule,
		LazyMaterialModule,
	],
	exports: [
		CalendarProfessionalViewComponent
	],
	providers: [
		{
			provide: CalendarDateFormatter,
			useClass: CustomDateFormatter,
		}
	]
})
export class TurnosModule {
}
