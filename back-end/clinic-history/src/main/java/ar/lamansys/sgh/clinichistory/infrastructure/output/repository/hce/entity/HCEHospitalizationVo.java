package ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hce.entity;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.ips.Snomed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.hospitalizationState.entity.ClinicalTermVo;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HCEHospitalizationVo extends ClinicalTermVo {

    private Boolean main;

    private Integer sourceId;

    private LocalDateTime entryDate;

    private LocalDateTime dischargeDate;

    private Integer patientId;

    public HCEHospitalizationVo(Integer id, Snomed snomed, String statusId, boolean main, Integer sourceId,
                                LocalDateTime startDate, LocalDateTime inactivationDate, Integer patientId) {
        super(id, snomed, statusId);
        this.main = main;
        this.sourceId = sourceId;
        this.entryDate = startDate;
        this.dischargeDate = inactivationDate;
        this.patientId = patientId;
    }
}
