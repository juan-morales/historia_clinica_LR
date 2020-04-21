package net.pladema.person.controller;

import io.swagger.annotations.Api;
import net.pladema.person.controller.dto.GenderDto;
import net.pladema.person.controller.dto.IdentificationTypeDto;
import net.pladema.person.controller.mapper.GenderMapper;
import net.pladema.person.controller.mapper.IdentificationTypeMapper;
import net.pladema.person.repository.entity.Gender;
import net.pladema.person.repository.entity.IdentificationType;
import net.pladema.person.service.PersonMasterDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/person/masterdata")
@Api(value = "Person Master Data", tags = { "Person Master Data" })
public class personMasterDataController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final PersonMasterDataService personMasterDataService;

    private final GenderMapper genderMapper;

    private final IdentificationTypeMapper identificationTypeMapper;

    public personMasterDataController(PersonMasterDataService personMasterDataService, GenderMapper genderMapper, IdentificationTypeMapper identificationTypeMapper) {
        super();
        this.personMasterDataService = personMasterDataService;
        this.genderMapper = genderMapper;
        this.identificationTypeMapper = identificationTypeMapper;
    }

    @GetMapping(value = "/genders")
    public ResponseEntity<Collection<GenderDto>> getGenders() {
        LOG.debug("{}", "All genders");
        List<Gender> genders = personMasterDataService.getGenders();
        List<GenderDto> result = genderMapper.fromGenderList(genders);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/identificationTypes")
    public ResponseEntity<Collection<IdentificationTypeDto>> getIdentificationTypes() {
        LOG.debug("{}", "All identificationTypes");
        List<IdentificationType> identificationTypes = personMasterDataService.getIdentificationTypes();
        List<IdentificationTypeDto> result = identificationTypeMapper.fromIdentificationTypeList(identificationTypes);
        return ResponseEntity.ok().body(result);
    }
}
