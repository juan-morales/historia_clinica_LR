package net.pladema.medicalconsultation.appointment.service.impl.exceptions;

import lombok.Getter;
@Getter
public enum UpdateAppointmentDateExceptionEnum {
	APPOINTMENT_DATE_ALREADY_ASSIGNED,
	APPOINTMENT_DATE_OUT_OF_DIARY_RANGE,
	APPOINTMENT_TIME_OUT_OF_OPENING_HOURS,
	APPOINTMENT_DAY_OF_WEEK_INVALID,
	APPOINTMENT_STATE_INVALID,
	APPOINTMENT_DATE_BEFORE_NOW
}
