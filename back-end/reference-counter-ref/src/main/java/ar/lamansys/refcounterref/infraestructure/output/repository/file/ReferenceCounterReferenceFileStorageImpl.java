package ar.lamansys.refcounterref.infraestructure.output.repository.file;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ar.lamansys.refcounterref.application.port.ReferenceCounterReferenceFileStorage;
import ar.lamansys.refcounterref.domain.enums.EReferenceCounterReferenceType;
import ar.lamansys.refcounterref.domain.file.ReferenceCounterReferenceFileBo;
import ar.lamansys.sgx.shared.filestorage.infrastructure.input.rest.StoredFileBo;
import ar.lamansys.sgx.shared.files.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReferenceCounterReferenceFileStorageImpl implements ReferenceCounterReferenceFileStorage {

    private static final String RELATIVE_DIRECTORY = "/patient/{patiendId}/reference-counter-reference/";
    private final String OUTPUT = "Output -> {}";

    private final FileService fileService;
    private final ReferenceCounterReferenceFileRepository referenceCounterReferenceFileRepository;

    @Override
	@Transactional //Transaccion compleja
    public Integer save(Integer institutionId, Integer patientId, MultipartFile file, Integer type) throws IOException {

        String newFileName = fileService.createFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
        String partialPath = buildPartialPath(patientId, newFileName);
		String uuid = newFileName.split("\\.")[0];
        fileService.transferMultipartFile(partialPath, uuid, "REFERENCIA_CONTRAREFERENCIA", file);

        Integer result = saveReferenceCounterReferenceFileMetadata(partialPath, file, type);
        log.debug(OUTPUT, result);
        return result;

    }

    @Override
    public void updateReferenceCounterReferenceId(Integer referenceCounterReferenceId, List<Integer> fileIds) {
        log.debug("Input parameters -> referenceCounterReferenceId {}, fileIds {}", referenceCounterReferenceId, fileIds);
        fileIds.stream().forEach(fileId -> {
            ReferenceCounterReferenceFile referenceFile = referenceCounterReferenceFileRepository.findById(fileId).get();
            referenceFile.setReferenceCounterReferenceId(referenceCounterReferenceId);
            referenceCounterReferenceFileRepository.save(referenceFile);
        });
    }

    @Override
    public StoredFileBo getFile(Integer fileId, Integer type) {
        log.debug("Input parameters -> fileId {}, type {}", fileId, type);
        StoredFileBo result = referenceCounterReferenceFileRepository.findByIdAndType(fileId, type).stream().map(rcrf ->
                new StoredFileBo(
                        fileService.loadFileRelativePath(rcrf.getPath()),
                        rcrf.getContentType(),
                        rcrf.getSize())).findFirst().orElse(null);
        log.debug(OUTPUT, result);
        return result;
    }

    @Override
    public Map<Integer, List<ReferenceCounterReferenceFileBo>> getFilesByReferenceCounterReferenceIdsAndType(List<Integer> referenceCounterReferenceIds, EReferenceCounterReferenceType type) {
        log.debug("Input parameters -> referenceCounterReferenceIds {}, type {}", referenceCounterReferenceIds, type);
        Map<Integer, List<ReferenceCounterReferenceFileBo>> result = referenceCounterReferenceFileRepository.findByReferenceCounterReferenceIdsAndType(referenceCounterReferenceIds, type.getId().intValue())
                .stream()
                .map(ReferenceCounterReferenceFileBo::new)
                .collect(Collectors.groupingBy(ReferenceCounterReferenceFileBo::getReferenceCounterReferenceId));
        log.debug(OUTPUT, result);
        return result;
    }

    @Override
    public List<ReferenceCounterReferenceFileBo> getFilesByReferenceCounterReferenceIdAndType(Integer referenceCounterReferenceId, Integer type) {
        log.debug("Input parameters -> referenceCounterReferenceId {}, type {}", referenceCounterReferenceId, type);
        return referenceCounterReferenceFileRepository.findByReferenceCounterReferenceIdAndType(referenceCounterReferenceId, type);
    }
    public void deleteFiles(List<Integer> filesIds) {
        log.debug("Input parameters -> filesIds {}", filesIds);
        referenceCounterReferenceFileRepository.deleteAllById(filesIds);
    }

    private String buildPartialPath(Integer patientId, String relativeFilePath) {
        log.debug("Input parameters -> patientId {}, relativeFilePath {}", patientId, relativeFilePath);
        String result = RELATIVE_DIRECTORY
                .replace("{patiendId}", patientId.toString())
                .concat(relativeFilePath);
        log.debug(OUTPUT, result);
        return result;
    }

    private Integer saveReferenceCounterReferenceFileMetadata(String completePath, MultipartFile file, Integer type) {
        ReferenceCounterReferenceFile referenceCounterReferenceFile = new ReferenceCounterReferenceFile(
                completePath,
                file.getContentType(),
                file.getSize(),
                file.getOriginalFilename(),
                type
        );
        Integer result = referenceCounterReferenceFileRepository.save(referenceCounterReferenceFile).getId();
        log.debug(OUTPUT, result);
        return result;
    }

}