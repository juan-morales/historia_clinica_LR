package net.pladema.patient.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.lamansys.sgh.shared.infrastructure.input.service.BasicDataPersonDto;
import ar.lamansys.sgx.shared.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.patient.controller.dto.AuditPatientSearch;
import net.pladema.patient.controller.dto.DuplicatePatientDto;
import net.pladema.patient.controller.dto.MergedPatientSearchDto;
import net.pladema.patient.controller.dto.MergedPatientSearchFilter;
import net.pladema.patient.controller.dto.PatientPersonalInfoDto;
import net.pladema.patient.controller.dto.PatientRegistrationSearchDto;
import net.pladema.patient.controller.dto.PatientRegistrationSearchFilter;
import net.pladema.patient.controller.mapper.PatientMapper;
import net.pladema.patient.controller.service.exception.AuditPatientException;
import net.pladema.patient.controller.service.exception.AuditPatientExceptionEnum;
import net.pladema.patient.repository.entity.PatientType;
import net.pladema.patient.service.PatientService;
import net.pladema.patient.service.domain.MergedPatientSearch;
import net.pladema.patient.service.domain.PatientRegistrationSearch;
import net.pladema.person.controller.service.PersonExternalService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("audit")
@Tag(name = "Audit patient", description = "Audit patient")
public class AuditPatientController {

	private final PersonExternalService personExternalService;

	private final PatientService patientService;

	private final PatientMapper patientMapper;

	private final ObjectMapper jackson;

	@GetMapping("/duplicate-patients-by-filter")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public ResponseEntity<List<DuplicatePatientDto>> getDuplicatePatientSearchFilter(
			@RequestParam String searchFilterStr) {
		log.debug("searchFilterStr {}", searchFilterStr);
		AuditPatientSearch searchFilter = null;
		try {
			searchFilter = jackson.readValue(searchFilterStr, AuditPatientSearch.class);
		} catch (IOException e) {
			log.error(String.format("Error mappeando filter: %s", searchFilterStr), e);
		}
		validateFilter(searchFilter);
		List<DuplicatePatientDto> result = personExternalService.getDuplicatePersonsByFilter(searchFilter);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/patients-personal-info")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public ResponseEntity<List<PatientPersonalInfoDto>> getPatientPersonalInfo(
			@RequestParam String searchPatientInfoStr) {
		log.debug("searchPatientInfoStr {}", searchPatientInfoStr);
		DuplicatePatientDto duplicatePatientDto = null;
		try {
			duplicatePatientDto = jackson.readValue(searchPatientInfoStr, DuplicatePatientDto.class);
		} catch (IOException e) {
			log.error(String.format("Error mappeando filter: %s", searchPatientInfoStr), e);
		}
		List<PatientPersonalInfoDto> result = personExternalService.getPatientsPersonalInfo(duplicatePatientDto);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping(value = "/patient-types")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public ResponseEntity<List<PatientType>> getPatientTypes() {
		List<PatientType> result = patientService.getPatientTypesForAuditor();
		log.debug("Get all patient types for auditor -> {} ", result);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/search-registration-patients")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public  ResponseEntity<List<PatientRegistrationSearchDto>> searchRegistrationPatient(
			@RequestParam String searchFilterStr) {
		log.debug("Input data -> searchFilterStr {}", searchFilterStr);
		PatientRegistrationSearchFilter searchFilter = null;
		try {
			searchFilter = jackson.readValue(searchFilterStr, PatientRegistrationSearchFilter.class);
		} catch (IOException e) {
			log.error(String.format("Error mappeando filter: %s", searchFilterStr), e);
			if (e.getMessage().contains("out of range") || e.getMessage().contains("not a valid `int` value")) {
				return ResponseEntity.ok((Collections.emptyList()));
			}
		}
		List<PatientRegistrationSearch> result;
		if (searchFilter.getPatientId() != null)
			result = patientService.getPatientRegistrationById(searchFilter.getPatientId());
		else
			result = patientService.getPatientsRegistrationByFilter(searchFilter);
		return ResponseEntity.ok(result.stream().map(patientMapper::toPatientRegistrationSearchDto).collect(Collectors.toList()));
	}

	@GetMapping("/search-merged-patients")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public  ResponseEntity<List<MergedPatientSearchDto>> searchMergedPatient(
			@RequestParam String searchFilterStr) {
		log.debug("Input data -> searchFilterStr {}", searchFilterStr);
		MergedPatientSearchFilter searchFilter = null;
		try {
			searchFilter = jackson.readValue(searchFilterStr, MergedPatientSearchFilter.class);
		} catch (IOException e) {
			log.error(String.format("Error mappeando filter: %s", searchFilterStr), e);
		}
		List<MergedPatientSearch> result;
		result = patientService.getMergedPatientsByFilter(searchFilter);
		return ResponseEntity.ok(result.stream().map(patientMapper::toMergedPatientSearchDto).collect(Collectors.toList()));
	}

	@GetMapping("/patients-to-audit")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public ResponseEntity<List<PatientRegistrationSearchDto>> fetchPatientsToAudit() {
		List<PatientRegistrationSearch> result = patientService.getPatientsToAudit();
		log.debug("Get all patients to audit -> {} ", result.size());
		return ResponseEntity.ok(result.stream().map(patientMapper::toPatientRegistrationSearchDto).collect(Collectors.toList()));
	}

	private void validateFilter(AuditPatientSearch auditPatientSearch) {
		if ((auditPatientSearch == null) || (!auditPatientSearch.getName() && !auditPatientSearch.getIdentify() && !auditPatientSearch.getBirthdate())) {
			throw new AuditPatientException(AuditPatientExceptionEnum.INVALID_FILTER_FOR_SEARCH,String.format("No se esta filtrando por ningún dato personal."));
		}
	}

	@GetMapping("/patient/{activePatientId}/merged-patients-personal-info")
	@PreAuthorize("hasAnyAuthority('AUDITOR_MPI')")
	public ResponseEntity<List<PatientPersonalInfoDto>> getMergedPatientPersonalInfo(
			@PathVariable(name = "activePatientId") Integer activePatientId) {
		log.debug("activePatientId {}", activePatientId);
		List<PatientPersonalInfoDto> result = patientService.getMergedPersonsByPatientId(activePatientId)
				.stream().map(PatientPersonalInfoDto::new).collect(Collectors.toList());
		result.add(getPatientPersonalInfoByActivePatient(activePatientId));
		return ResponseEntity.ok().body(result);
	}

	private PatientPersonalInfoDto getPatientPersonalInfoByActivePatient(Integer patientId){
		return patientService.getActivePatient(patientId)
				.map(patient -> {
					BasicDataPersonDto person = personExternalService.getBasicDataPerson(patient.getPersonId());
					return new PatientPersonalInfoDto(patient.getId(),
							person.getFirstName(),
							person.getMiddleNames(),
							person.getLastName(),
							person.getOtherLastNames(),
							person.getIdentificationTypeId(),
							person.getIdentificationNumber(),
							person.getBirthDate(),
							person.getGender().getId(),
							null,
							null,
							person.getNameSelfDetermination(),
							patient.getTypeId());
				})
				.orElseThrow(()-> new NotFoundException("patient-not-exists", String.format("El paciente con id %s no existe", patientId)));
	}



}
