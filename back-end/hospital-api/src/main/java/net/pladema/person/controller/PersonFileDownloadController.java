package net.pladema.person.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.clinichistory.requests.servicerequests.service.domain.StoredFileBo;
import net.pladema.person.service.GetPersonFileService;

@RequiredArgsConstructor
@Slf4j
@RestController
@Tag(name = "Person File", description = "Person File")
@RequestMapping("/institution/{institutionId}/person/{personId}/file")
public class PersonFileDownloadController {
	private final GetPersonFileService getPersonFileService;
	public static final String OUTPUT = "Output -> {}";

	@GetMapping("/download/{fileId}")
	@PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ADMINISTRATIVO_RED_DE_IMAGENES')")
	public ResponseEntity<Resource> download(@PathVariable(name = "institutionId") Integer institutionId,
								   @PathVariable(name = "personId") Integer personId,
								   @PathVariable(name = "fileId") Integer fileId
	) {
		log.debug("Input parameters -> institutionId {} personId {}, fileId {}", institutionId, personId, fileId);
		StoredFileBo result = getPersonFileService.run(fileId);
		log.debug(OUTPUT, result);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(result.getContentType()))
				.contentLength(result.getContentLenght())
				.body(result.getResource());
	}
}
