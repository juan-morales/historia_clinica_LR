package net.pladema.staff.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nullable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ProfessionalLicenseNumberValidationResponseDto {

	private Boolean twoFactorAuthenticationEnabled;

	private Boolean healthcareProfessionalLicenseNumberValid;

	private Boolean healthcareProfessionalCompleteContactData;

	private Boolean healthcareProfessionalHasLicenses;

	@Nullable
	private String patientEmail;

	public ProfessionalLicenseNumberValidationResponseDto() {
		this.twoFactorAuthenticationEnabled = true;
		this.healthcareProfessionalLicenseNumberValid = true;
		this.healthcareProfessionalCompleteContactData = true;
		this.healthcareProfessionalHasLicenses = true;
	}


}
