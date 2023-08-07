package ar.lamansys.virtualConsultation.infrastructure.input.rest;

import net.pladema.staff.controller.service.HealthcareProfessionalExternalService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.lamansys.mqtt.infraestructure.input.service.MqttCallExternalService;
import ar.lamansys.sgx.shared.security.UserInfo;
import ar.lamansys.virtualConsultation.application.saveVirtualConsultation.SaveVirtualConsultationRequestService;
import ar.lamansys.virtualConsultation.domain.VirtualConsultationRequestBo;
import ar.lamansys.virtualConsultation.domain.enums.EVirtualConsultationStatus;
import ar.lamansys.virtualConsultation.infrastructure.input.rest.dto.VirtualConsultationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/virtual-consultation")
@RequiredArgsConstructor
public class VirtualConsultationController {

	private final MqttCallExternalService mqttCallExternalService;

	private final SaveVirtualConsultationRequestService saveVirtualConsultationService;

	private final HealthcareProfessionalExternalService healthcareProfessionalExternalService;

	@PostMapping(value = "/{institutionId}")
	public Integer saveVirtualConsultationRequest(@PathVariable(name = "institutionId") Integer institutionId,
												  @RequestBody VirtualConsultationRequestDto virtualConsultation) {
		log.debug("Input parameters -> institutionId {}, virtualConsultation {}", institutionId, virtualConsultation);
		Integer doctorId = healthcareProfessionalExternalService.getProfessionalId(UserInfo.getCurrentAuditor());
		VirtualConsultationRequestBo virtualConsultationBo = new VirtualConsultationRequestBo(virtualConsultation);
		virtualConsultationBo.setResponsibleHealthcareProfessionalId(doctorId);
		virtualConsultationBo.setStatusId(EVirtualConsultationStatus.PENDING.getId());
		virtualConsultationBo.setInstitutionId(institutionId);
		Integer result = saveVirtualConsultationService.run(virtualConsultationBo);
		log.debug("Output -> {}", result);
		return result;
	}

}
