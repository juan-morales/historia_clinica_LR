package net.pladema.questionnaires.edmonton.getpdf.controller;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.lamansys.sgx.shared.dates.configuration.JacksonDateFormatConfig;
import ar.lamansys.sgx.shared.files.pdf.PDFDocumentException;
import ar.lamansys.sgx.shared.files.pdf.PdfService;
import ar.lamansys.sgx.shared.filestorage.application.FileContentBo;
import net.pladema.questionnaires.common.dto.PrintQuestionnaireDTO;
import net.pladema.questionnaires.edmonton.getpdf.domain.service.PrintEdmontonService;

@RestController
@Validated
@RequestMapping("/institution/patient/outpatient/consultation/")
public class GetEdmontonPDFController implements GetEdmontonPDFAPI {

	public static final String OUTPUT = "Output -> {}";

	private static final Logger logger = LoggerFactory.getLogger(GetEdmontonPDFController.class);

	private final PrintEdmontonService printQuestionnaireService;

	private final PdfService pdfService;

	public GetEdmontonPDFController(PrintEdmontonService printQuestionnaireService, PdfService pdfService) {
		this.printQuestionnaireService = printQuestionnaireService;
		this.pdfService = pdfService;
	}

	@Override
	public ResponseEntity<InputStreamResource> getQuestionnaireTestPDF(Long questionnaireId) throws PDFDocumentException {
		logger.debug("input parameters -> questionnaireId: {}", questionnaireId);

		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(JacksonDateFormatConfig.ZONE_ID));

		List<PrintQuestionnaireDTO> printQuestionnaireDTO = printQuestionnaireService.getPrintQuestionnaire(questionnaireId);

		Map<String, Object> context = printQuestionnaireService.createQuestionnaireContext(printQuestionnaireDTO);

		String outputFileName = printQuestionnaireService.createQuestionnaireFileName(questionnaireId, now);


		try {
			return generatedPDFResponse(context, outputFileName);
		} catch (IOException e) {
			logger.error("Error generating PDF response", e);
			throw new PDFDocumentException(e);
		}
	}

	private ResponseEntity<InputStreamResource> generatedPDFResponse(Map<String, Object> context, String outputFileName) throws IOException {
		logger.debug("input parameters -> context: {}, outputFileName: {}", "edmonton_reports", outputFileName);
		FileContentBo fileContentBo = pdfService.generate("edmonton_reports", context);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			fileContentBo.getStream().transferTo(outputStream);
		} catch (IOException e) {
			logger.error("Error transferring stream to output stream", e);
		}

		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		InputStreamResource resource = new InputStreamResource(inputStream);
		ResponseEntity<InputStreamResource> response;
		response = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= " + outputFileName).contentType(MediaType.APPLICATION_PDF).contentLength(outputStream.size()).body(resource);

		return response;
	}
}
