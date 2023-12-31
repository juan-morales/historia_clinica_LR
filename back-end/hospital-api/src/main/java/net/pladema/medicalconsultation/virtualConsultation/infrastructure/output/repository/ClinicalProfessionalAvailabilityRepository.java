package net.pladema.medicalconsultation.virtualConsultation.infrastructure.output.repository;

import net.pladema.medicalconsultation.virtualConsultation.infrastructure.output.repository.entity.VirtualConsultationClinicalProfessionalAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClinicalProfessionalAvailabilityRepository extends JpaRepository<VirtualConsultationClinicalProfessionalAvailability, Integer> {

	@Transactional(readOnly = true)
	@Query(" SELECT vccpa.available " +
			"FROM VirtualConsultationClinicalProfessionalAvailability vccpa " +
			"WHERE vccpa.healthcareProfessionalId = :healthcareProfessionalId")
	Boolean getProfessionalAvailabilityByHealthcareProfessionalId(@Param("healthcareProfessionalId") Integer healthcareProfessionalId);

}
