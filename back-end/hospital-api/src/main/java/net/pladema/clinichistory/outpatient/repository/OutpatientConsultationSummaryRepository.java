package net.pladema.clinichistory.outpatient.repository;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.HealthConditionSummaryVo;
import net.pladema.clinichistory.outpatient.repository.domain.ConsultationsVo;
import net.pladema.clinichistory.outpatient.repository.domain.OutpatientEvolutionSummaryVo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.ProcedureSummaryVo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.ReasonSummaryVo;

import java.util.List;

public interface OutpatientConsultationSummaryRepository {

    List<OutpatientEvolutionSummaryVo> getAllOutpatientEvolutionSummary(Integer patientId);

    List<HealthConditionSummaryVo> getHealthConditionsByPatient(Integer patientId, List<Integer> outpatientIds);

    List<ReasonSummaryVo> getReasonsByPatient(Integer patientId, List<Integer> outpatientIds);

    List<ProcedureSummaryVo> getProceduresByPatient(Integer patientId, List<Integer> outpatientIds);

    List<ConsultationsVo> getOutpatientConsultations(Integer patientId);
}
