package net.pladema.clinichistory.generalstate.service.impl;

import net.pladema.clinichistory.generalstate.repository.HCEImmunizationRepository;
import net.pladema.clinichistory.generalstate.repository.domain.HCEImmunizationVo;
import net.pladema.clinichistory.generalstate.service.HCEImmunizationService;
import net.pladema.clinichistory.generalstate.service.domain.HCEImmunizationBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HCEImmunizationServiceImpl implements HCEImmunizationService {

    private static final Logger LOG = LoggerFactory.getLogger(HCEImmunizationServiceImpl.class);

    private static final String LOGGING_OUTPUT = "Output -> {}";
    private static final String LOGGING_INPUT = "Input parameters -> patientId {} ";

    private final HCEImmunizationRepository hceImmunizationRepository;

    public HCEImmunizationServiceImpl(HCEImmunizationRepository hceImmunizationRepository) {
        this.hceImmunizationRepository = hceImmunizationRepository;
    }

    @Override
    public List<HCEImmunizationBo> getImmunization(Integer patientId) {
        LOG.debug(LOGGING_INPUT, patientId);
        List<HCEImmunizationVo> resultQuery = hceImmunizationRepository.getImmunization(patientId);
        List<HCEImmunizationBo> result = resultQuery.stream().map(HCEImmunizationBo::new).sorted(Comparator.comparing(HCEImmunizationBo::getAdministrationDate).reversed()).collect(Collectors.toList());
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }
}
