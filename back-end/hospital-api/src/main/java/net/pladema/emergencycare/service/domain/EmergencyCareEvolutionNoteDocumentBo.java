package net.pladema.emergencycare.service.domain;

import ar.lamansys.sgh.clinichistory.domain.document.IDocumentBo;
import ar.lamansys.sgh.clinichistory.domain.document.PatientInfoBo;
import ar.lamansys.sgh.clinichistory.domain.ips.AllergyConditionBo;
import ar.lamansys.sgh.clinichistory.domain.ips.AnthropometricDataBo;
import ar.lamansys.sgh.clinichistory.domain.ips.DiagnosisBo;
import ar.lamansys.sgh.clinichistory.domain.ips.DocumentObservationsBo;
import ar.lamansys.sgh.clinichistory.domain.ips.HealthConditionBo;
import ar.lamansys.sgh.clinichistory.domain.ips.HealthHistoryConditionBo;
import ar.lamansys.sgh.clinichistory.domain.ips.MedicationBo;
import ar.lamansys.sgh.clinichistory.domain.ips.ProcedureBo;
import ar.lamansys.sgh.clinichistory.domain.ips.ReasonBo;
import ar.lamansys.sgh.clinichistory.domain.ips.RiskFactorBo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EmergencyCareEvolutionNoteDocumentBo implements IDocumentBo {

	private Long id;

	private Integer encounterId;

	private Integer clinicalSpecialtyId;

	private Integer patientId;

	private PatientInfoBo patientInfo;

	private Integer institutionId;

	private HealthConditionBo mainDiagnosis;

	private List<DiagnosisBo> diagnosis;

	private List<ReasonBo> reasons;

	private String evolutionNote;

	private AnthropometricDataBo anthropometricData;

	private RiskFactorBo riskFactors;

	private List<HealthHistoryConditionBo> familyHistories;

	private List<AllergyConditionBo> allergies;

	private List<ProcedureBo> procedures;

	private List<MedicationBo> medications;

	private LocalDateTime performedDate;

	@Override
	public Integer getPatientId() {
		if (patientInfo != null)
			return patientInfo.getId();
		return patientId;
	}

	@Override
	public DocumentObservationsBo getNotes() {
		if (evolutionNote == null)
			return null;
		DocumentObservationsBo notes = new DocumentObservationsBo();
		notes.setOtherNote(evolutionNote);
		return notes;
	}

	@Override
	public short getDocumentType() {
		return DocumentType.EMERGENCY_CARE_EVOLUTION_NOTE;
	}

	@Override
	public Short getDocumentSource() {
		return SourceType.EMERGENCY_CARE;
	}

}
