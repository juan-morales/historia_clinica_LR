package ar.lamansys.refcounterref.infraestructure.input.rest;

import ar.lamansys.refcounterref.application.createcounterreferencefile.CreateCounterReferenceFile;
import ar.lamansys.refcounterref.application.deletefiles.DeleteFiles;
import ar.lamansys.refcounterref.application.getcounterreferencefile.GetCounterReferenceFile;
import ar.lamansys.sgx.shared.filestorage.infrastructure.input.rest.StoredFileBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/institutions/{institutionId}/counterreference-file")
//@Api(value = "Counte Reference File", tags = {"Counter Reference File"})
public class CounterReferenceFileController {

    private static final String OUTPUT = "Output -> {}";

    private final CreateCounterReferenceFile createCounterReferenceFile;
    private final DeleteFiles deleteFiles;
    private final GetCounterReferenceFile getCounterReferenceFile;

    @PostMapping(value = "/patient/{patientId}/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, ENFERMERO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA')")
    public Integer uploadFile(@PathVariable(name = "institutionId") Integer institutionId,
                              @PathVariable(name = "patientId") Integer patientId,
                              @RequestPart("file") MultipartFile file) throws IOException {
        log.debug("Input parameters -> {} institutionId {}, patientId {}", institutionId, patientId);
        var result = createCounterReferenceFile.run(institutionId, patientId, file);
        log.debug(OUTPUT, result);
        return result;
    }

    @GetMapping("/download/{fileId}")
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO')")
    public ResponseEntity download(@PathVariable(name = "institutionId") Integer institutionId,
                                   @PathVariable(name = "fileId") Integer fileId) {
        log.debug("Input parameters -> institutionId {}, fileId {}", institutionId, fileId);
        StoredFileBo result = getCounterReferenceFile.run(fileId);
        log.debug(OUTPUT, result);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .contentLength(result.getContentLenght())
                .body(result.getResource());
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasPermission(#institutionId, 'ESPECIALISTA_MEDICO, PROFESIONAL_DE_SALUD, ESPECIALISTA_EN_ODONTOLOGIA, ENFERMERO')")
    public boolean delete(@PathVariable(name = "institutionId") Integer institutionId,
                          @RequestParam(name = "fileIds") List<Integer> fileIds) {
        log.debug("Input parameters -> institutionId {}, fileIds {}", institutionId, fileIds);
        deleteFiles.run(fileIds);
        return true;
    }

}