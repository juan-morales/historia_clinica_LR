package net.pladema.clinichistory.requests.medicationrequests.controller.dto;

import ar.lamansys.sgh.shared.infrastructure.input.service.QuantityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ar.lamansys.sgh.clinichistory.domain.ips.DosageBo;
import ar.lamansys.sgx.shared.dates.controller.dto.DateDto;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DosageInfoDto implements Serializable {

    private Integer id;

    private Integer frequency;

    private String periodUnit;

    private Double duration;

    private DateDto startDate;

	private Double dosesByDay;

	private Double dosesByUnit;

	private QuantityDto quantityDto;

    private boolean expired = false;

    private boolean chronic = false;

    public static DosageInfoDto from(DosageBo dosage) {
        if (dosage.getId() == null)
            return null;
        DosageInfoDto result = new DosageInfoDto();
        result.setId(dosage.getId());
        result.setFrequency(dosage.getFrequency());
        result.setPeriodUnit(dosage.getPeriodUnit());
        result.setDuration(dosage.getDuration());
        result.setStartDate(new DateDto(dosage.getStartDate().getYear(),
                dosage.getStartDate().getMonthValue(),
                dosage.getStartDate().getDayOfMonth()));
        result.setChronic(dosage.isChronic());
        result.setExpired(dosage.isExpired());
		result.setDosesByUnit(dosage.getDosesByUnit());
		result.setDosesByDay(dosage.getDosesByDay());
		result.setQuantityDto(new QuantityDto(dosage.getQuantity().getValue(), dosage.getQuantity().getUnit()));
        return result;
    }

    public boolean isDailyInterval() {
        return "d".equals(periodUnit);
    }
}
