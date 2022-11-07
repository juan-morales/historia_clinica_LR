package net.pladema.clinichistory.requests.servicerequests.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ar.lamansys.sgx.shared.files.FileService;
import net.pladema.clinichistory.requests.servicerequests.repository.DiagnosticReportFileRepository;
import net.pladema.clinichistory.requests.servicerequests.repository.entity.DiagnosticReportFile;
import net.pladema.clinichistory.requests.servicerequests.service.UploadDiagnosticReportCompletedFileService;

@Service
public class UploadDiagnosticReportCompletedFileServiceImpl  implements UploadDiagnosticReportCompletedFileService {

    private final FileService fileService;
    private final DiagnosticReportFileRepository diagnosticReportFileRepository;

    private static final String RELATIVE_DIRECTORY = "/patient/{patiendId}/diagnostic-reports/{studyId}/";
    private static final Logger LOG = LoggerFactory.getLogger(UploadDiagnosticReportCompletedFileServiceImpl.class);
    private final String OUTPUT = "Output -> {}";

    public UploadDiagnosticReportCompletedFileServiceImpl(FileService fileService, DiagnosticReportFileRepository diagnosticReportFileRepository) {
        this.fileService = fileService;
        this.diagnosticReportFileRepository = diagnosticReportFileRepository;
    }

    @Override
	@Transactional // Transaccion compleja
    public List<Integer> execute(MultipartFile[] files, Integer diagnosticReportId, Integer patientId) {
        List<Integer> result = Arrays.stream(files).mapToInt(file -> {
            String newFileName = fileService.createFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
            String partialPath = buildPartialPath(patientId, newFileName, diagnosticReportId);
			String uuid = newFileName.split("\\.")[0];
			try {
				fileService.transferMultipartFile(partialPath, uuid, "RESULTADO_ESTUDIO", file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return saveDiagnosticReportFileMetadata(partialPath, file);
		})
		.boxed()
		.collect(Collectors.toList());
        LOG.debug(OUTPUT, result);
        return result;

    }

    private Integer saveDiagnosticReportFileMetadata(String completePath, MultipartFile file) {
        DiagnosticReportFile diagnosticReportFile = new DiagnosticReportFile(
                completePath,
                file.getContentType(),
                file.getSize(),
                file.getOriginalFilename());
        Integer result = diagnosticReportFileRepository.save(diagnosticReportFile).getId();
        LOG.debug(OUTPUT, result);
        return result;
    }

    private String buildPartialPath(Integer patientId, String relativeFilePath, Integer studyId){
        LOG.debug("Input parameters -> patientId {}, relativeFilePath {}", patientId, relativeFilePath);
        String result = RELATIVE_DIRECTORY
                .replace("{patiendId}", patientId.toString())
                .replace("{studyId}", studyId.toString())
                .concat(relativeFilePath);
        LOG.debug(OUTPUT, result);
        return result;
    }

    private String buildCompleteFilePath(String partialPath){
        LOG.debug("Input parameters -> partialPath {}", partialPath);
        String result = fileService.buildCompletePath(partialPath);
        LOG.debug(OUTPUT, result);
        return result;
    }

}
