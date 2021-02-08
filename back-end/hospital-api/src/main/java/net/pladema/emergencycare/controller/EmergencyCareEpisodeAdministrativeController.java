package net.pladema.emergencycare.controller;

import io.swagger.annotations.Api;
import net.pladema.emergencycare.controller.dto.ResponseEmergencyCareDto;
import net.pladema.emergencycare.controller.mapper.EmergencyCareMapper;
import net.pladema.emergencycare.service.EmergencyCareEpisodeService;
import net.pladema.emergencycare.service.domain.EmergencyCareBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/institution/{institutionId}/emergency-care/episodes/{episodeId}/administrative")
@Api(value = "Emergency care Episodes Administrative", tags = {"Emergency care Episodes Administrative"})
public class EmergencyCareEpisodeAdministrativeController {

	private static final Logger LOG = LoggerFactory.getLogger(EmergencyCareEpisodeAdministrativeController.class);

	private final EmergencyCareEpisodeService emergencyCareEpisodeService;

	private final EmergencyCareMapper emergencyCareMapper;

	public EmergencyCareEpisodeAdministrativeController(EmergencyCareEpisodeService emergencyCareEpisodeService, EmergencyCareMapper emergencyCareMapper){

		this.emergencyCareEpisodeService = emergencyCareEpisodeService;
		this.emergencyCareMapper = emergencyCareMapper;
	}

	@GetMapping
	@PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ENFERMERO, ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD')")
	public ResponseEntity<ResponseEmergencyCareDto> getAdministrative(
			@PathVariable(name = "institutionId") Integer institutionId,
			@PathVariable(name = "episodeId") Integer episodeId) {
		LOG.debug("Input parameters -> institutionId {}, episodeId {}", institutionId, episodeId);
		EmergencyCareBo episode = emergencyCareEpisodeService.get(episodeId, institutionId);
		ResponseEmergencyCareDto result = emergencyCareMapper.toResponseEmergencyCareDto(episode);
		LOG.debug("Output -> {}", result);
		return ResponseEntity.ok().body(result);
	}

	@Transactional
	@PutMapping
	@PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ENFERMERO, ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD')")
	public ResponseEntity<Boolean> setPatient(
			@PathVariable(name = "institutionId") Integer institutionId,
			@PathVariable(name = "episodeId") Integer episodeId,
			@RequestBody Integer patientId) {
		LOG.debug("Update patient of emergency care administrative episode -> episodeId {}, patientId {}",
				episodeId, patientId);
		Boolean result = emergencyCareEpisodeService.validateAndSetPatient(episodeId, patientId, institutionId);
		LOG.debug("Output -> {}", result);
		return ResponseEntity.ok().body(result);
	}
}
