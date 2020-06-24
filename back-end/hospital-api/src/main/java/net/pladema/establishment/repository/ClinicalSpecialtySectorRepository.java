package net.pladema.establishment.repository;

import net.pladema.establishment.repository.entity.ClinicalSpecialtySector;
import net.pladema.staff.repository.entity.ClinicalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ClinicalSpecialtySectorRepository extends JpaRepository<ClinicalSpecialtySector, Integer> {

	@Transactional(readOnly = true)
	@Query(value = " SELECT cs FROM  ClinicalSpecialtySector css "
			+ " INNER JOIN ClinicalSpecialty cs ON cs.id = css.clinicalSpecialtyId "
			+ " INNER JOIN Sector s ON css.sectorId = s.id "
			+ " WHERE css.sectorId = :idSector AND s.institutionId = :institutionId")
	List<ClinicalSpecialty> getAllBySectorAndInstitution(@Param("idSector") Integer idSector,
			@Param("institutionId") Integer institutionId);


	@Transactional(readOnly = true)
	@Query(value = " SELECT s.institutionId " +
			"FROM  ClinicalSpecialtySector css " +
			"INNER JOIN Sector s ON (css.sectorId = s.id) " +
			"WHERE css.id = :id")
    Integer getInstitutionId(@Param("id") Integer id);

}
