import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { NewAppointmentComponent } from '@turnos/dialogs/new-appointment/new-appointment.component';
import { ConfirmDialogComponent } from '@presentation/dialogs/confirm-dialog/confirm-dialog.component';
import { EmptyAppointmentDto } from '@api-rest/api-model';
import { DatePipeFormat } from '@core/utils/date.utils';
import { DatePipe } from '@angular/common';

@Component({
	selector: 'app-appointment-details',
	templateUrl: './appointment-details.component.html',
	styleUrls: ['./appointment-details.component.scss'],
})
export class AppointmentDetailsComponent implements OnInit {

	@Input() emptyAppointment: EmptyAppointmentDto;
	@Input() patientId: number;
	@Output() resetAppointmentList = new EventEmitter<void>();
	appointmentTime: Date = new Date();

	constructor(
		private readonly dialog: MatDialog,
		private readonly datePipe: DatePipe
	) {}

	ngOnInit(): void {
		const timeData = this.emptyAppointment.hour.split(":");
		this.appointmentTime.setHours(+timeData[0]);
		this.appointmentTime.setMinutes(+timeData[1]);
		this.appointmentTime.setSeconds(+timeData[2]);
	}

	assignAppointment() {
		const dialogReference = this.dialog.open(NewAppointmentComponent, {
			width: '35%',
			data: {
				date: this.emptyAppointment.date,
				diaryId: this.emptyAppointment.diaryId,
				hour: this.emptyAppointment.hour,
				openingHoursId: this.emptyAppointment.openingHoursId,
				overturnMode: false,
				patientId: this.patientId ? this.patientId : null,
			}
		});
		dialogReference.afterClosed().subscribe(
			(result: boolean) => {
				if (result) {
					this.resetAppointmentList.emit();

					var fullAppointmentDate = this.datePipe.transform(this.emptyAppointment.date, DatePipeFormat.FULL_DATE);
					fullAppointmentDate = fullAppointmentDate[0].toUpperCase() + fullAppointmentDate.slice(1);
					const timeData = this.emptyAppointment.hour.split(":");

					const specialtyAndAlias = this.emptyAppointment.clinicalSpecialtyName ? this.emptyAppointment.clinicalSpecialtyName :
					`${this.emptyAppointment.alias} (${this.emptyAppointment.clinicalSpecialtyName})`;

					this.dialog.open(ConfirmDialogComponent, {
						data: {
							title: 'turnos.new-appointment.ASSIGNED_APPOINTMENT',
							content: `${this.emptyAppointment.doctorFirstName} ${this.emptyAppointment.doctorLastName}
							- ${specialtyAndAlias}
							- ${fullAppointmentDate}
							- ${timeData[0]}:${timeData[1]}hs`,
							okButtonLabel: 'turnos.new-appointment.ACCEPT'
						}
					});
				}
			}
		);
	}

}