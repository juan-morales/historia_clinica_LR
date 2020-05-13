package net.pladema.internation.controller.documents.epicrisis;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.Api;
import net.pladema.internation.controller.constraints.DocumentValid;
import net.pladema.internation.controller.constraints.InternmentValid;
import net.pladema.internation.controller.internment.dto.ResponsibleDoctorDto;
import net.pladema.internation.controller.internment.mapper.ResponsibleDoctorMapper;
import net.pladema.internation.repository.masterdata.entity.DocumentType;
import net.pladema.internation.service.documents.ReportDocumentService;
import net.pladema.internation.service.documents.epicrisis.EpicrisisReportService;
import net.pladema.internation.service.documents.epicrisis.domain.Epicrisis;
import net.pladema.internation.service.internment.InternmentEpisodeService;
import net.pladema.patient.controller.dto.BasicPatientDto;
import net.pladema.patient.controller.service.PatientExternalService;
import net.pladema.pdf.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

@RestController
@RequestMapping("/institutions/{institutionId}/internments/{internmentEpisodeId}/epicrisis-report")
@Api(value = "Epicrisis Report", tags = { "Epicrisis Report" })
@Validated
public class EpicrisisReportController {

    private static final Logger LOG = LoggerFactory.getLogger(EpicrisisReportController.class);

    public static final String OUTPUT = "Output -> {}";
    public static final String INVALID_EPISODE = "internmentepisode.invalid";

    private final InternmentEpisodeService internmentEpisodeService;

    private final EpicrisisReportService epicrisisReportService;

    private final PatientExternalService patientExternalService;

    private final PdfService pdfService;

    private final ReportDocumentService reportDocumentService;

    private final ResponsibleDoctorMapper responsibleDoctorMapper;

    public EpicrisisReportController(InternmentEpisodeService internmentEpisodeService,
                                     EpicrisisReportService epicrisisReportService,
                                     PatientExternalService patientExternalService,
                                     PdfService pdfService,
                                     ReportDocumentService reportDocumentService,
                                     ResponsibleDoctorMapper responsibleDoctorMapper) {
        this.internmentEpisodeService = internmentEpisodeService;
        this.epicrisisReportService = epicrisisReportService;
        this.patientExternalService = patientExternalService;
        this.pdfService = pdfService;
        this.reportDocumentService = reportDocumentService;
        this.responsibleDoctorMapper = responsibleDoctorMapper;
    }


    @GetMapping("/{epicrisisId}")
    @InternmentValid
    @DocumentValid(isConfirmed = true, documentType = DocumentType.EPICRISIS)
    public ResponseEntity<InputStreamResource> getPDF(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @PathVariable(name = "epicrisisId") Long epicrisisId) throws IOException, DocumentException {

        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, epicrisisId {}",
                institutionId, internmentEpisodeId, epicrisisId);
        Epicrisis epicrisis = epicrisisReportService.getDocument(epicrisisId);
        Integer patientId = internmentEpisodeService.getPatient(internmentEpisodeId)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_EPISODE));
        BasicPatientDto patientData = patientExternalService.getBasicDataFromPatient(patientId);
        ResponsibleDoctorDto author = responsibleDoctorMapper.toResponsibleDoctorDto(
                reportDocumentService.getAuthor(epicrisisId));
        Context ctx = createEpicrisisContext(epicrisis, patientData, author);
        String name = "Epicrisis_" + epicrisis.getId() ;
        return pdfService.getResponseEntityPdf(name, "epicrisis", LocalDateTime.now(), ctx);
    }

    private Context createEpicrisisContext(Epicrisis epicrisis, BasicPatientDto patientData, ResponsibleDoctorDto author ) {
        LOG.debug("Input parameters -> epicrisis {}", epicrisis);
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("patient", patientData);
        ctx.setVariable("mainDiagnosis", epicrisis.getMainDiagnosis());
        ctx.setVariable("diagnosis", epicrisis.getDiagnosis());
        ctx.setVariable("personalHistories", epicrisis.getPersonalHistories());
        ctx.setVariable("familyHistories", epicrisis.getFamilyHistories());
        ctx.setVariable("allergies", epicrisis.getAllergies());
        ctx.setVariable("inmunizations", epicrisis.getInmunizations());
        ctx.setVariable("medications", epicrisis.getMedications());
        ctx.setVariable("notes", epicrisis.getNotes());
        ctx.setVariable("author", author);
        LOG.debug(OUTPUT, ctx);
        return ctx;
    }

}