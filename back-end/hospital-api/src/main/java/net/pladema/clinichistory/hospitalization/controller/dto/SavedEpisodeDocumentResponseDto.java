package net.pladema.clinichistory.hospitalization.controller.dto;

import ar.lamansys.sgx.shared.dates.controller.dto.DateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SavedEpisodeDocumentResponseDto {

	private Integer id;
	private String filePath;
	private String fileName;
	private String uuidFile;
	private DateDto createdOn;
	private Integer episodeDocumentTypeId;
	private Integer internmentEpisodeId;
}
