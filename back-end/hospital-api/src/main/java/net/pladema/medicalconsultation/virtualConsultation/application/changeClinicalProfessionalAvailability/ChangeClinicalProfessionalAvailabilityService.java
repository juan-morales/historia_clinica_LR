package net.pladema.medicalconsultation.virtualConsultation.application.changeClinicalProfessionalAvailability;

import net.pladema.medicalconsultation.virtualConsultation.domain.ClinicalProfessionalAvailabilityBo;

public interface ChangeClinicalProfessionalAvailabilityService {

	Boolean run(ClinicalProfessionalAvailabilityBo professionalAvailability);

}
