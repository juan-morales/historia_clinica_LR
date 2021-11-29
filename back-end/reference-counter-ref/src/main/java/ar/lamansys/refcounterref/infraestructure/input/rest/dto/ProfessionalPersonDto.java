package ar.lamansys.refcounterref.infraestructure.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalPersonDto implements Serializable {

    private static final long serialVersionUID = 1781479099977648184L;

    private Integer id;

    private String firstName;

    private String lastName;

}