package net.pladema.clinichistory.hospitalization.service.epicrisis;

import net.pladema.clinichistory.hospitalization.service.epicrisis.domain.EpicrisisBo;

public interface UpdateEpicrisisDraftService {

	Long run(Integer intermentEpisodeId, Long oldEpicrisisId, EpicrisisBo newEpicrisis);

}
