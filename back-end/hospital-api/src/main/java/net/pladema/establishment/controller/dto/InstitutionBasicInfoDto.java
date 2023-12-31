package net.pladema.establishment.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class InstitutionBasicInfoDto implements Serializable {
	private static final long serialVersionUID = -6806500543924261426L;

	private Integer id;

	private String name;

}
