package net.pladema.establishment.service;

import net.pladema.address.controller.service.domain.AddressBo;
import net.pladema.establishment.service.domain.InstitutionBasicInfoBo;
import net.pladema.establishment.service.domain.InstitutionBo;

import java.util.List;

public interface InstitutionService {

	List<InstitutionBasicInfoBo> getInstitutionsByImageSectors();

	InstitutionBo get(Integer id);
	
	InstitutionBo get(String sisaCode);

	AddressBo getAddress(Integer institutionId);

	List<InstitutionBasicInfoBo> getFromInstitutionDestinationReference(Short departmentId, Integer clinicalSpecialtyId, Integer careLineId);

	List<InstitutionBasicInfoBo> getVirtualConsultationInstitutions();
	
	List<InstitutionBasicInfoBo> getInstitutionsByReferenceByPracticeFilter(Short departmentId, Integer practiceSnomedId, Integer clinicalSpecialtyId, Integer careLineId);

}
