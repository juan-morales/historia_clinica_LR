package net.pladema.patient.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PatientRegistrationSearchFilter extends PatientSearchFilter{

	private Integer patientId;

	private Boolean toAudit;

	private Boolean automaticValidation;

	private Boolean manualValidation;

	private Boolean temporary;

	private Boolean permanentNotValidated;

	private Boolean validated;

	private Boolean permanent;

}
