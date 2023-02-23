package ar.lamansys.sgh.publicapi.infrastructure.input.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.lamansys.sgh.publicapi.application.fetchactivitiesbyfilter.ActivitySearchFilter;
import ar.lamansys.sgh.publicapi.application.fetchactivitiesbyfilter.FetchActivitiesByFilter;
import ar.lamansys.sgh.publicapi.infrastructure.input.rest.dto.AttentionInfoDto;
import ar.lamansys.sgh.publicapi.infrastructure.input.rest.mapper.ActivitiesMapper;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/public-api")
@Tag(name = "Public Api", description = "Public Api Activities")
public class ActivitiesController {

	private static final Logger LOG = LoggerFactory.getLogger(ActivitiesController.class);
	private static final String OUTPUT = "Output -> {}";
	private static final String INPUT = "Input data -> ";

	private final ActivitiesMapper activitiesMapper;
	private final FetchActivitiesByFilter fetchActivitiesByFilter;
	private final LocalDateMapper localDateMapper;

	public ActivitiesController(ActivitiesMapper activitiesMapper, FetchActivitiesByFilter fetchActivitiesByFilter, LocalDateMapper localDateMapper) {
		this.activitiesMapper = activitiesMapper;
		this.fetchActivitiesByFilter = fetchActivitiesByFilter;
		this.localDateMapper = localDateMapper;
	}

	@GetMapping("/institution/refset/{refsetCode}/activities")
	public ResponseEntity<List<AttentionInfoDto>> getActivitiesByInstitution(
			@PathVariable("refsetCode") String refsetCode,
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("reprocessing") Boolean reprocessing
	) {
		LOG.debug(INPUT + "refsetCode {}, from {}, to {}, reprocessing {}", refsetCode, from, to, reprocessing);

		ActivitySearchFilter activitySearchFilter = new ActivitySearchFilter(
				refsetCode, null, localDateMapper.fromStringToLocalDate(from), localDateMapper.fromStringToLocalDate(to), reprocessing, null);
		var resultBo = activitiesMapper.mapTo(fetchActivitiesByFilter.run(activitySearchFilter));
		List<AttentionInfoDto> result = activitiesMapper.groupDiagnosis(resultBo);
		LOG.debug(OUTPUT, resultBo);

		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/institution/refset/{refsetCode}/patient/{identificationNumber}/activities")
	public ResponseEntity<List<AttentionInfoDto>> getActivitiesByInstitutionAndPatient(
			@PathVariable("refsetCode") String refsetCode,
			@PathVariable("identificationNumber") String identificationNumber,
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("reprocessing") Boolean reprocessing
	) {
		LOG.debug(INPUT + "refsetCode {}, identificationNumber{}, from {}, to {}, reprocessing {}",
				refsetCode, identificationNumber, from, to, reprocessing);

		ActivitySearchFilter activitySearchFilter = new ActivitySearchFilter(
				refsetCode, identificationNumber, localDateMapper.fromStringToLocalDate(from), localDateMapper.fromStringToLocalDate(to), reprocessing, null);
		var resultBo = activitiesMapper.mapTo(fetchActivitiesByFilter.run(activitySearchFilter));
		List<AttentionInfoDto> result = activitiesMapper.groupDiagnosis(resultBo);

		LOG.debug(OUTPUT, result);

		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/institution/refset/{refsetCode}/coverage/{coverageCuit}/activities")
	public ResponseEntity<List<AttentionInfoDto>> getActivitiesByInstitutionAndCoverage(
			@PathVariable("refsetCode") String refsetCode,
			@PathVariable("coverageCuit") String coverageCuit,
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("reprocessing") Boolean reprocessing
	) {
		LOG.debug(INPUT + "refsetCode {}, coverageCuit{}, from {}, to {}, reprocessing {}",
				refsetCode, coverageCuit, from, to, reprocessing);

		ActivitySearchFilter activitySearchFilter = new ActivitySearchFilter(
				refsetCode, null, localDateMapper.fromStringToLocalDate(from), localDateMapper.fromStringToLocalDate(to), reprocessing, coverageCuit);
		var resultBo = activitiesMapper.mapTo(fetchActivitiesByFilter.run(activitySearchFilter));
		List<AttentionInfoDto> result = activitiesMapper.groupDiagnosis(resultBo);
		LOG.debug(OUTPUT, result);
		return ResponseEntity.ok().body(result);
	}
}