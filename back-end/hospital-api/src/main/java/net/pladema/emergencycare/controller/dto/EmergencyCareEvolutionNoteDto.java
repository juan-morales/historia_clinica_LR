package net.pladema.emergencycare.controller.dto;

import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.DiagnosisDto;
import ar.lamansys.sgh.clinichistory.infrastructure.input.rest.ips.dto.HealthConditionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientAllergyConditionDto;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientAnthropometricDataDto;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientFamilyHistoryDto;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientMedicationDto;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientProcedureDto;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientReasonDto;
import net.pladema.clinichistory.outpatient.createoutpatient.controller.dto.OutpatientRiskFactorDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyCareEvolutionNoteDto {

	private Integer patientId;

	private HealthConditionDto mainDiagnosis;

	private List<DiagnosisDto> diagnosis;

	private Integer clinicalSpecialtyId;

	private List<OutpatientReasonDto> reasons;

	private String evolutionNote;

	private OutpatientAnthropometricDataDto anthropometricData;

	private OutpatientRiskFactorDto riskFactors;

	private List<OutpatientFamilyHistoryDto> familyHistories;

	private List<OutpatientMedicationDto> medications;

	private List<OutpatientAllergyConditionDto> allergies;

	private List<OutpatientProcedureDto> procedures;

}
