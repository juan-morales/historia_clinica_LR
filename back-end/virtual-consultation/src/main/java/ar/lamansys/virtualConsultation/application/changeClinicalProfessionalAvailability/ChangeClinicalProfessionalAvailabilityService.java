package ar.lamansys.virtualConsultation.application.changeClinicalProfessionalAvailability;

import ar.lamansys.virtualConsultation.domain.ClinicalProfessionalAvailabilityBo;

public interface ChangeClinicalProfessionalAvailabilityService {

	Boolean run(ClinicalProfessionalAvailabilityBo professionalAvailability);

}
