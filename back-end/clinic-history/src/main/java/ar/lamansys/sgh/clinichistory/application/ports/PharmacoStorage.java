package ar.lamansys.sgh.clinichistory.application.ports;

import ar.lamansys.sgh.clinichistory.domain.ips.PharmacoBo;
import ar.lamansys.sgh.clinichistory.domain.ips.PharmacoSummaryBo;

import java.util.List;
import java.util.Optional;

public interface PharmacoStorage {

	Integer createPharmaco(PharmacoBo pharmacoBo);

	List<PharmacoSummaryBo> getInternmentEpisodePharmacos(Integer internmentEpisodeId, Short sourceTypeId);

	Optional<PharmacoBo> findById(Integer id);

	List<PharmacoSummaryBo> getMostFrequentPharmacos(Integer professionalId, Integer institutionId, Integer limit);

}
