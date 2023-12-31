package ar.lamansys.sgh.clinichistory.application.indication.getinternmentepisodediets;

import java.util.List;

import org.springframework.stereotype.Service;

import ar.lamansys.sgh.clinichistory.application.ports.DietStorage;
import ar.lamansys.sgh.clinichistory.domain.ips.DietBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetInternmentEpisodeDiets {

	private final DietStorage storage;

	public List<DietBo> run(Integer internmentEpisodeId, Short sourceTypeId) {
		log.debug("Input parameter -> internmentEpisodeId {}, sourceTypeId {}", internmentEpisodeId, sourceTypeId);
		List<DietBo> result = storage.getInternmentEpisodeDiets(internmentEpisodeId, sourceTypeId);
		log.debug("Output -> {}", result);
		return result;
	}
}