package ar.lamansys.refcounterref.infraestructure.input.rest.dto;

import ar.lamansys.refcounterref.domain.enums.EReferenceClosureType;
import ar.lamansys.refcounterref.domain.enums.EReferencePriority;
import ar.lamansys.sgx.shared.dates.controller.dto.DateTimeDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class ReferenceReportDto {

	private Integer referenceId;

	private String patientFullName;

	private String identificationType;

	private String identificationNumber;

	private List<String> problems;

	private EReferencePriority priority;

	private DateTimeDto date;

	private String clinicalSpecialtyOrigin;

	private String institutionOrigin;

	private String	institutionDestination;

	private String clinicalSpecialtyDestination;

	private String careLine;

	private EReferenceClosureType closureType;

}
