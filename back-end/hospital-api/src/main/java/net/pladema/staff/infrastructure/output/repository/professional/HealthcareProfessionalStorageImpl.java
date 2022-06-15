package net.pladema.staff.infrastructure.output.repository.professional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.pladema.staff.application.ports.HealthcareProfessionalStorage;
import net.pladema.staff.domain.LicenseNumberBo;
import net.pladema.staff.domain.ProfessionBo;
import net.pladema.staff.domain.ProfessionalCompleteBo;
import net.pladema.staff.service.domain.ELicenseNumberTypeBo;

@Service
@Slf4j
public class HealthcareProfessionalStorageImpl implements HealthcareProfessionalStorage {

	private final EntityManager entityManager;

	public HealthcareProfessionalStorageImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public ProfessionalCompleteBo fetchProfessionalByUserId(Integer userId) {
		log.debug("fetchProfessionalByUserId -> userId={}", userId);
		String sqlString = "" +
				"SELECT p.id, p.firstName, p.lastName, pe.nameSelfDetermination " +
				"FROM UserPerson AS up " +
				"JOIN Person p ON (up.pk.personId = p.id) " +
				"JOIN PersonExtended pe ON (p.id = pe.id) " +
				"WHERE up.pk.userId = :userId " +
				"";
		Query query = entityManager.createQuery(sqlString);
		query.setParameter("userId", userId);
		Object[] authorInfo = (Object[]) query.getSingleResult();
		ProfessionalCompleteBo result = new ProfessionalCompleteBo((Integer) authorInfo[0], (String) authorInfo[1],
				(String) authorInfo[2], (String) authorInfo[3]);

		result.setProfessions(buildProfessions(result.getPersonId()));
		log.trace("execute result query -> {}", result);
		return result;
	}

	@Override
	public ProfessionalCompleteBo fetchProfessionalById(Integer professionalId) {
		log.debug("fetchProfessionalById -> professionalId={}", professionalId);
		String sqlString = "" +
				"SELECT p.id, p.firstName, p.lastName, pe.nameSelfDetermination " +
				"FROM HealthcareProfessional AS hp " +
				"JOIN Person p ON (hp.personId = p.id) " +
				"JOIN PersonExtended pe ON (p.id = pe.id) " +
				"WHERE hp.id = :professionalId " +
				"";
		Query query = entityManager.createQuery(sqlString);
		query.setParameter("professionalId", professionalId);
		Object[] authorInfo = (Object[]) query.getSingleResult();
		ProfessionalCompleteBo result = new ProfessionalCompleteBo((Integer) authorInfo[0], (String) authorInfo[1],
				(String) authorInfo[2], (String) authorInfo[3]);

		result.setProfessions(buildProfessions(result.getPersonId()));
		log.trace("execute result query -> {}", result);
		return result;
	}

	private List<ProfessionBo> buildProfessions(Integer personId) {
		String sqlString = "" +
				"SELECT hp.id, " +
				"		ps.id as professionalId, ps.description " +
				"FROM HealthcareProfessional hp " +
				"INNER JOIN ProfessionalProfessions pp ON (pp.healthcareProfessionalId = hp.id) " +
				"INNER JOIN ProfessionalSpecialty ps ON (pp.professionalSpecialtyId = ps.id) " +
				"WHERE hp.personId = :personId " +
				"AND hp.deleteable.deleted = false " +
				"";

		List<Object[]> rows = new ArrayList<>();
		Integer searchTries = 0;
		Query query = entityManager.createQuery(sqlString);
		query.setParameter("personId", personId);

		while (rows.isEmpty() && (searchTries < 10)){
			rows = query.getResultList();
			searchTries++;
		}
		return rows.stream()
				.map(this::buildProfession)
				.collect(Collectors.toList());
	}

	private ProfessionBo buildProfession(Object[]  row) {
		var result = new ProfessionBo((Integer) row[0], (Integer) row[1],(String) row[2]);
		result.setLicenses(buildLicenses(result.getId()));
		return result;
	}

	private List<LicenseNumberBo> buildLicenses(Integer professionalProfessionId) {
		String sqlString = "" +
				"SELECT pln.id, " +
				"		pln.licenseNumber, pln.type " +
				"FROM ProfessionalLicenseNumber pln " +
				"WHERE pln.professionalProfessionId = :professionalProfessionId " +
				"";

		Query query = entityManager.createQuery(sqlString);
		query.setParameter("professionalProfessionId", professionalProfessionId);
		List<Object[]> rows = query.getResultList();
		return rows.stream()
				.map(this::buildLicense)
				.collect(Collectors.toList());
	}

	private LicenseNumberBo buildLicense(Object[] row) {
		return new LicenseNumberBo((Integer) row[0], (String) row[1], (ELicenseNumberTypeBo) row[2]);
	}

	private ProfessionalCompleteBo buildProfessionalCompleteBo(Integer userId) {
		String sqlString = "" +
				"SELECT p.id, p.firstName, p.lastName, pe.nameSelfDetermination " +
				"FROM UserPerson AS up " +
				"JOIN Person p ON (up.pk.personId = p.id) " +
				"JOIN PersonExtended pe ON (p.id = pe.id) " +
				"WHERE up.pk.userId = :userId " +
				"";
		Query query = entityManager.createQuery(sqlString);
		query.setParameter("userId", userId);
		Object[] authorInfo = (Object[]) query.getSingleResult();
		return new ProfessionalCompleteBo((Integer) authorInfo[0], (String) authorInfo[1],
				(String) authorInfo[2], (String) authorInfo[3]);
	}
}
