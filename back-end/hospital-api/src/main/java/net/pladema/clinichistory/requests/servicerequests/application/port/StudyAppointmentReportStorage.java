package net.pladema.clinichistory.requests.servicerequests.application.port;

import net.pladema.clinichistory.requests.servicerequests.domain.InformerObservationBo;
import net.pladema.clinichistory.requests.servicerequests.domain.StudyAppointmentBo;

public interface StudyAppointmentReportStorage {

	StudyAppointmentBo getStudyByAppointment(Integer appointmentId);
	Long createDraftReport(Integer appointmentId, InformerObservationBo informerObservations);
	Long updateDraftReport(Integer appointmentId, InformerObservationBo informerObservations);
	Long closeDraftReport(Integer appointmentId, InformerObservationBo informerObservations);
	Long saveReport(Integer appointmentId, InformerObservationBo informerObservations);
}
