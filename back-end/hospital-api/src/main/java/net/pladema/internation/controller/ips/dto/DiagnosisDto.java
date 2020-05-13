package net.pladema.internation.controller.ips.dto;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiagnosisDto extends HealthConditionDto {

    @Nullable
    private boolean presumptive = false;

}
