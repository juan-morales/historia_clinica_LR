package net.pladema.emergencycare.service;

import net.pladema.emergencycare.service.domain.EpisodeDischargeBo;
import net.pladema.emergencycare.service.domain.MedicalDischargeBo;
import java.time.ZoneId;

public interface EmergencyCareEpisodeDischargeService {

    boolean newMedicalDischarge(MedicalDischargeBo medicalDischargeBo, Integer institutionId);

    EpisodeDischargeBo getDischarge(Integer episodeId);

	boolean hasMedicalDischarge(Integer episodeId);

}
