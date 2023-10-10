package net.pladema.questionnaires.frail.get.repository;

import java.util.Optional;

import javax.persistence.EntityManager;

import net.pladema.questionnaires.common.dto.QuestionnaireSummary;
import net.pladema.questionnaires.common.repository.QuestionnaireSummaryRepository;

import org.springframework.stereotype.Repository;

@Repository
public class FrailSummaryRepositoryImpl implements QuestionnaireSummaryRepository {

	private final EntityManager entityManager;

	public FrailSummaryRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Optional<QuestionnaireSummary> getQuestionnaireSummaryReport(Integer questionnaireId) {

		String query = "SELECT p.first_name, p.middle_names, p.last_name, hp.license_number, to_char(qr.created_on, 'YYYY-MM-DD') " + "FROM  {h-schema}minsal_lr_questionnaire_response qr " + "INNER JOIN {h-schema}healthcare_professional hp ON hp.id = qr.created_by " + "INNER JOIN {h-schema}person p ON p.id = hp.person_id " + "WHERE qr.id = :questionnaireId";

		Optional<Object[]> queryResult = entityManager.createNativeQuery(query).setParameter("questionnaireId", questionnaireId).getResultList().stream().findFirst();

		return queryResult.map(a -> new QuestionnaireSummary((String) a[0], (String) a[1], (String) a[2], (String) a[3], (String) a[4]));
	}
}
