package net.pladema.patient.controller.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.pladema.patient.service.domain.MedicalCoverageBo;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value= HealthInsuranceDto.class, name="HealthInsuranceDto"),
        @JsonSubTypes.Type(value= PrivateHealthInsuranceDto.class, name="PrivateHealthInsuranceDto")
})
    public abstract class CoverageDto implements Serializable {

    Integer id;

    String name;

    public abstract MedicalCoverageBo newInstance();
}
