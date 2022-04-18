package ar.lamansys.sgh.clinichistory.infrastructure.input.service;

import ar.lamansys.sgh.clinichistory.application.document.DocumentService;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.entity.Document;
import ar.lamansys.sgh.shared.infrastructure.input.service.DocumentReduceInfoDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedDocumentPort;
import ar.lamansys.sgx.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharedDocumentPortImpl implements SharedDocumentPort {

	private final DocumentService documentService;

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
}
