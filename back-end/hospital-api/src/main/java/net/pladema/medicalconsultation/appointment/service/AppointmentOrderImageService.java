package net.pladema.medicalconsultation.appointment.service;


import net.pladema.medicalconsultation.appointment.service.domain.AppointmentOrderImageBo;
import net.pladema.medicalconsultation.appointment.service.domain.DetailsOrderImageBo;

import java.util.Optional;


public interface AppointmentOrderImageService {


	boolean isAlreadyCompleted(Integer appointmentId);

	boolean updateCompleted(DetailsOrderImageBo detailsOrderImageBo, boolean finished);

	Optional<String> getImageId(Integer appointmentId);

	void save(AppointmentOrderImageBo appointmentOrderImageBo);

	void setImageId(Integer appointmentId, String imageId);

	void setDestInstitutionId(Integer institutionId, Integer appointmentId);

	void updateReportStatusId(Integer appointmentId, boolean isReportRequired);
}
