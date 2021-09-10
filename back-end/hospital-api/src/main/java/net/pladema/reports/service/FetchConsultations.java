package net.pladema.reports.service;

import net.pladema.reports.repository.ConsultationStorage;
import net.pladema.reports.service.domain.ConsultationsBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FetchConsultations {

    private final Logger logger;

    private final ConsultationStorage consultationStorage;

    public FetchConsultations(ConsultationStorage consultationStorage) {
        this.consultationStorage = consultationStorage;
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    public List<ConsultationsBo> run(Integer patientId) {
        logger.debug("Input parameter -> patientId {}", patientId);
        List<ConsultationsBo> result = consultationStorage.fetchAllByPatientId(patientId).stream().map(ConsultationsBo::new)
                .collect(Collectors.toList());
        logger.debug("Output -> {}", result);
        return result;
    }
}
