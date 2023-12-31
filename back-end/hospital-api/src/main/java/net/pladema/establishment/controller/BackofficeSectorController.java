package net.pladema.establishment.controller;

import net.pladema.establishment.controller.constraints.validator.permissions.BackofficeSectorValidator;
import net.pladema.establishment.repository.SectorRepository;
import net.pladema.establishment.repository.entity.Sector;
import net.pladema.medicalconsultation.doctorsoffice.repository.DoctorsOfficeRepository;
import net.pladema.sgx.backoffice.repository.BackofficeRepository;
import net.pladema.sgx.backoffice.rest.AbstractBackofficeController;
import ar.lamansys.sgx.shared.exceptions.dto.ApiErrorMessageDto;
import net.pladema.sgx.backoffice.rest.SingleAttributeBackofficeQueryAdapter;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController
@RequestMapping("backoffice/sectors")
public class BackofficeSectorController extends AbstractBackofficeController<Sector, Integer> {

    private static final Map<String, String> constraintTocode;
    static {
		constraintTocode = Map.of(
				"uq_sector_description_institution_id", "sector-description-inst-unique"
				);

    }
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ ConstraintViolationException.class })
	public ApiErrorMessageDto handleValidationExceptions(ConstraintViolationException ex) {
		String sqlError = NestedExceptionUtils.getMostSpecificCause(ex).getLocalizedMessage();
		String constraintCode = constraintCode(ex);
		return new ApiErrorMessageDto(constraintCode, sqlError);
	}

	private String constraintCode(ConstraintViolationException ex) {
		String constraintName = ex.getConstraintName() != null ? ex.getConstraintName() : "constraint-default";
		return constraintTocode.getOrDefault(constraintName, "constraint-default");
	}
	
	public BackofficeSectorController(SectorRepository sectorRepository,
									  BackofficeSectorValidator sectorValidator) {
		super(new BackofficeRepository<Sector, Integer>(sectorRepository,
				new SingleAttributeBackofficeQueryAdapter<Sector>("description")), sectorValidator);
	}

}
