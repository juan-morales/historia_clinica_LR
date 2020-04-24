package net.pladema.internation.service.domain.ips;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class HealthHistoryCondition extends HealthConditionBo {

    private LocalDate date;

    private String note;
}
