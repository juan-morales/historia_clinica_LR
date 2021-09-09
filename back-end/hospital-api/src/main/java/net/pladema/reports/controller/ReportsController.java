package net.pladema.reports.controller;

import ar.lamansys.sgx.shared.dates.configuration.JacksonDateFormatConfig;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import ar.lamansys.sgx.shared.pdf.PDFDocumentException;
import ar.lamansys.sgx.shared.pdf.PdfService;
import ar.lamansys.sgx.shared.reports.util.struct.IWorkbook;
import net.pladema.reports.controller.dto.AnnexIIDto;
import net.pladema.reports.controller.dto.FormVDto;
import net.pladema.reports.controller.mapper.ReportsMapper;
import net.pladema.reports.repository.QueryFactory;
import net.pladema.reports.service.AnnexReportService;
import net.pladema.reports.service.ExcelService;
import net.pladema.reports.service.FormReportService;
import net.pladema.reports.service.domain.AnnexIIBo;
import net.pladema.reports.service.domain.ConsultationSummaryReport;
import net.pladema.reports.service.domain.FormVBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("reports")
public class ReportsController {

    private static final Logger LOG = LoggerFactory.getLogger(ReportsController.class);

    public static final String OUTPUT = "Output -> {}";

    private final ExcelService excelService;

    private final ConsultationSummaryReport consultationSummaryReport;

    private final QueryFactory queryFactory;

    private final LocalDateMapper localDateMapper;

    private final PdfService pdfService;

    private final AnnexReportService annexReportService;

    private final FormReportService formReportService;

    private final ReportsMapper reportsMapper;

    public ReportsController(ExcelService excelService, ConsultationSummaryReport consultationSummaryReport,
                             QueryFactory queryFactory, LocalDateMapper localDateMapper, PdfService pdfService, AnnexReportService annexReportService, FormReportService formReportService, ReportsMapper reportsMapper){
        this.excelService = excelService;
        this.consultationSummaryReport = consultationSummaryReport;
        this.queryFactory = queryFactory;
        this.localDateMapper = localDateMapper;
        this.pdfService = pdfService;
        this.annexReportService = annexReportService;
        this.formReportService = formReportService;
        this.reportsMapper = reportsMapper;
    }

    @GetMapping(value = "/{institutionId}/monthly")
    public @ResponseBody
    void getMonthlyExcelReport(
            @PathVariable Integer institutionId,
            @RequestParam(value="fromDate", required = true) String fromDate,
            @RequestParam(value="toDate", required = true) String toDate,
            @RequestParam(value="clinicalSpecialtyId", required = false) Integer clinicalSpecialtyId,
            @RequestParam(value="doctorId", required = false) Integer doctorId,
            HttpServletResponse response
    ) throws Exception {
        LOG.debug("Se creará el excel {}", institutionId);
        LOG.debug("Input parameters -> institutionId {}, fromDate {}, toDate {}", institutionId, fromDate, toDate);

        String title = "DNCE-Hoja 2";
        String[] headers = new String[]{"Provincia", "Municipio", "Cod_Estable", "Establecimiento", "Apellidos paciente", "Nombres paciente", "Tipo documento",
                "Nro documento", "Fecha de nacimiento", "Género autopercibido", "Domicilio", "Teléfono", "Mail", "Obra social/Prepaga", "Nro de afiliado",
                "Fecha de atención", "Especialidad", "Profesional", "Motivo de consulta", "Problemas de Salud/Diagnóstico", "Peso", "Talla", "Tensión sistólica", "Tensión diastólica"};

        LocalDate startDate = localDateMapper.fromStringToLocalDate(fromDate);
        LocalDate endDate = localDateMapper.fromStringToLocalDate(toDate);

        // obtengo el workbook en base a la query pasada como parametro
        IWorkbook wb = this.excelService.buildExcelFromQuery(title, headers, this.queryFactory.query(institutionId, startDate, endDate, clinicalSpecialtyId, doctorId));

        // armo la respuesta con el workbook obtenido
        String filename = title + "." + wb.getExtension();
        response.addHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType(wb.getContentType());

        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
        out.flush();
        response.flushBuffer();
    }

    @GetMapping(value = "/{institutionId}/summary")
    @PreAuthorize("hasPermission(#institutionId, 'ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE')")
    public @ResponseBody void fetchConsultationSummaryReport(
            @PathVariable Integer institutionId,
            @RequestParam(value="fromDate") String fromDate,
            @RequestParam(value="toDate") String toDate,
            @RequestParam(value="clinicalSpecialtyId", required = false) Integer clinicalSpecialtyId,
            @RequestParam(value="doctorId", required = false) Integer doctorId,
            HttpServletResponse response) throws Exception {
        LOG.debug("Outpatient summary Report");
        LOG.debug("Input parameters -> institutionId {}, fromDate {}, toDate {}", institutionId, fromDate, toDate);

        LocalDate startDate = localDateMapper.fromStringToLocalDate(fromDate);
        LocalDate endDate = localDateMapper.fromStringToLocalDate(toDate);

        IWorkbook workbook = consultationSummaryReport.build(institutionId, startDate, endDate, doctorId, clinicalSpecialtyId);
        String title = "Resumen Mensual de Consultorios Externos - Hoja 2.1";
        String filename = title + "." + workbook.getExtension();
        response.addHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType(workbook.getContentType());

        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.close();
        out.flush();
        response.flushBuffer();
    }

    @GetMapping("/{institutionId}/appointment-annex")
    @PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ENFERMERO')")
    public ResponseEntity<InputStreamResource> getAppointmentAnnexReport(
            @PathVariable Integer institutionId,
            @RequestParam(name = "appointmentId") Integer appointmentId)
            throws PDFDocumentException {
        LOG.debug("Input parameter -> appointmentId {}", appointmentId);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(JacksonDateFormatConfig.ZONE_ID));
        AnnexIIBo reportDataBo = annexReportService.getAppointmentData(appointmentId);
        AnnexIIDto reportDataDto = reportsMapper.toAnexoIIDto(reportDataBo);
        Map<String, Object> context = annexReportService.createAppointmentContext(reportDataDto);
        String outputFileName = annexReportService.createOutputFileName(appointmentId, now);
        ResponseEntity<InputStreamResource> response = generatePdfResponse(context, outputFileName, "annex_report");
        LOG.debug(OUTPUT, reportDataDto);
        return response;
    }

    @GetMapping("/{institutionId}/outpatient-annex")
    @PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO')")
    public ResponseEntity<InputStreamResource> getOutpatientAnnexReport(
            @PathVariable Integer institutionId,
            @RequestParam(name = "outpatientId") Integer outpatientId)
            throws PDFDocumentException {
        LOG.debug("Input parameter -> outpatientId {}", outpatientId);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(JacksonDateFormatConfig.ZONE_ID));
        AnnexIIBo reportDataBo = annexReportService.getOutpatientData(outpatientId);
        AnnexIIDto reportDataDto = reportsMapper.toAnexoIIDto(reportDataBo);
        Map<String, Object> context = annexReportService.createOutpatientContext(reportDataDto);
        String outputFileName = annexReportService.createOutputFileName(outpatientId, now);
        ResponseEntity<InputStreamResource> response = generatePdfResponse(context, outputFileName, "annex_report");
        LOG.debug(OUTPUT, reportDataDto);
        return response;
    }

    @GetMapping("/{institutionId}/appointment-formv")
    @PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ENFERMERO')")
    public ResponseEntity<InputStreamResource> getFormVAppointmentReport(
            @PathVariable Integer institutionId,
            @RequestParam(name = "appointmentId") Integer appointmentId)
            throws PDFDocumentException {
        LOG.debug("Input parameter -> appointmentId {}", appointmentId);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(JacksonDateFormatConfig.ZONE_ID));
        FormVBo reportDataBo = formReportService.getAppointmentData(appointmentId);
        FormVDto reportDataDto = reportsMapper.toFormVDto(reportDataBo);
        Map<String, Object> context = formReportService.createAppointmentContext(reportDataDto);
        String outputFileName = formReportService.createOutputFileName(appointmentId, now);
        ResponseEntity<InputStreamResource> response = generatePdfResponse(context, outputFileName, "form_report");
        LOG.debug(OUTPUT, reportDataDto);
        return response;
    }

    @GetMapping("/{institutionId}/outpatient-formv")
    @PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO')")
    public ResponseEntity<InputStreamResource> getFormVOutpatientReport(
            @PathVariable Integer institutionId,
            @RequestParam(name = "outpatientId") Integer outpatientId)
            throws PDFDocumentException {
        LOG.debug("Input parameter -> outpatientId {}", outpatientId);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(JacksonDateFormatConfig.ZONE_ID));
        FormVBo reportDataBo = formReportService.getOutpatientData(outpatientId);
        FormVDto reportDataDto = reportsMapper.toFormVDto(reportDataBo);
        Map<String, Object> context = formReportService.createOutpatientContext(reportDataDto);
        String outputFileNames = formReportService.createOutputFileName(outpatientId, now);
        ResponseEntity<InputStreamResource> response = generatePdfResponse(context, outputFileNames, "form_report");
        LOG.debug(OUTPUT, reportDataDto);
        return response;
    }

    private ResponseEntity<InputStreamResource> generatePdfResponse(Map<String, Object> context, String outputFileName, String templateName) throws PDFDocumentException {
        LOG.debug("Input parameters -> context {}, outputFileName {}", context, outputFileName);
        ByteArrayOutputStream outputStream = pdfService.writer(templateName, context);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
        ResponseEntity<InputStreamResource> response;
        response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + outputFileName)
                .contentType(MediaType.APPLICATION_PDF).contentLength(outputStream.size()).body(resource);
        return response;
    }
}
