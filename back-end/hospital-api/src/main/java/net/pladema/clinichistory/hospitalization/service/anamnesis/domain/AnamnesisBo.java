package net.pladema.clinichistory.hospitalization.service.anamnesis.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.pladema.clinichistory.documents.repository.ips.masterdata.entity.DocumentType;
import net.pladema.clinichistory.documents.service.Document;
import net.pladema.clinichistory.hospitalization.service.domain.ClinicalSpecialtyBo;
import net.pladema.clinichistory.documents.repository.ips.masterdata.entity.DocumentStatus;
import net.pladema.clinichistory.documents.service.ips.domain.*;
import net.pladema.clinichistory.outpatient.createoutpatient.service.domain.ProblemBo;
import net.pladema.clinichistory.outpatient.createoutpatient.service.domain.ProcedureBo;
import net.pladema.clinichistory.outpatient.createoutpatient.service.domain.ReasonBo;
import net.pladema.clinichistory.outpatient.repository.domain.SourceType;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@ToString
public class AnamnesisBo implements Document {

    private Long id;

    private Integer patientId;

    private Integer encounterId;

    private boolean confirmed;

    private DocumentObservationsBo notes;

    private HealthConditionBo mainDiagnosis;

    private List<DiagnosisBo> diagnosis;

    private List<HealthHistoryConditionBo> personalHistories;

    private List<ProcedureBo> procedures;

    private List<HealthHistoryConditionBo> familyHistories;

    private List<MedicationBo> medications;

    private List<ImmunizationBo> immunizations;

    private List<AllergyConditionBo> allergies;

    private AnthropometricDataBo anthropometricData;

    private VitalSignBo vitalSigns;

    public String getDocumentStatusId(){
        return confirmed ? DocumentStatus.FINAL : DocumentStatus.DRAFT;
    }

    @Override
    public List<ProblemBo> getProblems() {
        return Collections.emptyList();
    }

    @Override
    public List<ReasonBo> getReasons() {
        return Collections.emptyList();
    }

    @Override
    public ClinicalSpecialtyBo getClinicalSpecialty() {
        return null;
    }
    
    public short getDocumentType() {
        return DocumentType.ANAMNESIS;
    }

    @Override
    public Short getDocumentSource() {
        return SourceType.HOSPITALIZATION;
    }
}
