package net.pladema.snowstorm.services.loadCsv;

import ar.lamansys.sgh.shared.infrastructure.input.service.SharedSnomedDto;
import ar.lamansys.sgh.shared.infrastructure.input.service.SharedSnomedPort;
import ar.lamansys.sgx.shared.dates.configuration.DateTimeProvider;
import ar.lamansys.sgx.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.pladema.snowstorm.repository.SnomedCacheLogRepository;
import net.pladema.snowstorm.repository.SnomedGroupRepository;
import net.pladema.snowstorm.repository.SnomedRelatedGroupRepository;
import net.pladema.snowstorm.repository.entity.SnomedCacheLog;
import net.pladema.snowstorm.repository.entity.SnomedRelatedGroup;
import net.pladema.snowstorm.services.domain.semantics.SnomedECL;
import net.pladema.snowstorm.services.domain.semantics.SnomedSemantics;
import net.pladema.snowstorm.services.loadCsv.exceptions.UpdateSnomedConceptsException;
import net.pladema.snowstorm.services.loadCsv.exceptions.UpdateSnomedConceptsExceptionEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateSnomedConceptsSynonymsByCsv {

	@Value("${snomed-cache-update.batch-size:1000}")
	private int batchMaxSize;
	private static final int BATCH_SIZE_DIVIDER = 10;
	private static final int BATCH_SIZE_MULTIPLIER = 10;
	private final SnomedRelatedGroupRepository snomedRelatedGroupRepository;
	private final SnomedSemantics snomedSemantics;
	private final SnomedGroupRepository snomedGroupRepository;
	private final DateTimeProvider dateTimeProvider;
	private final SharedSnomedPort sharedSnomedPort;
	private final SnomedCacheLogRepository snomedCacheLogRepository;

	private final Semaphore semaphore = new Semaphore(1);

	public UpdateConceptsSynonymsResultBo run(MultipartFile csvFile, String eclKey){
		log.debug("Update SnomedConceptsSynonyms By CSV File");
		if (!this.semaphore.tryAcquire()) {
			log.error("There is another Snomed update in progress");
			throw new UpdateSnomedConceptsException(UpdateSnomedConceptsExceptionEnum.UPDATE_ALREADY_IN_PROGRESS,
					"Hay otra actualización en marcha. Intente nuevamente más tarde");
		}
		UpdateConceptsSynonymsResultBo result = updateSnomedConceptSynonyms(csvFile, eclKey);
		this.semaphore.release();
		return result;
	}

	private UpdateConceptsSynonymsResultBo updateSnomedConceptSynonyms(MultipartFile csvFile, String eclKey){
		Integer conceptsProcessed = 0;
		Integer erroneousConcepts = 0;
		Integer missingMainConcepts = 0;
		List<String> errorMessages = new ArrayList<>();
		Integer batchSize = batchMaxSize;
		Integer totalConcepts = SnomedConceptsCsvReader.getTotalRecords(csvFile);
		LocalDate today = dateTimeProvider.nowDate();
		Integer snomedGroupId = getSnomedGroupId(eclKey, today);
		List<SnomedConceptBo> conceptBatch = null;

		while (conceptsProcessed < totalConcepts) {
			try {
				conceptBatch = getNextBatch(batchSize, conceptsProcessed, totalConcepts, csvFile);
				conceptsProcessed = saveConcepts(conceptsProcessed, today, snomedGroupId, conceptBatch, missingMainConcepts);
				// if the batch size had decreased before due to an error, it will increase again
				batchSize = Math.min(batchSize * BATCH_SIZE_MULTIPLIER, batchMaxSize);
				log.debug("Concepts processed -> {}", conceptsProcessed);
			} catch (Exception e) {
				// If the batch size is equal to 1, it means that that element is the one that can't be saved.
				// So, it should be skipped to try to save the rest
				if (batchSize.equals(1)) {
					saveError(e, conceptBatch.get(0), errorMessages);
					conceptsProcessed += 1;
					erroneousConcepts += 1;
					batchSize = batchMaxSize;
				} else {
					batchSize = Math.max(batchSize / BATCH_SIZE_DIVIDER, 1);
				}
			}

		}
		UpdateConceptsSynonymsResultBo result = new UpdateConceptsSynonymsResultBo(eclKey,
				conceptsProcessed - erroneousConcepts,
				erroneousConcepts,
				missingMainConcepts,
				errorMessages);
		log.debug("Finished loading snomed concepts");
		log.debug("Output -> {}", result);
		return result;
	}

	private void saveError(Exception e, SnomedConceptBo snomedConceptBo, List<String> errorMessages) {
		String message = String.format("Error saving %s -> %s", snomedConceptBo.toString(), e.getCause().getMessage());
		errorMessages.add(message);
		snomedCacheLogRepository.save(new SnomedCacheLog(message, dateTimeProvider.nowDateTime()));
	}

	private List<SnomedConceptBo> getNextBatch(Integer batchSize, Integer conceptsProcessed, Integer totalConcepts, MultipartFile csvFile) {
		int batchFinishIndex = Math.min(conceptsProcessed + batchSize, totalConcepts);
		return getConcepts(csvFile, conceptsProcessed, batchFinishIndex);
	}

	private List<SnomedConceptBo> getConcepts(MultipartFile csvFile, int start, int end) {
		log.debug("Input parameter -> csvFile {}, start {}, end {}", csvFile.getOriginalFilename(), start, end);
		List<SnomedConceptBo> result = new ArrayList<>();
		if (SnomedConceptsCsvReader.hasCsvFormat(csvFile)) {
			try {
				result = SnomedConceptsCsvReader.csvToSnomedConceptsBo(csvFile.getInputStream(), start, end);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		log.debug("Output size -> {}", result.size());
		return result;
	}

	private Integer getSnomedGroupId(String eclKey, LocalDate date) {
		log.debug("Input parameters -> eclKey {}, date {}", eclKey, date);
		String ecl = "";
		try {
			ecl = snomedSemantics.getEcl(SnomedECL.map(eclKey));
		} catch (NotFoundException e){
			this.semaphore.release();
			throw e;
		}
		Integer result = snomedGroupRepository.getBaseGroupIdByEclAndDescription(ecl, eclKey);
		log.debug("Output -> {}", result);
		return result;
	}

	private Integer saveConcepts(Integer conceptsProcessed, LocalDate today, Integer snomedGroupId, List<SnomedConceptBo> conceptBatch, Integer missingMainConcepts) {
		List<Integer> conceptIds = sharedSnomedPort.addSnomedSynonyms(mapToDto(conceptBatch));
		conceptsProcessed = associateConceptIdsWithSnomedGroup(snomedGroupId, conceptIds, conceptsProcessed, today);
		missingMainConcepts = missingMainConcepts + (conceptBatch.size() - conceptsProcessed);
		return conceptsProcessed;
	}

	private List<SharedSnomedDto> mapToDto(List<SnomedConceptBo> concepts) {
		return concepts.stream()
				.map(c -> new SharedSnomedDto(c.getSctid(), c.getPt(), null, null))
				.collect(Collectors.toList());
	}

	private Integer associateConceptIdsWithSnomedGroup(Integer snomedGroupId, List<Integer> conceptIds, Integer orden, LocalDate date) {
		log.debug("Input parameters -> snomedGroupId {}, conceptIds size = {}, orden {}, date {}",
				snomedGroupId, conceptIds.size(), orden, date);
		List<SnomedRelatedGroup> elementsToSave = new ArrayList<>();
		for (Integer snomedId : conceptIds) {
			SnomedRelatedGroup snomedRelatedGroup = snomedRelatedGroupRepository.getByGroupIdAndSnomedId(snomedGroupId, snomedId)
					.orElse(new SnomedRelatedGroup(snomedId, snomedGroupId));
			snomedRelatedGroup.setLastUpdate(date);
			snomedRelatedGroup.setOrden(orden);
			elementsToSave.add(snomedRelatedGroup);
			orden = orden + 1;
		}
		snomedRelatedGroupRepository.saveAll(elementsToSave);
		log.debug("Output -> {}", orden);
		return orden;
	}

}
