
export const APPOINTMENT_DURATIONS = [
	{
		id: 10,
		description: '10 minutos'
	},
	{
		id: 15,
		description: '15 minutos'
	},
	{
		id: 20,
		description: '20 minutos'
	},
	{
		id: 30,
		description: '30 minutos'
	},
	{
		id: 60,
		description: '60 minutos'
	}
];

export const CANCEL_STATE_ID = 4;

export enum APPOINTMENT_STATES_ID {
	ASSIGNED = 1,
	CONFIRMED = 2,
	ABSENT = 3,
	CANCELLED = 4,
	SERVED = 5,
	BOOKED = 6,
	BLOCKED = 7,
	OUT_OF_DIARY = 8
}

export const APPOINTMENT_STATES: AppointmentState[] = [
	{
		id: APPOINTMENT_STATES_ID.ASSIGNED,
		description: 'Asignado'
	},
	{
		id: APPOINTMENT_STATES_ID.CONFIRMED,
		description: 'Confirmado'
	},
	{
		id: APPOINTMENT_STATES_ID.ABSENT,
		description: 'Ausente'
	},
	{
		id: APPOINTMENT_STATES_ID.CANCELLED,
		description: 'Cancelado'
	},
	{
		id: APPOINTMENT_STATES_ID.SERVED,
		description: 'Atendido'
	},
	{
		id: APPOINTMENT_STATES_ID.BOOKED,
		description: 'Turno online'
	},
	{
		id: APPOINTMENT_STATES_ID.OUT_OF_DIARY,
		description: 'Fuera de agenda'
	},

];

export interface AppointmentState {
	id: APPOINTMENT_STATES_ID;
	description: string;
}

export function getAppointmentState(id: APPOINTMENT_STATES_ID): AppointmentState {
	return APPOINTMENT_STATES.find(appointment => appointment.id === id);
}

export const MAX_LENGTH_MOTIVE = 255;

export const MINUTES_IN_HOUR = 60;

export const TEMPORARY_PATIENT = 3;
export const GREY_TEXT = 'calendar-event-grey-text';
export const WHITE_TEXT = 'calendar-event-white-text';
export const BLUE_TEXT = 'calendar-event-blue-text';
export const PURPLE_TEXT = 'calendar-event-purple-text';

export const enum COLORES {
	ASSIGNED = '#4187FF',
	CONFIRMED = '#FFA500',
	ABSENT = '#D5E0D5',
	BLOCKED = '#7D807D',
	SERVED = '#A3EBAF',
	PROGRAMADA = '#7FC681',
	ESPONTANEA = '#2687C5',
	SOBRETURNO = '#1A45DD',
	RESERVA_ALTA = '#FFFFFF',
	RESERVA_VALIDACION = '#EB5757',
	FUERA_DE_AGENDA = '#FF0000',
	PROTECTED = '#AF26C5'
}
