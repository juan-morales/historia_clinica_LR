package ar.lamansys.sgh.clinichistory.domain.ips.services;

import ar.lamansys.sgh.clinichistory.application.document.DocumentService;
import ar.lamansys.sgh.clinichistory.application.notes.NoteService;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.ImmunizationRepository;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.entity.Inmunization;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.ImmunizationStatusRepository;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.masterdata.entity.InmunizationStatus;
import ar.lamansys.sgh.clinichistory.domain.ips.ImmunizationBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LoadImmunizations {

    private static final Logger LOG = LoggerFactory.getLogger(LoadImmunizations.class);

    public static final String OUTPUT = "Output -> {}";

    private final ImmunizationRepository immunizationRepository;

    private final ImmunizationStatusRepository immunizationStatusRepository;

    private final SnomedService snomedService;

    private final DocumentService documentService;

    private final NoteService noteService;

    public LoadImmunizations(ImmunizationRepository immunizationRepository,
                             ImmunizationStatusRepository immunizationStatusRepository,
                             SnomedService snomedService,
                             DocumentService documentService,
                             NoteService noteService){
        this.immunizationRepository = immunizationRepository;
        this.immunizationStatusRepository = immunizationStatusRepository;
        this.snomedService = snomedService;
        this.documentService = documentService;
        this.noteService = noteService;
    }

    public List<ImmunizationBo> run(Integer patientId, Long documentId, List<ImmunizationBo> immunizations) {
        LOG.debug("Input parameters -> patientId {}, documentId {}, immunizations {}", patientId, documentId, immunizations);
        immunizations.forEach(i -> {
			if(i.getId()==null) {
				Integer snomedId = snomedService.getSnomedId(i.getSnomed()).orElseGet(() -> snomedService.createSnomedTerm(i.getSnomed()));
				AtomicReference<Long> noteId = new AtomicReference<>(null);
				Optional.ofNullable(i.getNote()).ifPresent(n -> noteId.set(loadNote(n)));

				Inmunization immunization = saveImmunization(patientId, i, snomedId, noteId.get());
				i.setId(immunization.getId());
				i.setStatusId(immunization.getStatusId());
				i.setStatus(getStatus(i.getStatusId()));
				i.setAdministrationDate(immunization.getAdministrationDate());
			}

            documentService.createImmunization(documentId, i.getId());
        });
        List<ImmunizationBo> result = immunizations;
        LOG.debug(OUTPUT, result);
        return result;
    }

    private Inmunization saveImmunization(Integer patientId, ImmunizationBo immunizationBo,
                                          Integer snomedId, Long noteId) {
        LOG.debug("Input parameters -> patientId {}, immunizationBo {}, snomedId {}, noteId {}",
                patientId, immunizationBo, snomedId, noteId);
        Inmunization immunization = new Inmunization(patientId, snomedId,
                immunizationBo.getStatusId(),
                immunizationBo.getAdministrationDate(),
                immunizationBo.getInstitutionId(),
                immunizationBo.getInstitutionInfo(),
                immunizationBo.getDoctorInfo(),
                immunizationBo.getConditionId(),
                immunizationBo.getSchemeId(),
                immunizationBo.getDose() != null ? immunizationBo.getDose().getDescription() : null,
                immunizationBo.getDose() != null ? immunizationBo.getDose().getOrder() : null,
                noteId,
                immunizationBo.getLotNumber(),
                immunizationBo.isBillable());
        immunization = immunizationRepository.save(immunization);
        LOG.debug("Immunization saved -> {}", immunization.getId());
        LOG.debug(OUTPUT, immunization);
        return immunization;
    }

    private String getStatus(String id) {
        return immunizationStatusRepository.findById(id).map(InmunizationStatus::getDescription).orElse(null);
    }


    private Long loadNote(String note) {
        LOG.debug("Input parameters -> note {}", note);
        Long result = noteService.createNote(note);
        LOG.debug(OUTPUT, result);
        return result;
    }

}
