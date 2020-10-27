package net.pladema.clinichistory.outpatient.createoutpatient.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.pladema.clinichistory.hospitalization.controller.dto.ClinicalSpecialtyDto;
import net.pladema.sgx.dates.configuration.JacksonDateFormatConfig;
import net.pladema.staff.controller.dto.HealthcareProfessionalDto;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class OutpatientEvolutionSummaryDto implements Serializable {

    private Integer consultationID;

    private ClinicalSpecialtyDto clinicalSpecialty;

    private List<OutpatientSummaryHealthConditionDto> healthConditions;

    @JsonFormat(pattern = JacksonDateFormatConfig.DATE_FORMAT)
    private String startDate;

    private List<OutpatientReasonDto> reasons;

    private HealthcareProfessionalDto medic;

    private List<@Valid OutpatientProcedureDto> procedures = new ArrayList<>();

    private String evolutionNote;
}
