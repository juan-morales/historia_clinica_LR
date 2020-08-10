package net.pladema.clinichistory.hospitalization.controller.documents.anamnesis;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.Api;
import net.pladema.clinichistory.documents.events.OnGenerateInternmentDocumentEvent;
import net.pladema.clinichistory.hospitalization.controller.constraints.AnamnesisMainDiagnosisValid;
import net.pladema.clinichistory.hospitalization.controller.constraints.DocumentValid;
import net.pladema.clinichistory.hospitalization.controller.constraints.InternmentValid;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.constraints.AnamnesisValid;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.dto.AnamnesisDto;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.dto.ResponseAnamnesisDto;
import net.pladema.clinichistory.hospitalization.controller.documents.anamnesis.mapper.AnamnesisMapper;
import net.pladema.clinichistory.hospitalization.controller.generalstate.constraint.EffectiveVitalSignTimeValid;
import net.pladema.clinichistory.hospitalization.service.InternmentEpisodeService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.AnamnesisService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.CreateAnamnesisService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.UpdateAnamnesisService;
import net.pladema.clinichistory.hospitalization.service.anamnesis.domain.AnamnesisBo;
import net.pladema.clinichistory.ips.repository.masterdata.entity.DocumentType;
import net.pladema.clinichistory.ips.repository.masterdata.entity.EDocumentType;
import net.pladema.pdf.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/institutions/{institutionId}/internments/{internmentEpisodeId}/anamnesis")
@Api(value = "Anamnesis", tags = { "Anamnesis" })
@Validated
public class AnamnesisController {

    private static final Logger LOG = LoggerFactory.getLogger(AnamnesisController.class);
    public static final String OUTPUT = "Output -> {}";
    public static final String INVALID_EPISODE = "internmentepisode.invalid";

    private final InternmentEpisodeService internmentEpisodeService;

    private final CreateAnamnesisService createAnamnesisService;

    private final UpdateAnamnesisService updateAnamnesisService;

    private final AnamnesisService anamnesisService;

    private final AnamnesisMapper anamnesisMapper;

    private final PdfService pdfService;

    public AnamnesisController(InternmentEpisodeService internmentEpisodeService,
                               CreateAnamnesisService createAnamnesisService,
                               UpdateAnamnesisService updateAnamnesisService,
                               AnamnesisService anamnesisService,
                               AnamnesisMapper anamnesisMapper,
                               PdfService pdfService) {
        this.internmentEpisodeService = internmentEpisodeService;
        this.createAnamnesisService = createAnamnesisService;
        this.updateAnamnesisService = updateAnamnesisService;
        this.anamnesisService = anamnesisService;
        this.anamnesisMapper = anamnesisMapper;
        this.pdfService = pdfService;
    }

    @PostMapping
    @Transactional
    @InternmentValid
    @AnamnesisValid
    @AnamnesisMainDiagnosisValid
    @EffectiveVitalSignTimeValid
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO_ADULTO_MAYOR')")
    public ResponseEntity<ResponseAnamnesisDto> createAnamnesis(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @RequestBody @Valid AnamnesisDto anamnesisDto) throws IOException, DocumentException {
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, ananmnesis {}",
                institutionId, internmentEpisodeId, anamnesisDto);
        Integer patientId = internmentEpisodeService.getPatient(internmentEpisodeId)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_EPISODE));
        AnamnesisBo anamnesis = anamnesisMapper.fromAnamnesisDto(anamnesisDto);
        anamnesis = createAnamnesisService.createDocument(internmentEpisodeId, patientId, anamnesis);
        ResponseAnamnesisDto result = anamnesisMapper.fromAnamnesis(anamnesis);
        LOG.debug(OUTPUT, result);
        generateDocument(anamnesis, institutionId, internmentEpisodeId, patientId);
        return  ResponseEntity.ok().body(result);
    }


    @PutMapping("/{anamnesisId}")
    @InternmentValid
    @AnamnesisValid
    @AnamnesisMainDiagnosisValid
    @EffectiveVitalSignTimeValid
    @DocumentValid(isConfirmed = false, documentType = DocumentType.ANAMNESIS)
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO_ADULTO_MAYOR')")
    public ResponseEntity<ResponseAnamnesisDto> updateAnamnesis(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @PathVariable(name = "anamnesisId") Long anamnesisId,
            @Valid @RequestBody AnamnesisDto anamnesisDto) throws IOException, DocumentException  {
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, anamnesisId {}, ananmnesis {}",
                institutionId, internmentEpisodeId, anamnesisId, anamnesisDto);
        AnamnesisBo anamnesis = anamnesisMapper.fromAnamnesisDto(anamnesisDto);
        Integer patientId = internmentEpisodeService.getPatient(internmentEpisodeId)
                .orElseThrow(() -> new EntityNotFoundException(INVALID_EPISODE));
        anamnesis = updateAnamnesisService.updateDocument(internmentEpisodeId, patientId, anamnesis);
        ResponseAnamnesisDto result = anamnesisMapper.fromAnamnesis(anamnesis);
        generateDocument(anamnesis, institutionId, internmentEpisodeId, patientId);
        LOG.debug(OUTPUT, result);

        return  ResponseEntity.ok().body(result);
    }

    private void generateDocument(AnamnesisBo anamnesis, Integer institutionId, Integer internmentEpisodeId,
                                  Integer patientId) throws IOException, DocumentException {
        OnGenerateInternmentDocumentEvent event = new OnGenerateInternmentDocumentEvent(anamnesis, institutionId, internmentEpisodeId,
                EDocumentType.map(DocumentType.ANAMNESIS), patientId);
        pdfService.loadDocument(event);
    }

    @GetMapping("/{anamnesisId}")
    @InternmentValid
    @DocumentValid(isConfirmed = false, documentType = DocumentType.ANAMNESIS)
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO_ADULTO_MAYOR')")
    public ResponseEntity<ResponseAnamnesisDto> getAnamnesis(
            @PathVariable(name = "institutionId") Integer institutionId,
            @PathVariable(name = "internmentEpisodeId") Integer internmentEpisodeId,
            @PathVariable(name = "anamnesisId") Long anamnesisId){
        LOG.debug("Input parameters -> institutionId {}, internmentEpisodeId {}, anamnesisId {}",
                institutionId, internmentEpisodeId, anamnesisId);
        AnamnesisBo anamnesis = anamnesisService.getDocument(anamnesisId);
        ResponseAnamnesisDto result = anamnesisMapper.fromAnamnesis(anamnesis);
        LOG.debug(OUTPUT, result);
        return  ResponseEntity.ok().body(result);
    }
}