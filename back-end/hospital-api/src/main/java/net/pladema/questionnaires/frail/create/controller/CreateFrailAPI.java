package net.pladema.questionnaires.frail.create.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.pladema.questionnaires.common.dto.CreateQuestionnaireDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Tag(name = "Patient consultation - Frail", description = "Patient consultation - Frail")
public interface CreateFrailAPI {

	@PostMapping("/frail")
	ResponseEntity<Boolean> createPatientFrail(
			@PathVariable(name = "institutionId") Integer institutionId,
			@PathVariable(name = "patientId") Integer patientId,
			@RequestBody CreateQuestionnaireDTO createFrailDTO) throws IOException;

}