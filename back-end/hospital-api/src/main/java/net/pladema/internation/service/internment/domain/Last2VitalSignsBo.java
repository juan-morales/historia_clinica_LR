package net.pladema.internation.service.internment.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.pladema.internation.service.ips.domain.VitalSignBo;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Last2VitalSignsBo implements Serializable {

    private VitalSignBo current;

    private VitalSignBo previous;

}
