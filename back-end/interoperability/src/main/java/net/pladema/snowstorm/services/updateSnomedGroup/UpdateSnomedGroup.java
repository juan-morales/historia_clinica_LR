package net.pladema.snowstorm.services.updateSnomedGroup;

import ar.lamansys.sgh.shared.infrastructure.input.service.SharedSnomedDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedSnomedPort;
import ar.lamansys.sgx.shared.dates.configuration.DateTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.snowstorm.repository.entity.SnomedGroup;
import net.pladema.snowstorm.repository.entity.SnomedGroupRepository;
import net.pladema.snowstorm.repository.entity.SnomedRelatedGroup;
import net.pladema.snowstorm.repository.entity.SnomedRelatedGroupRepository;
import net.pladema.snowstorm.services.SnowstormService;
import net.pladema.snowstorm.services.domain.SnowstormItemResponse;
import net.pladema.snowstorm.services.domain.SnowstormSearchResponse;
import net.pladema.snowstorm.services.domain.semantics.SnomedECL;
import net.pladema.snowstorm.services.domain.semantics.SnomedSemantics;
import net.pladema.snowstorm.services.exceptions.SnowstormApiException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateSnomedGroup {

    private final SnowstormService snowstormService;
    private final SnomedGroupRepository snomedGroupRepository;
    private final SharedSnomedPort sharedSnomedPort;
    private final SnomedRelatedGroupRepository snomedRelatedGroupRepository;
    private final SnomedSemantics snomedSemantics;
    private final DateTimeProvider dateTimeProvider;


    public void run(String eclKey) throws SnowstormApiException {
        log.debug("Input parameter -> eclKey {}", eclKey);
        Integer conceptsProcessed = 1;
        String searchAfter = null;
        Integer snomedGroupId = saveSnomedGroup(eclKey);
        do {
            SnowstormSearchResponse response = snowstormService.getConceptsByEclKey(eclKey, searchAfter);
            searchAfter = response.getSearchAfter();
            List<Integer> conceptIds = sharedSnomedPort.addSnomedConcepts(associateWithParentAndMapToDto(response));
            conceptsProcessed = associateConceptIdsWithSnomedGroup(snomedGroupId, conceptIds, conceptsProcessed);
            log.trace("Concepts processed -> {}", conceptsProcessed);
        }
        while (searchAfter != null);
        log.debug("Finished updating Snomed group");
    }

    private List<SharedSnomedDto> associateWithParentAndMapToDto(SnowstormSearchResponse response) {
        return response.getItems().stream()
                .map(i -> {
                    SnowstormItemResponse parent = getConceptParent(i.getConceptId());
                    return new SharedSnomedDto(i.getConceptId(),
                            i.getPt().get("term").asText(),
                            (parent != null) ? parent.getConceptId() : null,
                            (parent != null) ? parent.getPt().get("term").asText() : null);
                })
                .collect(Collectors.toList());
    }

    private SnowstormItemResponse getConceptParent(String conceptId) {
        try {
            return snowstormService.getConceptParents(conceptId).get(0);
        }
        catch (SnowstormApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer associateConceptIdsWithSnomedGroup(Integer snomedGroupId, List<Integer> conceptIds, Integer orden) {
        for (Integer snomedId : conceptIds) {
            SnomedRelatedGroup snomedRelatedGroup = new SnomedRelatedGroup(snomedId, snomedGroupId, orden);
            snomedRelatedGroupRepository.save(snomedRelatedGroup);
            orden = orden + 1;
        }
        return orden;
    }

    private Integer saveSnomedGroup(String eclKey) {
        LocalDate now = dateTimeProvider.nowDate();
        String ecl = snomedSemantics.getEcl(SnomedECL.map(eclKey));
        Integer snomedGroupId = snomedGroupRepository.getIdByEcl(ecl);
        Integer customId = 1;

        SnomedGroup toSave = new SnomedGroup(snomedGroupId, eclKey, ecl, customId, now);
        return snomedGroupRepository.save(toSave).getId();
    }

}
