package net.pladema.clinichistory.requests.servicerequests.infrastructure.output;

import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.featureflags.application.FeatureFlagsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.clinichistory.requests.servicerequests.application.port.WorklistStorage;
import net.pladema.clinichistory.requests.servicerequests.domain.WorklistBo;
import net.pladema.clinichistory.requests.servicerequests.infrastructure.input.service.EDiagnosticImageReportStatus;
import net.pladema.medicalconsultation.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorklistStorageImpl implements WorklistStorage {

	private final FeatureFlagsService featureFlagsService;
	private final AppointmentRepository appointmentRepository;

	@Override
	public List<WorklistBo> getWorklistByModalityAndInstitution(Integer modalityId, Integer institutionId) {
		log.debug("Get worklist by modalityId {}, institutionId {}", modalityId, institutionId);
		List<WorklistBo> result;
		if (modalityId != null) {
			result = appointmentRepository.getPendingWorklistByModalityAndInstitution(modalityId, institutionId).stream().map(w -> {
				w.setPatientFullName(w.getFullName(featureFlagsService.isOn(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS)));
				w.setStatusId(EDiagnosticImageReportStatus.PENDING.getId());
				return w;
			}).collect(Collectors.toList());

			result.addAll(appointmentRepository.getCompletedWorklistByModalityAndInstitution(modalityId, institutionId).stream().map(w -> {
				w.setPatientFullName(w.getFullName(featureFlagsService.isOn(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS)));
				w.setStatusId(EDiagnosticImageReportStatus.COMPLETED.getId());
				return w;
			}).collect(Collectors.toList()));
		}
		else {
			result = appointmentRepository.getPendingWorklistByInstitution(institutionId).stream().map(w -> {
				w.setPatientFullName(w.getFullName(featureFlagsService.isOn(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS)));
				w.setStatusId(EDiagnosticImageReportStatus.PENDING.getId());
				return w;
			}).collect(Collectors.toList());

			result.addAll(appointmentRepository.getCompletedWorklistByInstitution(institutionId).stream().map(w -> {
				w.setPatientFullName(w.getFullName(featureFlagsService.isOn(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS)));
				w.setStatusId(EDiagnosticImageReportStatus.COMPLETED.getId());
				return w;
			}).collect(Collectors.toList()));
		}

		result.sort(Comparator.comparing(WorklistBo::getActionTime));

		return result;
	}
}
