package ar.lamansys.virtualConsultation.infrastructure.output.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ar.lamansys.sgx.shared.auditable.repository.SGXAuditableEntityJPARepository;
import ar.lamansys.virtualConsultation.domain.VirtualConsultationBo;
import ar.lamansys.virtualConsultation.infrastructure.output.repository.entity.VirtualConsultation;

@Repository
public interface VirtualConsultationRepository extends SGXAuditableEntityJPARepository<VirtualConsultation, Integer> {

	@Transactional(readOnly = true)
	@Query(" SELECT NEW ar.lamansys.virtualConsultation.domain.VirtualConsultationBo(vc.id, p.id, p2.firstName, pe.nameSelfDetermination, p2.lastName, " +
			"EXTRACT(YEAR FROM (AGE(p2.birthDate))), spg.description, s2.pt, s.pt, cs.name, cl.description, vc.institutionId, i.name, vc.statusId, p3.firstName, p3.lastName, hp.id, " +
			"vc.priorityId, vc.creationable.createdOn, vc.callId) " +
			"FROM VirtualConsultation vc " +
			"JOIN Patient p ON (p.id = vc.patientId) " +
			"JOIN Person p2 ON (p2.id = p.personId) " +
			"JOIN PersonExtended pe ON (pe.id = p2.id) " +
			"LEFT JOIN SelfPerceivedGender spg ON (spg.id = p2.genderId) " +
			"JOIN Snomed s ON (s.id = vc.motiveId) " +
			"LEFT JOIN Snomed s2 ON (s2.id = vc.problemId) " +
			"JOIN ClinicalSpecialty cs ON (cs.id = vc.clinicalSpecialtyId) " +
			"JOIN CareLine cl ON (cl.id = vc.careLineId) " +
			"JOIN Institution i ON (i.id = vc.institutionId) " +
			"JOIN HealthcareProfessional hp ON (hp.id = vc.responsibleHealthcareProfessionalId) " +
			"JOIN Person p3 ON (p3.id = hp.personId)")
	List<VirtualConsultationBo> getDomainVirtualConsultations();

}
