package ar.lamansys.sgh.clinichistory.infrastructure.input.service;

import ar.lamansys.sgh.clinichistory.application.document.DocumentService;
import ar.lamansys.sgh.clinichistory.application.ports.DocumentFileStorage;
import ar.lamansys.sgh.clinichistory.application.rebuildFile.RebuildFile;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.Document;
import ar.lamansys.sgh.shared.infrastructure.input.service.DocumentReduceInfoDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedDocumentPort;
import ar.lamansys.sgx.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharedDocumentPortImpl implements SharedDocumentPort {

	private final DocumentService documentService;
	private final RebuildFile rebuildFile;
	private final DocumentFileStorage documentFileStorage;

	@Override
	public void deleteDocument(Long documentId, String newDocumentStatus) {
		log.debug("Input parameter documentId {}, newDocumentStatus {}", documentId, newDocumentStatus);
		documentService.deleteById(documentId, newDocumentStatus);
	}

	@Override
	public void updateDocumentModificationReason(Long documentId, String reason) {
		log.debug("Input parameter documentId {}, reason {}", documentId, reason);
		documentService.updateDocumentModificationReason(documentId, reason);
	}
	
	@Override
	public DocumentReduceInfoDto getDocument(Long documentId) {
		log.debug("Input parameter documentId {}", documentId);
		Document document = documentService.findById(documentId)
				.orElseThrow(() -> new NotFoundException("document-not-exists", String.format("No existe el documento con id %s", documentId)));
		DocumentReduceInfoDto result = new DocumentReduceInfoDto();
		result.setSourceId(document.getSourceId());
		result.setCreatedBy(document.getCreatedBy());
		result.setCreatedOn(document.getCreatedOn());
		result.setTypeId(document.getTypeId());
		return result;
	}

	@Override
	public void rebuildFilesFromPatient(Integer patient) {
		List<Long> documentsIds = documentService.getDocumentsIdsFromPatient(patient);
		List<Long> documentFilesIds = documentFileStorage.getIdsByDocumentsIds(documentsIds);
		documentFilesIds.forEach(this::rebuildFile);
	}

	@Override
	public void rebuildFile(Long documentId) {
		log.debug("Input parameter documentId {}", documentId);
		rebuildFile.run(documentId);
	}

}
