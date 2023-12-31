package net.pladema.odontologicalreports.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class OdontologyConsultationDetail {

	private String institution;

	private String professional;

	private String procedures;

	private String counter;

}
