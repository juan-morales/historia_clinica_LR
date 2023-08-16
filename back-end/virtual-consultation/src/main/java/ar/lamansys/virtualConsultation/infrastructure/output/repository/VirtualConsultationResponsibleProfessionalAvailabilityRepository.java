package ar.lamansys.virtualConsultation.infrastructure.output.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ar.lamansys.virtualConsultation.infrastructure.output.repository.entity.VirtualConsultationResponsibleProfessionalAvailability;
import ar.lamansys.virtualConsultation.infrastructure.output.repository.entity.VirtualConsultationResponsibleProfessionalAvailabilityPK;

@Repository
public interface VirtualConsultationResponsibleProfessionalAvailabilityRepository extends JpaRepository<VirtualConsultationResponsibleProfessionalAvailability, VirtualConsultationResponsibleProfessionalAvailabilityPK> {

	@Transactional(readOnly = true)
	@Query(" SELECT vcrpa.available " +
			"FROM VirtualConsultationResponsibleProfessionalAvailability vcrpa " +
			"WHERE vcrpa.id.healthcareProfessionalId = :healthcareProfessionalId " +
			"AND vcrpa.id.institutionId = :institutionId")
	Boolean getAvailabilityByHealthcareProfessionalId(@Param("healthcareProfessionalId") Integer healthcareProfessionalId, @Param("institutionId") Integer institutionId);

}
