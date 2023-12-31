package net.pladema.template.application.save;

import ar.lamansys.sgx.shared.files.FileService;
import ar.lamansys.sgx.shared.files.infrastructure.output.repository.FileInfo;
import ar.lamansys.sgx.shared.filestorage.application.FileContentBo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.template.application.create.CreateTemplate;
import net.pladema.template.application.existsname.ExistsNameDocumentTemplate;
import net.pladema.template.application.port.DocumentTemplateStorage;
import net.pladema.template.domain.DocumentTemplateBo;
import net.pladema.template.domain.enums.EDocumentTemplate;
import net.pladema.template.domain.exceptions.DocumentTemplateException;
import net.pladema.template.domain.exceptions.EDocumentTemplateException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveDocumentTemplate {

    private final ExistsNameDocumentTemplate existsNameDocumentTemplate;
    private final DocumentTemplateStorage documentTemplateStorage;
    private final FileService fileService;

    public void run(CreateTemplate createTemplate, DocumentTemplateBo documentTemplateBo) throws JsonProcessingException {

        log.debug("Input -> DocumentTemplateBo {}", documentTemplateBo);

        if (existsNameDocumentTemplate.run(documentTemplateBo.getUserId(), documentTemplateBo.getName(), documentTemplateBo.getTypeId()))
            throw new DocumentTemplateException(EDocumentTemplateException.DUPLICATE_NAME, "document.template.error.duplicated-name");


        String json = createTemplate.run(documentTemplateBo);

        FileContentBo templateStream = FileContentBo.fromString(json);
        String uuid = fileService.createUuid();
        var path = fileService.buildCompletePath(String.format("/user/%d/templates/%s/%s",
                documentTemplateBo.getUserId(),
                EDocumentTemplate.map(documentTemplateBo.getTypeId()).getDescription(),
                uuid));
        FileInfo result = fileService.saveStreamInPath(path, uuid, "PLANTILLA", false, templateStream);

        Long fileId = result.getId();
        documentTemplateStorage.save(documentTemplateBo, fileId);

        log.debug("Output -> template {} saved", fileId);
    }


}
