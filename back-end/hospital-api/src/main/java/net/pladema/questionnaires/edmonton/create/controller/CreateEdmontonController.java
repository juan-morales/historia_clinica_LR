package net.pladema.questionnaires.edmonton.create.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.pladema.questionnaires.common.domain.model.QuestionnaireAnswerBO;
import net.pladema.questionnaires.common.domain.model.QuestionnaireBO;
import net.pladema.questionnaires.common.dto.CreateQuestionnaireDTO;
import net.pladema.questionnaires.common.dto.QuestionnaireAnswerDTO;
import net.pladema.questionnaires.edmonton.create.domain.EEdmontonTestAnswer;
import net.pladema.questionnaires.edmonton.create.domain.service.CreateEdmontonService;

@RestController
@Validated
@RequestMapping("/institution/{institutionId}/patient/{patientId}/hce/general-state")
public class CreateEdmontonController implements CreateEdmontonAPI {

	private static final Logger logger = LoggerFactory.getLogger(CreateEdmontonController.class);

	private final CreateEdmontonService createEdmontonService;

	public CreateEdmontonController(CreateEdmontonService createEdmontonService) {
		this.createEdmontonService = createEdmontonService;
	}

	@Override
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA')")
	public ResponseEntity<Boolean> createPatientEdmonton(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "patientId") Integer patientId, @RequestBody CreateQuestionnaireDTO createEdmontonDTO) {

		logger.debug("Received request body: {}", createEdmontonDTO);

		QuestionnaireBO edmontonBO = createEdmontonDTO(patientId, createEdmontonDTO);

		createEdmontonService.execute(edmontonBO);

		logger.debug("Edmonton frailty test created successfully.");

		return ResponseEntity.ok().body(true);
	}

	private QuestionnaireBO createEdmontonDTO(Integer patientId, CreateQuestionnaireDTO createEdmontonDTO) {
		QuestionnaireBO reg = new QuestionnaireBO();
		QuestionnaireAnswerBO lstReg;
		reg.setPatientId(patientId);
		int count = 0;
		if (createEdmontonDTO.getQuestionnaire() != null && !createEdmontonDTO.getQuestionnaire().isEmpty()) {
			reg.setAnswers(new ArrayList<>());
			for (QuestionnaireAnswerDTO dto : createEdmontonDTO.getQuestionnaire()) {
				lstReg = new QuestionnaireAnswerBO();
				EEdmontonTestAnswer eReg = EEdmontonTestAnswer.getById(dto.getAnswerId());
				lstReg.setAnswerId(eReg.getAnswerId());
				lstReg.setValue(eReg.getValue());
				if (eReg.getQuestionId() == 13) {
					switch (count) {
						case 0:
							lstReg.setQuestionId(eReg.getQuestionId());
							break;
						case 1:
							lstReg.setQuestionId((short) (eReg.getQuestionId() + 1));
							break;
						case 2:
							lstReg.setQuestionId((short) (eReg.getQuestionId() + 3));
							break;
						case 3:
							lstReg.setQuestionId((short) (eReg.getQuestionId() + 5));
							break;
						case 4:
							lstReg.setQuestionId((short) (eReg.getQuestionId() + 7));
							break;
						default:
							break;
					}
					count++;
				} else {
					lstReg.setQuestionId(eReg.getQuestionId());
				}
				reg.getAnswers().add(lstReg);
			}
		}
		return reg;
	}
}
