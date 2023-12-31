package net.pladema.clinichistory.indication.controller;

import ar.lamansys.sgh.shared.infrastructure.input.service.DietDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.OtherIndicationDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.ParenteralPlanDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.PharmacoDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.pladema.clinichistory.indication.service.diet.DietService;
import net.pladema.clinichistory.indication.service.otherindication.OtherIndicationService;
import net.pladema.clinichistory.indication.service.parenteralplan.ParenteralPlanService;
import net.pladema.clinichistory.indication.service.pharmaco.PharmacoService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/institutions/{institutionId}/indication")
@Tag(name = "Indications", description = "Indications")
@Validated
@Slf4j
@RequiredArgsConstructor
public class IndicationController {

	private final DietService dietService;

	private final OtherIndicationService otherIndicationService;

	private final PharmacoService pharmacoService;

	private final ParenteralPlanService parenteralPlanService;

	@GetMapping("/diets/{dietId}")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO, PERSONAL_DE_FARMACIA')")
	public ResponseEntity<DietDto> getInternmentEpisodeDiet(@PathVariable(name = "institutionId") Integer institutionId,
															@PathVariable(name = "dietId") Integer dietId) {
		log.debug("Input parameters -> institutionId {}, dietId {}", institutionId, dietId);
		DietDto result = dietService.getDiet(dietId);
		log.debug("Get active internment episode diet by id => {}", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/other-indications/{otherIndicationId}")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO, PERSONAL_DE_FARMACIA')")
	public ResponseEntity<OtherIndicationDto> getInternmentEpisodeOtherIndication(@PathVariable(name = "institutionId") Integer institutionId,
																				  @PathVariable(name = "otherIndicationId") Integer otherIndicationId) {
		log.debug("Input parameters -> institutionId {}, otherIndicationId {}", institutionId, otherIndicationId);
		OtherIndicationDto result = otherIndicationService.getOtherIndication(otherIndicationId);
		log.debug("Get active internment episode other indication by id => {}", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/pharmacos/{pharmacoId}")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO, PERSONAL_DE_FARMACIA')")
	public ResponseEntity<PharmacoDto> getInternmentEpisodePharmaco(@PathVariable(name = "institutionId") Integer institutionId,
																	@PathVariable(name = "pharmacoId") Integer pharmacoId) {
		log.debug("Input parameters -> institutionId {}, pharmacoId {}", institutionId, pharmacoId);
		PharmacoDto result = pharmacoService.getPharmaco(pharmacoId);
		log.debug("Output -> {}", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/parenteral-plans/{parenteralPlanId}")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ESPECIALISTA_EN_ODONTOLOGIA, PROFESIONAL_DE_SALUD, ENFERMERO, PERSONAL_DE_FARMACIA')")
	public ResponseEntity<ParenteralPlanDto> getInternmentEpisodeParenteralPlan(@PathVariable(name = "institutionId") Integer institutionId,
																				@PathVariable(name = "parenteralPlanId") Integer parenteralPlanId) {
		log.debug("Input parameters -> institutionId {}, parenteralPlanId {}", institutionId, parenteralPlanId);
		ParenteralPlanDto result = parenteralPlanService.getParenteralPlan(parenteralPlanId);
		log.debug("Output => {}", result.toString());
		return ResponseEntity.ok(result);
	}

}
