package net.pladema.medicalconsultation.appointment.service;

import net.pladema.medicalconsultation.appointment.service.domain.AppointmentBo;
import net.pladema.medicalconsultation.appointment.service.domain.UpdateAppointmentBo;

import java.util.Optional;

public interface EquipmentAppointmentService {

	Optional<AppointmentBo> getEquipmentAppointment(Integer appointmentId);

	boolean updateEquipmentState(Integer appointmentId, short appointmentStateId, Integer userId, String reason);

	AppointmentBo updateEquipmentAppointment(UpdateAppointmentBo appointmentDto);

    }
