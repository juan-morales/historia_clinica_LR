package net.pladema.clinichistory.documents.core;

import net.pladema.clinichistory.documents.service.Document;
import net.pladema.clinichistory.documents.service.DocumentFactory;
import net.pladema.clinichistory.documents.service.DocumentService;
import net.pladema.clinichistory.documents.service.NoteService;
import net.pladema.clinichistory.documents.service.ips.*;
import net.pladema.clinichistory.documents.service.ips.domain.DocumentObservationsBo;
import net.pladema.clinichistory.hospitalization.service.InternmentEpisodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentFactoryImpl implements DocumentFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentFactoryImpl.class);

    private final DocumentService documentService;

    private final InternmentEpisodeService internmentEpisodeService;

    private final NoteService noteService;

    private final HealthConditionService healthConditionService;

    private final AllergyService allergyService;

    private final MedicationService medicationService;

    private final ClinicalObservationService clinicalObservationService;

    private final ImmunizationService immunizationService;

    private final ProceduresService proceduresService;

    private final DiagnosticReportService diagnosticReportService;

    public DocumentFactoryImpl(DocumentService documentService,
                               InternmentEpisodeService internmentEpisodeService,
                               NoteService noteService,
                               HealthConditionService healthConditionService,
                               AllergyService allergyService,
                               ClinicalObservationService clinicalObservationService,
                               ImmunizationService immunizationService,
                               ProceduresService proceduresService,
                               MedicationService medicationService,
                               DiagnosticReportService diagnosticReportService) {
        this.documentService = documentService;
        this.internmentEpisodeService = internmentEpisodeService;
        this.noteService = noteService;
        this.healthConditionService = healthConditionService;
        this.allergyService = allergyService;
        this.clinicalObservationService = clinicalObservationService;
        this.immunizationService = immunizationService;
        this.proceduresService = proceduresService;
        this.medicationService = medicationService;
        this.diagnosticReportService = diagnosticReportService;
    }

    @Override
    public Long run(Document document) {

        net.pladema.clinichistory.documents.repository.entity.Document doc =
                new net.pladema.clinichistory.documents.repository.entity.Document(
                        document.getEncounterId(), document.getDocumentStatusId(), document.getDocumentType(), document.getDocumentSource());
        loadNotes(doc, Optional.ofNullable(document.getNotes()));
        doc = documentService.save(doc);

        Integer patientId = document.getPatientId();
        Integer intermentEpisodeId = document.getEncounterId();
        healthConditionService.loadMainDiagnosis(patientId, doc.getId(), Optional.ofNullable(document.getMainDiagnosis()));
        healthConditionService.loadDiagnosis(patientId, doc.getId(), document.getDiagnosis());
        healthConditionService.loadPersonalHistories(patientId, doc.getId(), document.getPersonalHistories());
        healthConditionService.loadFamilyHistories(patientId, doc.getId(), document.getFamilyHistories());
        allergyService.loadAllergies(patientId, doc.getId(), document.getAllergies());
        immunizationService.loadImmunization(patientId, doc.getId(), document.getImmunizations());
        medicationService.loadMedications(patientId, doc.getId(), document.getMedications());
        proceduresService.loadProcedures(patientId, doc.getId(), document.getProcedures());

        clinicalObservationService.loadVitalSigns(patientId, doc.getId(), Optional.ofNullable(document.getVitalSigns()));
        clinicalObservationService.loadAnthropometricData(patientId, doc.getId(), Optional.ofNullable(document.getAnthropometricData()));

        internmentEpisodeService.updateAnamnesisDocumentId(intermentEpisodeId, doc.getId());

        diagnosticReportService.loadDiagnosticReport(doc.getId(), patientId, document.getDiagnosticReports());

        return doc.getId();
    }

    private net.pladema.clinichistory.documents.repository.entity.Document loadNotes(net.pladema.clinichistory.documents.repository.entity.Document document, Optional<DocumentObservationsBo> optNotes) {
        LOG.debug("Input parameters -> anamnesisDocument {}, notes {}", document, optNotes);
        optNotes.ifPresent(notes -> {
            document.setCurrentIllnessNoteId(noteService.createNote(notes.getCurrentIllnessNote()));
            document.setPhysicalExamNoteId(noteService.createNote(notes.getPhysicalExamNote()));
            document.setStudiesSummaryNoteId(noteService.createNote(notes.getStudiesSummaryNote()));
            document.setEvolutionNoteId(noteService.createNote(notes.getEvolutionNote()));
            document.setClinicalImpressionNoteId(noteService.createNote(notes.getClinicalImpressionNote()));
            document.setOtherNoteId(noteService.createNote(notes.getOtherNote()));
        });
        return document;
    }

}
