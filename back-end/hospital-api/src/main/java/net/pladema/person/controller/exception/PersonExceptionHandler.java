package net.pladema.person.controller.exception;

import ar.lamansys.sgx.shared.exceptions.dto.ApiErrorMessageDto;
import net.pladema.clinichistory.hospitalization.controller.exceptions.HospitalizationExceptionHandler;
import net.pladema.person.controller.service.exceptions.CreatePersonException;

import net.pladema.person.controller.service.exceptions.CreatePersonFileException;

import net.pladema.person.controller.service.exceptions.PersonException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = {"net.pladema.person"})
public class PersonExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HospitalizationExceptionHandler.class);

	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ExceptionHandler({CreatePersonException.class})
	protected ApiErrorMessageDto handleCreatePersonException(CreatePersonException ex) {
		LOG.debug("CreatePersonException exception -> {}", ex.getMessage());
		return new ApiErrorMessageDto(ex.getCode().name(), ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({CreatePersonFileException.class})
	protected ApiErrorMessageDto handleCreatePersonFileException(CreatePersonFileException ex) {
		LOG.debug("CreatePersonFileException exception -> {}", ex.getMessage());
		return new ApiErrorMessageDto(ex.getCode().name(), ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({PersonException.class})
	protected ApiErrorMessageDto handlePersonException(PersonException ex) {
		LOG.debug("PersonException exception -> {}", ex.getMessage());
		return new ApiErrorMessageDto(ex.getCode().name(), ex.getMessage());
	}

}