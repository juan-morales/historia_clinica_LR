package net.pladema.person.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.person.controller.service.exceptions.CreatePersonFileException;
import net.pladema.person.service.DeletePersonFileService;
import net.pladema.person.service.PersonFileService;

@RequiredArgsConstructor
@Slf4j
@RestController
@Tag(name = "Person File", description = "Person File")
@RequestMapping("/institution/{institutionId}/person/{personId}/file")
public class PersonFileController {

	private final PersonFileService personFileService;
	private final DeletePersonFileService deletePersonFileService;
	public static final String OUTPUT = "Output -> {}";

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	@PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ADMINISTRATIVO_RED_DE_IMAGENES, AUDITOR_MPI')")
	public List<Integer> uploadFile(@PathVariable(name = "institutionId") Integer institutionId,
									@PathVariable(name = "personId") Integer personId,
									@RequestPart("files") MultipartFile[] files) throws CreatePersonFileException {
		log.debug("Input parameters -> institutionId {}, personId {}, ", institutionId, personId);
		var result = personFileService.addFiles(files, personId, institutionId);
		log.debug(OUTPUT, result);
		return result;
	}

	@DeleteMapping("/delete")
	@PreAuthorize("hasPermission(#institutionId, 'ADMINISTRATIVO, ADMINISTRATIVO_RED_DE_IMAGENES')")
	public boolean delete(@PathVariable(name = "institutionId") Integer institutionId,
						  @RequestParam(name = "fileIds") List<Integer> fileIds) {
		log.debug("Input parameters -> institutionId {}, fileIds {}", institutionId, fileIds);
		deletePersonFileService.run(fileIds);
		return true;
	}

}
