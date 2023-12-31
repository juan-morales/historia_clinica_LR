package net.pladema.establishment.controller.exceptions;

import ar.lamansys.sgx.shared.exceptions.dto.ApiErrorMessageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.pladema.establishment.controller.service.exceptions.HierarchicalUnitStaffException;

import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "net.pladema.establishment")
@Slf4j
public class EstablishmentExceptionHandler {

	private final MessageSource messageSource;

	private ApiErrorMessageDto buildErrorMessage(String error, Locale locale) {
		return new ApiErrorMessageDto(
				error,
				messageSource.getMessage(error, null, error, locale)
		);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ HierarchicalUnitStaffException.class })
	protected ApiErrorMessageDto handleHierarchicalUnitStaffException(HierarchicalUnitStaffException ex, Locale locale) {
		log.debug("HierarchicalUnitStaffException exception -> {}", ex.getMessage());
		return buildErrorMessage(ex.getMessage(), locale);
	}
}
