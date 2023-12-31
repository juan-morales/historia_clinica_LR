package net.pladema.emergencycare.controller.dto;

import ar.lamansys.sgh.shared.infrastructure.input.service.ProfessionalPersonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.establishment.controller.dto.BedDto;
import net.pladema.medicalconsultation.doctorsoffice.controller.dto.DoctorsOfficeDto;
import ar.lamansys.sgx.shared.dates.controller.dto.DateTimeDto;
import ar.lamansys.sgx.shared.masterdata.infrastructure.input.rest.dto.MasterDataDto;
import net.pladema.medicalconsultation.shockroom.infrastructure.controller.dto.ShockroomDto;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyCareListDto implements Serializable {

	Integer id;

	DateTimeDto creationDate;

	EmergencyCarePatientDto patient;

	EmergencyCareEpisodeListTriageDto triage;

	MasterDataDto type;

	MasterDataDto state;

	DoctorsOfficeDto doctorsOffice;

	ProfessionalPersonDto relatedProfessional;

	ShockroomDto shockroom;

	BedDto bed;
}
