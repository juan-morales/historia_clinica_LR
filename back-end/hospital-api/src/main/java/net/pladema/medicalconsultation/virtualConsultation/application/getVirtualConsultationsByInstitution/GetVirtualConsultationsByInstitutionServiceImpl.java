package net.pladema.medicalconsultation.virtualConsultation.application.getVirtualConsultationsByInstitution;

import java.util.List;

import net.pladema.medicalconsultation.virtualConsultation.domain.VirtualConsultationFilterBo;
import net.pladema.medicalconsultation.virtualConsultation.infrastructure.output.repository.ListVirtualConsultationRepository;

import org.springframework.stereotype.Service;

import net.pladema.medicalconsultation.virtualConsultation.domain.VirtualConsultationBo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class GetVirtualConsultationsByInstitutionServiceImpl implements GetVirtualConsultationsByInstitutionService {

	private final ListVirtualConsultationRepository listVirtualConsultationRepository;

	@Override
	public List<VirtualConsultationBo> run(VirtualConsultationFilterBo filter) {
		log.debug("Input parameters -> filter {}", filter);
		List<VirtualConsultationBo> result = listVirtualConsultationRepository.getInstitutionVirtualConsultation(filter);
		log.debug("Output -> {}", result);
		return result;
	}

}
