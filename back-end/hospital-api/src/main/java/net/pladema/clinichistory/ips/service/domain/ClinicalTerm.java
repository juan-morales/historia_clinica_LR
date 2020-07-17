package net.pladema.clinichistory.ips.service.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.pladema.clinichistory.ips.repository.masterdata.entity.ConditionClinicalStatus;

@Getter
@Setter
@ToString
public abstract class ClinicalTerm {

    private Integer id;

    private String statusId ;

    private SnomedBo snomed;

    private String status;


    public boolean isActive(){
        return statusId.equals(ConditionClinicalStatus.ACTIVE);
    }

}
