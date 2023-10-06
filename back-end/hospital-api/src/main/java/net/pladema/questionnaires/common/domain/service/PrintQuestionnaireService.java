package net.pladema.questionnaires.common.domain.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import net.pladema.questionnaires.common.dto.PrintQuestionnaireDTO;

public interface PrintQuestionnaireService {

	List<PrintQuestionnaireDTO> getPrintQuestionnaire(Long questionnaireId);

	Map<String, Object> createQuestionnaireContext(List <PrintQuestionnaireDTO> lst);

	String createQuestionnaireFileName(Long questionnaireId, ZonedDateTime questionnaireDate);

}
