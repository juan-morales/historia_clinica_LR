package net.pladema.person.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.lamansys.sgx.shared.filestorage.application.FilePathBo;
import ar.lamansys.sgx.shared.files.images.ImageFileService;
import net.pladema.person.controller.dto.PersonPhotoDto;
import net.pladema.person.repository.PersonExtendedRepository;
import net.pladema.person.repository.domain.PersonPhotoVo;
import net.pladema.person.repository.entity.PersonExtended;
import net.pladema.person.service.PersonPhotoService;

@Service
public class PersonPhotoServiceImpl implements PersonPhotoService {

    private final Logger LOG = LoggerFactory.getLogger(PersonPhotoServiceImpl.class);

    private final String OUTPUT = "Output -> {}";
	private static final String FILE_EXTENSION = ".b64image";
    private static final String RELATIVE_DIRECTORY = "/institution/person/{personIdSubdivision}/{personId}/";

    private static final Integer SUBDIVISION_DIGITS = 2;

    private final ImageFileService imageFileService;

    private final PersonExtendedRepository personExtendedRepository;

    public PersonPhotoServiceImpl(ImageFileService imageFileService, PersonExtendedRepository personExtendedRepository){
        this.imageFileService = imageFileService;
        this.personExtendedRepository = personExtendedRepository;
    }

    @Override
    public PersonPhotoDto get(Integer personId) {
        LOG.debug("Input parameter -> personId {}", personId);
        PersonPhotoDto personPhotoDto = new PersonPhotoDto();
        Optional<FilePathBo> photoFilePath = personExtendedRepository.getPhotoFilePath(personId)
				.map(imageFileService::buildCompletePath);
        try {
			photoFilePath.ifPresent(path -> personPhotoDto.setImageData(imageFileService.readImage(path)));
		} catch (Exception e) {
			LOG.error("Error al intentar obtener la imagen del paciente con personId -> {} ", personId);
		}
        LOG.debug(OUTPUT, personPhotoDto);
        return personPhotoDto;
    }

    @Override
    public List<PersonPhotoDto> get(List<Integer> personIds) {
        LOG.debug("Input parameter -> personIds {}", personIds);

        List<PersonPhotoVo> personPhotoVoList = personExtendedRepository.getPhotoFilePath(personIds);

        List<PersonPhotoDto> result = new ArrayList<>();
        personPhotoVoList.forEach(p -> {
            PersonPhotoDto personPhotoDto = buildPersonPhotoDto(p);
            if (personPhotoDto != null)
                result.add(personPhotoDto);
        });

        LOG.debug(OUTPUT, result);
        return result;
    }

    private PersonPhotoDto buildPersonPhotoDto(PersonPhotoVo personPhotoVo) {
        LOG.debug("Input parameter -> personPhotoVo {}", personPhotoVo);

        if (personPhotoVo.relativePath != null) {
			var path = imageFileService.buildCompletePath(personPhotoVo.relativePath);
            String image = imageFileService.readImage(path);
            PersonPhotoDto result = (image != null) ? new PersonPhotoDto(personPhotoVo.personId, image) : null;
            LOG.debug(OUTPUT, result);
            return result;
        }
        return null;
    }

    @Override
	@Transactional // Transaccion compleja
    public boolean save(Integer personId, String imageData) {
        LOG.debug("Input parameters -> personId {}, imageData {}", personId, imageData);
        if (imageData == null) {
            LOG.debug(OUTPUT, false);
            return false;
        }
        String uuid = imageFileService.createUuid();
        String relativePath = RELATIVE_DIRECTORY
				.replace("{personIdSubdivision}", getNLastDigits(SUBDIVISION_DIGITS, personId))
				.replace("{personId}", personId.toString())
				.concat(uuid)
				.concat(FILE_EXTENSION);
		var path = imageFileService.buildCompletePath(relativePath);

        boolean result = imageFileService.saveImage(path, uuid, "FOTO_PERSONAL", imageData);

        PersonExtended personExtended = getPersonExtended(personId);
        personExtended.setPhotoFilePath(path.relativePath);
        personExtendedRepository.save(personExtended);
        LOG.debug(OUTPUT, result);
        return result;
    }

    private String getNLastDigits(Integer n, Integer number){
        LOG.debug("Input parameters -> n {}, number {}", n, number);
        String numberString = number.toString();
        Integer length = numberString.length();
        String result;
        if (length > n)
            result = numberString.substring(length - n);
        else
        {
            String format = "%0" + n + "d";
            result = String.format(format, number); // to fill with zeros at the left of the number
        }
        LOG.debug(OUTPUT, result);
        return result;
    }

    private PersonExtended getPersonExtended(Integer personId) {
        LOG.debug("Input parameter -> personId {}", personId);
        Optional<PersonExtended> optionalPersonExtended = personExtendedRepository.findById(personId);
        PersonExtended personExtended;
        if (optionalPersonExtended.isEmpty()){
            personExtended = new PersonExtended();
            personExtended.setId(personId);
        } else
            personExtended = optionalPersonExtended.get();
        LOG.debug(OUTPUT, personId);
        return personExtended;
    }


}
