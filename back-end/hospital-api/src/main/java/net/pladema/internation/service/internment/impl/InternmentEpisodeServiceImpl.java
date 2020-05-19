package net.pladema.internation.service.internment.impl;

import net.pladema.internation.repository.core.EvolutionNoteDocumentRepository;
import net.pladema.internation.repository.core.InternmentEpisodeRepository;
import net.pladema.internation.repository.core.domain.EvaluationNoteSummaryVo;
import net.pladema.internation.repository.core.domain.InternmentSummaryVo;
import net.pladema.internation.repository.core.entity.EvolutionNoteDocument;
import net.pladema.internation.repository.core.entity.InternmentEpisode;
import net.pladema.internation.service.internment.InternmentEpisodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InternmentEpisodeServiceImpl implements InternmentEpisodeService {

    public static final String INPUT_PARAMETERS = "Input parameters -> {}";
    private static final Logger LOG = LoggerFactory.getLogger(InternmentEpisodeServiceImpl.class);
    
    private static final String LOGGING_OUTPUT = "Output -> {}";

    private final InternmentEpisodeRepository internmentEpisodeRepository;

    private final EvolutionNoteDocumentRepository evolutionNoteDocumentRepository;

    private static final short ACTIVO =1;
    
    public InternmentEpisodeServiceImpl(InternmentEpisodeRepository internmentEpisodeRepository,
                                        EvolutionNoteDocumentRepository evolutionNoteDocumentRepository) {
        this.internmentEpisodeRepository = internmentEpisodeRepository;
        this.evolutionNoteDocumentRepository = evolutionNoteDocumentRepository;
    }


    @Override
    public void updateAnamnesisDocumentId(Integer internmentEpisodeId, Long anamnesisDocumentId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}, anamnesisDocumentId {}", internmentEpisodeId, anamnesisDocumentId);
        internmentEpisodeRepository.updateAnamnesisDocumentId(internmentEpisodeId, anamnesisDocumentId, LocalDateTime.now());
    }

    @Override
    public void updateEpicrisisDocumentId(Integer internmentEpisodeId, Long epicrisisId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}, epicrisisId {}", internmentEpisodeId, epicrisisId);
        internmentEpisodeRepository.updateEpicrisisDocumentId(internmentEpisodeId, epicrisisId, LocalDateTime.now());
    }

    @Override
    public EvolutionNoteDocument addEvolutionNote(Integer internmentEpisodeId, Long evolutionNoteId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}, evolutionNoteId {}", internmentEpisodeId, evolutionNoteId);
        EvolutionNoteDocument result = new EvolutionNoteDocument(evolutionNoteId, internmentEpisodeId);
        result = evolutionNoteDocumentRepository.save(result);
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public InternmentEpisode addInternmentEpisode(InternmentEpisode internmentEpisode, Integer institutionId) {
        LOG.debug("Input parameters -> internmentEpisode {}, institutionId {}", internmentEpisode, institutionId);
        internmentEpisode.setInstitutionId(institutionId);
        internmentEpisode.setStatusId(ACTIVO);
        internmentEpisode.setEntryDate(LocalDate.now());
        InternmentEpisode result = internmentEpisodeRepository.save(internmentEpisode);
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public boolean haveAnamnesis(Integer internmentEpisodeId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
        boolean result = internmentEpisodeRepository.haveAnamnesis(internmentEpisodeId);
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public LocalDate getEntryDate(Integer internmentEpisodeId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
        LocalDate result = internmentEpisodeRepository.getEntryDate(internmentEpisodeId);
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public boolean haveAnamnesisAndEvolutionNote(Integer internmentEpisodeId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
        boolean result = internmentEpisodeRepository.haveAnamnesisAndEvolutionNote(internmentEpisodeId);
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public Optional<InternmentSummaryVo> getIntermentSummary(Integer internmentEpisodeId) {
        LOG.debug(INPUT_PARAMETERS, internmentEpisodeId);
        Optional<InternmentSummaryVo> result = internmentEpisodeRepository.getSummary(internmentEpisodeId);
        result.ifPresent(r ->
            r.getDocuments().setLastEvaluationNote(getLastEvaluationNoteSummary(internmentEpisodeId).orElse(null))
        );
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    private Optional<EvaluationNoteSummaryVo> getLastEvaluationNoteSummary(Integer internmentEpisodeId){
        LOG.debug(INPUT_PARAMETERS, internmentEpisodeId);
        Page<EvaluationNoteSummaryVo> resultQuery = evolutionNoteDocumentRepository.getLastEvaluationNoteSummary(internmentEpisodeId, PageRequest.of(0, 1));
        Optional<EvaluationNoteSummaryVo> result = resultQuery.getContent().stream().findFirst();
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public Optional<Integer> getPatient(Integer internmentEpisodeId) {
        LOG.debug(INPUT_PARAMETERS, internmentEpisodeId);
        Optional<Integer> result = internmentEpisodeRepository.getPatient(internmentEpisodeId);
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }


}
