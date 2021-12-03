package net.pladema.patient.infrastructure.input.shared;

import ar.lamansys.sgh.shared.infrastructure.input.service.*;
import net.pladema.audit.service.domain.enums.EActionType;
import net.pladema.patient.controller.dto.APatientDto;
import net.pladema.patient.controller.mapper.PatientMapper;
import net.pladema.patient.controller.service.PatientExternalService;
import net.pladema.patient.repository.entity.Patient;
import net.pladema.patient.service.PatientService;
import net.pladema.person.controller.dto.BMPersonDto;
import net.pladema.person.controller.service.PersonExternalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class SharedPatientImpl implements SharedPatientPort {

    private final PatientExternalService patientExternalService;

    private final PersonExternalService personExternalService;

    private final PatientService patientService;

    private final PatientMapper patientMapper;

    private final Short TYPE_ID_PERMANENTE_NO_VALIDADO = 7;


    public SharedPatientImpl(PatientExternalService patientExternalService,
                             PersonExternalService personExternalService,
                             PatientService patientService,
                             PatientMapper patientMapper) {
        this.patientExternalService = patientExternalService;
        this.personExternalService = personExternalService;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }


    @Override
    public BasicPatientDto getBasicDataFromPatient(Integer patientId) {
        var result = patientExternalService.getBasicDataFromPatient(patientId);
        return new BasicPatientDto(result.getId(), mapPersonData(result.getPerson()), result.getTypeId());
    }

    @Override
    public List<Integer> getPatientId(Short identificationTypeId, String identificationNumber, Short genderId) {
        return personExternalService.getPersonByDniAndGender(identificationTypeId, identificationNumber, genderId);
    }

    @Override
    public Integer createPatient(RequiredPatientDataDto requiredPatientDataDto) {
        APatientDto patientDto = mapToAPatientDto(requiredPatientDataDto);
        BMPersonDto createdPerson = personExternalService.addPerson(patientDto);
        personExternalService.addPersonExtended(patientDto, createdPerson.getId(), null);
        Patient createdPatient = persistPatientData(patientDto, createdPerson, patient -> {
        });
        patientService.auditActionPatient(1, createdPatient.getId(), EActionType.CREATE);
        return createdPatient.getId();
    }

    private Patient persistPatientData(APatientDto patientDto, BMPersonDto createdPerson, Consumer<Patient> addIds) {
        Patient patientToAdd = patientMapper.fromPatientDto(patientDto);
        patientToAdd.setPersonId(createdPerson.getId());
        addIds.accept(patientToAdd);
        return patientService.addPatient(patientToAdd);
    }

    private APatientDto mapToAPatientDto(RequiredPatientDataDto requiredPatientDataDto) {
        APatientDto aPatientDto = new APatientDto();
        aPatientDto.setTypeId(TYPE_ID_PERMANENTE_NO_VALIDADO);
        aPatientDto.setBirthDate(requiredPatientDataDto.getBirthDate());
        aPatientDto.setFirstName(requiredPatientDataDto.getFirstName());
        aPatientDto.setLastName(requiredPatientDataDto.getLastName());
        aPatientDto.setGenderId(requiredPatientDataDto.getGenderId());
        aPatientDto.setIdentificationTypeId(requiredPatientDataDto.getIdentificationTypeId());
        aPatientDto.setIdentificationNumber(requiredPatientDataDto.getIdentificationNumber());
        aPatientDto.setPhoneNumber(requiredPatientDataDto.getPhoneNumber());
        aPatientDto.setEmail(requiredPatientDataDto.getEmail());
        return aPatientDto;
    }

    private BasicDataPersonDto mapPersonData(net.pladema.person.controller.dto.BasicDataPersonDto person) {
        var result = new BasicDataPersonDto();
        result.setId(person.getId());
        result.setFirstName(person.getFirstName());
        result.setMiddleNames(person.getMiddleNames());
        result.setLastName(person.getLastName());
        result.setOtherLastNames(person.getOtherLastNames());
        result.setIdentificationTypeId(person.getIdentificationTypeId());
        result.setIdentificationType(person.getIdentificationType());
        result.setIdentificationNumber(person.getIdentificationNumber());
        result.setGender(mapGender(person.getGender()));
        result.setAge(person.getAge());
        result.setBirthDate(person.getBirthDate());
        return result;
    }

    private GenderDto mapGender(net.pladema.person.controller.dto.GenderDto gender) {
        var result = new GenderDto();
        result.setId(gender.getId());
        result.setDescription(gender.getDescription());
        return result;
    }

}
