package ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.generateFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ar.lamansys.sgh.clinichistory.domain.document.event.GenerateFilePort;
import ar.lamansys.sgh.clinichistory.domain.document.event.OnGenerateDocumentEvent;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.DocumentFile;
import ar.lamansys.sgx.shared.files.FileService;
import ar.lamansys.sgx.shared.files.pdf.PDFDocumentException;
import ar.lamansys.sgx.shared.files.pdf.PdfService;


@Component
public class GenerateFilePortImpl implements GenerateFilePort {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateFilePortImpl.class);

    public static final String OUTPUT = "Output -> {}";

    private final FileService fileService;

    private final PdfService pdfService;

    private final AuditableContextBuilder auditableContextBuilder;

    public GenerateFilePortImpl(
			FileService fileService,
            PdfService pdfService,
            AuditableContextBuilder auditableContextBuilder
    ) {
        super();
        this.fileService = fileService;
        this.pdfService = pdfService;
        this.auditableContextBuilder = auditableContextBuilder;
    }

    @Override
    public Optional<DocumentFile> save(OnGenerateDocumentEvent event)  {
        Map<String,Object> contextMap = auditableContextBuilder.buildContext(event.getDocumentBo(), event.getPatientId());

		formatStringDates(contextMap);

        String path = fileService.buildRelativePath(event.getRelativeDirectory());
        String realFileName = event.getUuid();
        String fictitiousFileName = event.buildDownloadName();
        String checksum = null;
        try {
            ByteArrayOutputStream output =  pdfService.writer(event.getTemplateName(), contextMap);
			fileService.saveStreamInPath(path, false, output);
            checksum = getHash(path);
        } catch (PDFDocumentException e) {
            LOG.error("Save document file -> {}", event, e);
        }
        return Optional.of(new DocumentFile(
                event.getDocumentBo().getId(),
                event.getEncounterId(),
                event.getSourceType(),
                event.getDocumentTypeId(), path, fictitiousFileName, realFileName, checksum));
    }

    private static String getHash(String path) {
        LOG.debug("Input parameters -> path {}", path);
        String result;
        String algorithm = "SHA-256";
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] sha256Hash = md.digest(Files.readAllBytes(Paths.get(path)));
            result = Base64.getEncoder().encodeToString(sha256Hash);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Algorithm doesn't exist -> {} ",algorithm);
            result = null;
        }
        catch (IOException e) {
            LOG.error("Error with path file {} ", path, e);
            result = null;
        }
        LOG.debug(OUTPUT, result);
        return result;
    }

	private void formatStringDates(Map<String, Object> context){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		ArrayList<ImmunizationInfoDto> vaccinesData = (ArrayList<ImmunizationInfoDto>) context.get("nonBillableImmunizations");
		vaccinesData.stream().filter(immunizationInfoDto -> immunizationInfoDto.getAdministrationDate() != null)
				.forEach(immunizationInfoDto -> immunizationInfoDto.setAdministrationDate(LocalDate.parse(immunizationInfoDto.getAdministrationDate()).format(dateTimeFormatter)));
	}

}
