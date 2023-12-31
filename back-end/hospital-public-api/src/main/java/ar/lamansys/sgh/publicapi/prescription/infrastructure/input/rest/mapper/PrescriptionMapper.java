package ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionValidStatesEnum;

import org.springframework.stereotype.Component;

import ar.lamansys.sgh.publicapi.prescription.domain.CommercialMedicationBo;
import ar.lamansys.sgh.publicapi.prescription.domain.GenericMedicationBo;
import ar.lamansys.sgh.publicapi.prescription.domain.InstitutionPrescriptionBo;
import ar.lamansys.sgh.publicapi.prescription.domain.PatientPrescriptionBo;
import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionBo;
import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionLineBo;
import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionProblemBo;
import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionProfessionBo;
import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionProfessionalRegistrationBo;
import ar.lamansys.sgh.publicapi.prescription.domain.ProfessionalPrescriptionBo;
import ar.lamansys.sgh.publicapi.prescription.domain.exceptions.PrescriptionNotFoundException;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.CommercialMedicationDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.GenericMedicationDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.InstitutionPrescriptionDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.PatientPrescriptionDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.PrescriptionDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.PrescriptionLineDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.PrescriptionProblemDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.PrescriptionProfessionDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.PrescriptionProfessionalRegistrationDto;
import ar.lamansys.sgh.publicapi.prescription.infrastructure.input.rest.dto.ProfessionalPrescriptionDto;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;

@Component
public class PrescriptionMapper {
	private final LocalDateMapper localDateMapper;
	private static final short VENCIDO = 4;
	public PrescriptionMapper(LocalDateMapper localDateMapper) {
		this.localDateMapper = localDateMapper;
	}

	public PrescriptionDto mapTo(PrescriptionBo prescriptionBo) throws PrescriptionNotFoundException {
		if (prescriptionBo.getPrescriptionId() == null) {
			throw new PrescriptionNotFoundException();
		}
		return PrescriptionDto.builder()
				.domain(prescriptionBo.getDomain())
				.prescriptionId(prescriptionBo.getPrescriptionId())
				.dueDate(prescriptionBo.getDueDate())
				.link(prescriptionBo.getLink())
				.isArchived(prescriptionBo.getIsArchived())
				.institutionPrescriptionDto(mapTo(prescriptionBo.getInstitutionPrescriptionBo()))
				.prescriptionDate(prescriptionBo.getPrescriptionDate())
				.patientPrescriptionDto(mapTo(prescriptionBo.getPatientPrescriptionBo()))
				.professionalPrescriptionDto(mapTo(prescriptionBo.getProfessionalPrescriptionBo()))
				.prescriptionsLineDto(mapToPrescriptionLineDtoList(prescriptionBo.getPrescriptionsLineBo(), prescriptionBo.getDueDate()))
				.build();
	}

	private List<PrescriptionLineDto> mapToPrescriptionLineDtoList(List<PrescriptionLineBo> prescriptionsLineBo, LocalDateTime dueDate) {
		if(prescriptionsLineBo == null) {
			return new ArrayList<>();
		}
		return prescriptionsLineBo.stream().map(line -> mapTo(line, dueDate)).collect(Collectors.toList());
	}

	private PrescriptionLineDto mapTo(PrescriptionLineBo prescriptionLineBo, LocalDateTime dueDate) {
		var due = localDateMapper.fromLocalDateTime(LocalDateTime.now()).plusDays(30).isBefore(localDateMapper.fromLocalDateTime(dueDate));
		return PrescriptionLineDto.builder()
				.prescriptionLineNumber(prescriptionLineBo.getPrescriptionLineNumber())
				.prescriptionLineStatus(due ? PrescriptionValidStatesEnum.map(VENCIDO).toString() : prescriptionLineBo.getPrescriptionLineStatus())
				.dayDosis(prescriptionLineBo.getDayDosis())
				.presentation(prescriptionLineBo.getPresentation())
				.presentationQuantity(prescriptionLineBo.getPresentationQuantity())
				.duration(prescriptionLineBo.getDuration())
				.unitDosis(prescriptionLineBo.getUnitDosis())
				.prescriptionProblemDto(mapTo(prescriptionLineBo.getPrescriptionProblemBo()))
				.genericMedicationDto(mapTo(prescriptionLineBo.getGenericMedicationBo()))
				.commercialMedicationDto(mapTo(prescriptionLineBo.getCommercialMedicationBo()))
				.quantity(prescriptionLineBo.getQuantity())
				.build();

	}

	private CommercialMedicationDto mapTo(CommercialMedicationBo commercialMedicationBo) {
		return CommercialMedicationDto.builder()
				.name(commercialMedicationBo.getName())
				.snomedId(commercialMedicationBo.getSnomedId())
				.build();
	}

	private GenericMedicationDto mapTo(GenericMedicationBo genericMedicationBo) {
		return GenericMedicationDto.builder()
				.name(genericMedicationBo.getName())
				.snomedId(genericMedicationBo.getSnomedId())
				.build();
	}

	private PrescriptionProblemDto mapTo(PrescriptionProblemBo prescriptionProblemBo) {
		return PrescriptionProblemDto.builder()
				.pt(prescriptionProblemBo.getPt())
				.problemType(prescriptionProblemBo.getProblemType())
				.snomedId(prescriptionProblemBo.getSnomedId())
				.build();
	}

	private ProfessionalPrescriptionDto mapTo(ProfessionalPrescriptionBo professionalPrescriptionBo) {
		if(professionalPrescriptionBo == null) {
			return new ProfessionalPrescriptionDto();
		}
		return ProfessionalPrescriptionDto.builder()
				.email(professionalPrescriptionBo.getEmail())
				.identificationType(professionalPrescriptionBo.getIdentificationType())
				.phoneNumber(professionalPrescriptionBo.getPhoneNumber())
				.name(professionalPrescriptionBo.getName())
				.lastName(professionalPrescriptionBo.getLastName())
				.identificationNumber(professionalPrescriptionBo.getIdentificationNumber())
				.professions(mapTo(professionalPrescriptionBo.getProfessions()))
				.registrations(mapToListPrescriptionProfessionalRegistrationDto(professionalPrescriptionBo.getRegistrations()))
				.build();
	}

	private List<PrescriptionProfessionalRegistrationDto> mapToListPrescriptionProfessionalRegistrationDto(List<PrescriptionProfessionalRegistrationBo> registrations) {
		return registrations.stream().map(this::mapTo).collect(Collectors.toList());
	}

	private PrescriptionProfessionalRegistrationDto mapTo(PrescriptionProfessionalRegistrationBo prescriptionProfessionalRegistrationBo) {
		return PrescriptionProfessionalRegistrationDto.builder()
				.registrationType(prescriptionProfessionalRegistrationBo.getRegistrationType())
				.registrationNumber(prescriptionProfessionalRegistrationBo.getRegistrationNumber())
				.build();
	}

	private List<PrescriptionProfessionDto> mapTo(List<PrescriptionProfessionBo> specialties) {
		return specialties.stream().map(this::mapTo).collect(Collectors.toList());
	}

	private PrescriptionProfessionDto mapTo(PrescriptionProfessionBo prescriptionProfessionBo) {
		return PrescriptionProfessionDto.builder()
				.snomedId(prescriptionProfessionBo.getSnomedId())
				.profession(prescriptionProfessionBo.getProfession())
				.build();
	}

	private PatientPrescriptionDto mapTo(PatientPrescriptionBo patientPrescriptionBo) {
		if(patientPrescriptionBo == null) {
			return new PatientPrescriptionDto();
		}
		return PatientPrescriptionDto.builder()
				.affiliateNumber(patientPrescriptionBo.getAffiliateNumber())
				.dniSex(patientPrescriptionBo.getDniSex())
				.gender(patientPrescriptionBo.getGender())
				.identificationNumber(patientPrescriptionBo.getIdentificationNumber())
				.identificationType(patientPrescriptionBo.getIdentificationType())
				.lastName(patientPrescriptionBo.getLastName())
				.name(patientPrescriptionBo.getName())
				.medicalCoverage(patientPrescriptionBo.getMedicalCoverage())
				.medicalCoverageCuit(patientPrescriptionBo.getMedicalCoverageCuit())
				.medicalCoveragePlan(patientPrescriptionBo.getMedicalCoveragePlan())
				.birthDate(patientPrescriptionBo.getBirthDate())
				.selfPerceivedName(patientPrescriptionBo.getSelfPerceivedName())
				.build();
	}

	private InstitutionPrescriptionDto mapTo(InstitutionPrescriptionBo institutionPrescriptionBo) {
		if(institutionPrescriptionBo == null) {
			return new InstitutionPrescriptionDto();
		}
		return InstitutionPrescriptionDto.builder()
				.name(institutionPrescriptionBo.getName() != null ? institutionPrescriptionBo.getName() : null)
				.address(institutionPrescriptionBo.getAddress() != null ? institutionPrescriptionBo.getAddress() : null)
				.provinceCode(institutionPrescriptionBo.getProvinceCode() != null ? institutionPrescriptionBo.getProvinceCode() : null)
				.sisaCode(institutionPrescriptionBo.getSisaCode() != null ? institutionPrescriptionBo.getSisaCode() : null)
				.build();
	}
}
