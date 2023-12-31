package ar.lamansys.sgh.clinichistory.application.ports;

import ar.lamansys.sgh.clinichistory.domain.ips.ParenteralPlanBo;

import java.util.List;
import java.util.Optional;

public interface ParenteralPlanStorage {

	Integer createParenteralPlan (ParenteralPlanBo parenteralPlan);

	List<ParenteralPlanBo> getInternmentEpisodeParenteralPlans(Integer internmentEpisodeId, Short sourceTypeId);

	Optional<ParenteralPlanBo> findById(Integer id);

	List<ParenteralPlanBo> getMostFrequentParenteralPlans(Integer professionalId, Integer institutionId, Integer limit);

}
