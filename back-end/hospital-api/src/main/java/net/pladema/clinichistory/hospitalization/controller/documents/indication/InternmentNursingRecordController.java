package net.pladema.clinichistory.hospitalization.controller.documents.indication;

import ar.lamansys.sgh.shared.infrastructure.input.service.NursingRecordDto;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.pladema.clinichistory.hospitalization.service.indication.nursingrecord.InternmentNursingRecordService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/institutions/{institutionId}/internments/{internmentEpisodeId}/nursingRecords")
@Tag(name = "Nursing Record", description = "Nursing Record")
@Validated
@Slf4j
@RequiredArgsConstructor
public class InternmentNursingRecordController {

	private final LocalDateMapper localDateMapper;

	private final InternmentNursingRecordService internmentNursingRecordService;


	@GetMapping()
	@PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ESPECIALISTA_EN_ODONTOLOGIA, PROFESIONAL_DE_SALUD, ENFERMERO, PERSONAL_DE_FARMACIA')")
	public ResponseEntity<List<NursingRecordDto>> getInternmentEpisodeNursingRecords(@PathVariable(name = "institutionId") Integer institutionId, @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId) {
		log.debug("Input parameters -> institutionId {}, internmentEpisodeId {}", institutionId, internmentEpisodeId);
		List<NursingRecordDto> result = internmentNursingRecordService.getInternmentEpisodeNursingRecords(internmentEpisodeId);
		log.debug("Output => {}", result.toString());
		return ResponseEntity.ok(result);
	}


}
