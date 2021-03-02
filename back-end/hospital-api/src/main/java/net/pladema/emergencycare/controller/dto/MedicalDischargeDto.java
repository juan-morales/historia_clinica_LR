package net.pladema.emergencycare.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientProblemDto;
import net.pladema.sgx.dates.controller.dto.DateTimeDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MedicalDischargeDto {

    private DateTimeDto medicalDischargeOn;

    private Boolean autopsy;
}
