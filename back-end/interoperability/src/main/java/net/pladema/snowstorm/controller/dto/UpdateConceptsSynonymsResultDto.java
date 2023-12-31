package net.pladema.snowstorm.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateConceptsSynonymsResultDto {

	private String eclKey;

	private Integer conceptsLoaded;

	private Integer erroneousConcepts;

	private Integer missingMainConcepts;

	private List<String> errorMessages;

}
