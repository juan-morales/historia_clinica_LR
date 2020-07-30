import { ChangeDetectorRef } from '@angular/core';
import { fromEvent } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';
import { CalendarEvent, CalendarEventAction } from 'angular-calendar';
import { WeekViewHourSegment } from 'calendar-utils';
import { addDays, addMinutes, endOfWeek } from 'date-fns';
import { MEDICAL_ATTENTION } from '../constants/descriptions';
import { NewAttentionComponent } from '../dialogs/new-attention/new-attention.component';
import { MatDialog } from '@angular/material/dialog';

function floorToNearest(amount: number, precision: number) {
	return Math.floor(amount / precision) * precision;
  }

function ceilToNearest(amount: number, precision: number) {
	return Math.ceil(amount / precision) * precision;
  }

const colors: any = {
	blue: {
	  primary: '#0095E1',
	  secondary: '#B3DFFF',
	},
	green: {
		primary: '#4CAF50',
		secondary: '#C6E4C7',
	  },
};

export class NewAgendaService {

  constructor(
	  private readonly weekStartsOn,
	  private readonly viewDate: Date,
	  public events: CalendarEvent[],
	  private readonly dialog: MatDialog,
	  private readonly cdr: ChangeDetectorRef
  ) { }

  private appointmentDuration: number;

  private actions: CalendarEventAction[] = [
	{
		label: '<span class="material-icons">delete</span>',
		a11yLabel: 'Delete',
		onClick: ({ event }: { event: CalendarEvent }): void => {
			this.events = this.events.filter((iEvent) => iEvent !== event);
		},
	},
	{
		label: '<span class="material-icons">edit</span>',
		a11yLabel: 'Edit',
		onClick: ({ event }: { event: CalendarEvent }): void => {
			this.openConfirmDialogForEvent(event);
		},
	},
];

startDragToCreate(segment: WeekViewHourSegment, segmentElement: HTMLElement): void {
	const dragToSelectEvent: CalendarEvent = {
		id: this.events.length,
		title: '',
		start: segment.date,
		meta: {
			tmpEvent: true,
		},
		actions: this.actions,
	};

	this.events = [...this.events, dragToSelectEvent];

	fromEvent(document, 'mousemove')
		.pipe(
			finalize(() => {
				if (dragToSelectEvent.end) {
					this.openConfirmDialogForEvent(dragToSelectEvent);
				} else {
					this.removeTempEvent(dragToSelectEvent);
					this.refresh();
				}
			}),
			takeUntil(fromEvent(document, 'mouseup'))
		)
		.subscribe((mouseMoveEvent: MouseEvent) => {
			this.limitNewEventsEnd(dragToSelectEvent, mouseMoveEvent, segment, segmentElement);
			this.refresh();
		});


}


private limitNewEventsEnd(dragToSelectEvent: CalendarEvent, mouseMoveEvent: MouseEvent,
											   segment: WeekViewHourSegment, segmentElement: HTMLElement): void {

	const segmentPosition = segmentElement.getBoundingClientRect();
	const minutesDiff = ceilToNearest(mouseMoveEvent.clientY - segmentPosition.top, this.appointmentDuration);
	const daysDiff = floorToNearest(mouseMoveEvent.clientX - segmentPosition.left, segmentPosition.width) / segmentPosition.width;
	if (daysDiff > 0) {
		return;
	}

	const newEnd = addDays(addMinutes(segment.date, minutesDiff), daysDiff);
	const endOfView = endOfWeek(this.viewDate, {weekStartsOn: this.weekStartsOn});
	if (newEnd > segment.date && newEnd < endOfView) {
		const eventAlreadySetAt = this.events.filter(event => event !== dragToSelectEvent)
							.filter(event => event.start.getDay() === dragToSelectEvent.start.getDay())
							.filter(event => event.end > dragToSelectEvent.start)
							.map(event => event.start)
							.filter(start => newEnd > start );
		if (eventAlreadySetAt.length > 0) {
			return;
		}
		dragToSelectEvent.end = newEnd;
	}

}



openConfirmDialogForEvent(event: CalendarEvent): void {
	const dialogRef = this.dialog.open(NewAttentionComponent,
		{
			data: {
				start: event.start,
				end: event.end
			}
		});
	dialogRef.afterClosed().subscribe(dialogInfo => {
		if (!dialogInfo) {
			if (event.meta.tmpEvent) {
				this.removeTempEvent(event);
			}
		} else {
			this.setNewEvent(event, dialogInfo);
		}
		this.refresh();
	});
}

private removeTempEvent(event: CalendarEvent): void {
	const index: number = this.events.indexOf(event);
	if (index !== -1) {
		this.events.splice(index, 1);
	}
}

private setNewEvent(event: CalendarEvent, dialogInfo) {
	delete event.meta.tmpEvent;
	event.meta = dialogInfo;
	event.title = `<strong>Atencion ${dialogInfo.medicalAttentionType.description} </strong> <br>`,
	event.color = dialogInfo.medicalAttentionType.description === MEDICAL_ATTENTION.SPONTANEOUS ?  colors.blue : colors.green;
	event.title += dialogInfo.overTurnCount > 0 ? '<span>Atiende sobreturnos</span>' :  '<span>No atiende sobreturnos</span>';
}

private refresh() {
	this.events = [...this.events];
	this.cdr.detectChanges();
}

public setAppointmentDuration(duration: number) {
	this.appointmentDuration = duration;
}

public getEvents() {
	return this.events;
}

}
