package net.pladema.patient.controller.mapper;

import net.pladema.address.controller.dto.AddressDto;
import net.pladema.patient.controller.dto.APatientDto;
import net.pladema.patient.controller.dto.LimitedPatientSearchDto;
import net.pladema.patient.controller.dto.MergedPatientSearchDto;
import net.pladema.patient.controller.dto.MergedPatientSearchFilter;
import net.pladema.patient.controller.dto.PatientRegistrationSearchDto;
import net.pladema.patient.controller.dto.PatientSearchDto;
import net.pladema.patient.repository.entity.Patient;
import net.pladema.patient.service.domain.LimitedPatientSearchBo;
import net.pladema.patient.service.domain.MergedPatientSearch;
import net.pladema.patient.service.domain.PatientRegistrationSearch;
import net.pladema.patient.service.domain.PatientSearch;
import net.pladema.person.controller.mapper.PersonMapper;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = {PersonMapper.class, LocalDateMapper.class})
public interface PatientMapper {

	PatientSearchDto fromPatientSearch(PatientSearch patientSearch);

	List<PatientSearchDto> fromListPatientSearch(List<PatientSearch> patientSearch);

	@Named("toPatientRegistrationSearchDto")
	@Mapping(target = "idPatient", source = "patientId")
	PatientRegistrationSearchDto toPatientRegistrationSearchDto(PatientRegistrationSearch patientRegistrationSearch);

	@Named("toMergedPatientSearchDto")
	@Mapping(target = "idPatient", source = "patientId")
	MergedPatientSearchDto toMergedPatientSearchDto(MergedPatientSearch mergedPatientSearchFilter);

	AddressDto updatePatientAddress(APatientDto patient);

	@Mapping(target = "auditTypeId", source = "auditType.id")
	Patient fromPatientDto(APatientDto patientDto);

	@Named("toLimitedPatientSearchDto")
	LimitedPatientSearchDto toLimitedPatientSearchDto(LimitedPatientSearchBo limitedPatientSearchBo);
}
