package net.pladema.sisa.refeps.services;

import net.pladema.sisa.refeps.controller.dto.LicenseDataDto;
import net.pladema.sisa.refeps.controller.dto.ValidatedLicenseDataDto;
import net.pladema.sisa.refeps.services.domain.ValidatedLicenseNumberBo;
import net.pladema.sisa.refeps.services.exceptions.RefepsApiException;
import net.pladema.sisa.refeps.services.exceptions.RefepsLicenseException;

import java.util.List;

public interface RefepsService {

	List<ValidatedLicenseNumberBo> validateLicenseNumber(String identificationNumber, List<String> licenses) throws RefepsApiException, RefepsLicenseException;

	List<ValidatedLicenseDataDto> validateLicenseNumberAndType(String identificationNumber, List<LicenseDataDto> licensesData) throws RefepsApiException, RefepsLicenseException;

}
