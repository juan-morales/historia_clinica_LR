package ar.lamansys.sgh.clinichistory.domain.ips;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ConclusionBo extends HealthConditionBo {
    public ConclusionBo(SnomedBo snomedBo) {
        super(snomedBo);
    }

    @Override
    @JsonIgnore
    public boolean isActive() {
        return super.isActive();
    }

}
