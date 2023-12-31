package net.pladema.clinichistory.hospitalization.controller.documents.indication;

import java.util.List;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;

import net.pladema.clinichistory.hospitalization.controller.mapper.IndicationMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.lamansys.sgh.shared.infrastructure.input.service.DietDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.OtherIndicationDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.ParenteralPlanDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.PharmacoDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.PharmacoSummaryDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.clinichistory.indication.service.diet.DietService;
import net.pladema.clinichistory.indication.service.otherindication.OtherIndicationService;
import net.pladema.clinichistory.indication.service.parenteralplan.ParenteralPlanService;
import net.pladema.clinichistory.indication.service.pharmaco.PharmacoService;

@RestController
@RequestMapping("/institutions/{institutionId}/internments/{internmentEpisodeId}")
@Tag(name = "Indication", description = "Indication")
@Validated
@Slf4j
@RequiredArgsConstructor
public class InternmentIndicationController {

	private final DietService dietService;

	private final OtherIndicationService otherIndicationService;

	private final PharmacoService pharmacoService;

	private final ParenteralPlanService parenteralPlanService;

	private final IndicationMapper indicationMapper;

	@GetMapping("/diets")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO, PERSONAL_DE_FARMACIA, PRESCRIPTOR')")
	public ResponseEntity<List<DietDto>> getInternmentEpisodeDiets(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}", institutionId, internmentEpisodeId);
		List<DietDto> result = dietService.getEpisodeDiets(internmentEpisodeId, SourceType.HOSPITALIZATION);
		log.debug("Get active internment episode diets => {}", result);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/diet")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO')")
	public ResponseEntity<Integer> addDiet(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId, @RequestBody DietDto dietDto) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, dietDto {}", institutionId, internmentEpisodeId, dietDto);
		Integer result = dietService.addDiet(indicationMapper.mapToDietBo(dietDto, institutionId, internmentEpisodeId, SourceType.HOSPITALIZATION));
		log.debug("Output -> {}", result);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/other-indication")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO')")
	public ResponseEntity<Integer> addOtherIndication(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId, @RequestBody OtherIndicationDto otherIndicationDto) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, otherIndicationDto {}", institutionId, internmentEpisodeId, otherIndicationDto);
		Integer result = otherIndicationService.add(indicationMapper.mapToOtherIndicationBo(otherIndicationDto, institutionId, internmentEpisodeId, SourceType.HOSPITALIZATION));
		log.debug("Output -> {}", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/other-indications")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO, PERSONAL_DE_FARMACIA, PRESCRIPTOR')")
	public ResponseEntity<List<OtherIndicationDto>> getInternmentEpisodeOtherIndications(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}", institutionId, internmentEpisodeId);
		List<OtherIndicationDto> result = otherIndicationService.getEpisodeOtherIndications(internmentEpisodeId, SourceType.HOSPITALIZATION);
		log.debug("Get active internment episode other indications => {}", result);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/pharmaco")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ESPECIALISTA_EN_ODONTOLOGIA')")
	public ResponseEntity<Integer> addPharmaco(@PathVariable(name = "institutionId") Integer institutionId,
											   @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
											   @RequestBody PharmacoDto pharmacoDto) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, pharmacoDto {}", institutionId, internmentEpisodeId, pharmacoDto);
		Integer result = pharmacoService.add(indicationMapper.mapToPharmacoBo(pharmacoDto, institutionId, internmentEpisodeId, SourceType.HOSPITALIZATION));
		log.debug("Output -> {}", result);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/parenteral-plan")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ESPECIALISTA_EN_ODONTOLOGIA')")
	public ResponseEntity<Integer> addParenteralPlan(@PathVariable(name = "institutionId") Integer institutionId,
													 @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
													 @RequestBody ParenteralPlanDto parenteralPlan) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, parenteralPlanDto {}", institutionId, internmentEpisodeId, parenteralPlan);
		Integer result = parenteralPlanService.add(indicationMapper.mapToInternmentParenteralPlanBo(parenteralPlan, institutionId, internmentEpisodeId, SourceType.HOSPITALIZATION));
		log.debug("Output -> {}", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/pharmacos")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO, PERSONAL_DE_FARMACIA, PRESCRIPTOR')")
	public ResponseEntity<List<PharmacoSummaryDto>> getInternmentEpisodePharmacos(@PathVariable(name = "institutionId") Integer institutionId,
																				  @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}", institutionId, internmentEpisodeId);
		List<PharmacoSummaryDto> result = pharmacoService.getEpisodePharmacos(internmentEpisodeId, SourceType.HOSPITALIZATION);
		log.debug("Output -> {}", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/parenteral-plans")
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ESPECIALISTA_EN_ODONTOLOGIA, PROFESIONAL_DE_SALUD, ENFERMERO, PERSONAL_DE_FARMACIA, PRESCRIPTOR')")
	public ResponseEntity<List<ParenteralPlanDto>> getInternmentEpisodeParenteralPlans(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}", institutionId, internmentEpisodeId);
		List<ParenteralPlanDto> result = parenteralPlanService.getEpisodeParenteralPlans(internmentEpisodeId, SourceType.HOSPITALIZATION);
		log.debug("Output => {}", result.toString());
		return ResponseEntity.ok(result);
	}

}
