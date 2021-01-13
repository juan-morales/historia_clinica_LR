package net.pladema.person.controller.service;

import net.pladema.patient.controller.dto.APatientDto;
import net.pladema.person.controller.dto.BMPersonDto;
import net.pladema.person.controller.dto.BasicDataPersonDto;
import net.pladema.person.controller.dto.BasicPersonalDataDto;
import net.pladema.person.controller.dto.PersonPhotoDto;
import net.pladema.person.controller.dto.PersonalInformationDto;
import net.pladema.person.controller.mapper.PersonMapper;
import net.pladema.person.repository.domain.PersonalInformation;
import net.pladema.person.repository.entity.Gender;
import net.pladema.person.repository.entity.IdentificationType;
import net.pladema.person.repository.entity.Person;
import net.pladema.person.repository.entity.PersonExtended;
import net.pladema.person.service.PersonMasterDataService;
import net.pladema.person.service.PersonPhotoService;
import net.pladema.person.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonExternalServiceImpl implements PersonExternalService {

	private static final String ONE_INPUT_PARAMETER = "Input parameters -> {}";

	private static final Logger LOG = LoggerFactory.getLogger(PersonExternalServiceImpl.class);

	public static final String OUTPUT = "Output -> {}";

	private final PersonService personService;

	private final PersonMasterDataService personMasterDataService;

	private final PersonPhotoService personPhotoService;

	private final PersonMapper personMapper;

	public PersonExternalServiceImpl(PersonService personService, PersonMasterDataService personMasterDataService,
			PersonMapper personMapper, PersonPhotoService personPhotoService) {
		super();
		this.personService = personService;
		this.personMasterDataService = personMasterDataService;
		this.personMapper = personMapper;
		this.personPhotoService = personPhotoService;
		LOG.debug("{}", "created service");
	}

	@Override
	public BMPersonDto addPerson(APatientDto patient) {
		LOG.debug(ONE_INPUT_PARAMETER, patient);
		Person newPerson = personMapper.fromAPatientDto(patient);
		return persistPerson(newPerson);
	}

	@Override
	public void addPersonExtended(APatientDto patient, Integer personId, Integer addressId) {
		LOG.debug("Input parameters -> {}, {}, {}", patient, personId, addressId);
		PersonExtended personExtendedToAdd = personMapper.toPersonExtended(patient, addressId);
		LOG.debug("Mapped result updatePersonExtendedPatient -> {}", personExtendedToAdd);
		personExtendedToAdd.setId(personId);
		personExtendedToAdd.setAddressId(addressId);
		LOG.debug("Going to add person extended -> {}", personExtendedToAdd);
		PersonExtended personExtendedSaved = personService.addPersonExtended(personExtendedToAdd);
		LOG.debug("PersonExtended added -> {}", personExtendedSaved);
	}

	@Override
	public PersonExtended updatePersonExtended(APatientDto patient, Integer personId) {
		LOG.debug("Input parameters -> {}, {}", patient, personId);
		PersonExtended personExtendedToUpdate = personService.getPersonExtended(personId);
		personExtendedToUpdate = personMapper.updatePersonExtendedPatient(personExtendedToUpdate, patient);
		LOG.debug("Going to add person extended -> {}", personExtendedToUpdate);
		PersonExtended personExtendedUpdated = personService.addPersonExtended(personExtendedToUpdate);
		LOG.debug("PersonExtended added -> {}", personExtendedUpdated);
		return personExtendedUpdated;
	}
	
	@Override
	public List<Integer> getPersonByDniAndGender(Short identificationTypeId, String identificationNumber,
			Short genderId) {
		List<Integer> result = personService.getPersonByDniAndGender(identificationTypeId, identificationNumber,
				genderId);
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public PersonalInformationDto getPersonalInformation(Integer personId) {
		LOG.debug(ONE_INPUT_PARAMETER, personId);
		PersonalInformation serviceResult = personService.getPersonalInformation(personId).orElse(null);
		PersonalInformationDto result = personMapper.fromPersonalInformation(serviceResult);
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public BasicDataPersonDto getBasicDataPerson(Integer personId) {
		LOG.debug(ONE_INPUT_PARAMETER, personId);
		Person person = personService.getPerson(personId);
		Gender gender = personMasterDataService.getGender(person.getGenderId()).orElse(new Gender());
		IdentificationType identificationType = personMasterDataService.getIdentificationType(person.getIdentificationTypeId()).orElse(new IdentificationType());
		BasicDataPersonDto result = personMapper.basicDataFromPerson(person, gender, identificationType);
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public List<BasicDataPersonDto> getBasicDataPerson(Set<Integer> personIds) {
		LOG.debug(ONE_INPUT_PARAMETER, personIds);
		List<Person> people = personService.getPeople(personIds);
		List<Gender> genders = personMasterDataService.getGenders();
		List<IdentificationType> identificationTypes = personMasterDataService.getIdentificationTypes();

		List<BasicDataPersonDto> result = people.parallelStream()
				.map(p -> mapToBasicDataDto(p, genders, identificationTypes))
				.collect(Collectors.toList());
		LOG.debug("Result size {}", result.size());
		LOG.trace(OUTPUT, result);
		return result;
	}

	private BasicDataPersonDto mapToBasicDataDto(Person person, List<Gender> genders, List<IdentificationType> identificationTypes) {
		LOG.debug("Input parameters -> person {}, genders {}, identificationTypes {} ", person, genders, identificationTypes);
		Gender gender = genders.stream()
				.filter(g -> g.getId().equals(person.getGenderId())).findAny()
				.orElse(new Gender());

		IdentificationType identificationType = identificationTypes.stream()
				.filter(i -> i.getId().equals(person.getIdentificationTypeId())).findAny()
				.orElse(new IdentificationType());

		BasicDataPersonDto result = personMapper.basicDataFromPerson(person, gender, identificationType);
		LOG.debug("BasicDataPersonDto id result {}", result.getId());
		return result;
	}

	@Override
	public BasicPersonalDataDto getBasicPersonalDataDto(Integer personId) {
		LOG.debug(ONE_INPUT_PARAMETER, personId);
		Person person = personService.getPerson(personId);
		PersonExtended personExtended = personService.getPersonExtended(personId);
		BasicPersonalDataDto result = personMapper.basicPersonalDataDto(person);
		result.setPhoneNumber(personExtended.getPhoneNumber());
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public BMPersonDto updatePerson(APatientDto patient, Integer personId) {
		LOG.debug(ONE_INPUT_PARAMETER, patient);
		Person personToUpdate = personMapper.fromAPatientDto(patient);
		personToUpdate.setId(personId);
		return persistPerson(personToUpdate);
	}

	private BMPersonDto persistPerson(Person personToUpdate) {
		LOG.debug("Mapped result fromAPatientDto -> {}", personToUpdate);
		personToUpdate = personService.addPerson(personToUpdate);
		BMPersonDto result = personMapper.fromPerson(personToUpdate);
		LOG.debug("Mapped result fromPerson -> {}", result);
		LOG.debug(OUTPUT, result);
		return result;
	}

	@Override
	public PersonPhotoDto getPersonPhoto(Integer personId) {
		LOG.debug("Input parameter -> personId {}", personId);
		PersonPhotoDto personPhotoDto = personPhotoService.get(personId);
		LOG.debug(OUTPUT, personPhotoDto);
		return personPhotoDto;
	}

	@Override
	public boolean savePersonPhoto(Integer personId, String imageData) {
		LOG.debug("Input parameters -> personId {}, imageData {}", personId, imageData);
		boolean result = personPhotoService.save(personId, imageData);
		LOG.debug(OUTPUT, result);
		return result;
	}

}
