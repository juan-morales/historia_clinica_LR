package net.pladema.clinichistory.hospitalization.service.indication.diet;

import java.util.List;

import ar.lamansys.sgh.shared.infrastructure.input.service.DietDto;
import net.pladema.clinichistory.hospitalization.service.indication.diet.domain.InternmentDietBo;

public interface InternmentDietService {

	List<DietDto> getInternmentEpisodeDiets(Integer internmentEpisodeId, Short sourceTypeId);
	DietDto getInternmentEpisodeDiet(Integer dietId);

	Integer addDiet(InternmentDietBo dietBo, Short sourceTypeId);
}
