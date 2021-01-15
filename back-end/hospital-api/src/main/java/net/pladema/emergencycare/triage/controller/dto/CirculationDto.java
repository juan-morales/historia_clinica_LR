package net.pladema.emergencycare.triage.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pladema.clinichistory.documents.controller.dto.NewEffectiveClinicalObservationDto;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class CirculationDto implements Serializable {

    private final Short perfusionId;

    private final NewEffectiveClinicalObservationDto heartRate;

}
