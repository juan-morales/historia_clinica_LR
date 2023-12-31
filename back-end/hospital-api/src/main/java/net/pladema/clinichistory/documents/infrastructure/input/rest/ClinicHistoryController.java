package net.pladema.clinichistory.documents.infrastructure.input.rest;

import ar.lamansys.sgh.shared.infrastructure.input.service.SharedHospitalUserPort;
import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;

import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.featureflags.application.FeatureFlagsService;

import ar.lamansys.sgx.shared.filestorage.infrastructure.input.rest.StoredFileBo;
import ar.lamansys.sgx.shared.filestorage.infrastructure.input.rest.StoredFileResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lowagie.text.DocumentException;

import io.swagger.v3.oas.annotations.tags.Tag;

import net.pladema.clinichistory.documents.application.DownloadClinicHistoryDocuments.DownloadClinicHistoryDocuments;
import net.pladema.clinichistory.documents.application.getPatientClinicHistory.GetPatientClinicHistory;

import net.pladema.clinichistory.documents.application.getPatientClinicHistoryLastDownload.GetPatientClinicHistoryLastDownload;
import net.pladema.clinichistory.documents.domain.CHSearchFilterBo;
import net.pladema.clinichistory.documents.domain.HistoricClinicHistoryDownloadBo;
import net.pladema.clinichistory.documents.infrastructure.input.rest.dto.CHDocumentSummaryDto;
import net.pladema.clinichistory.documents.infrastructure.input.rest.dto.CHSearchFilterDto;
import net.pladema.clinichistory.documents.infrastructure.input.rest.dto.HistoricClinicHistoryDownloadDto;
import net.pladema.clinichistory.documents.infrastructure.input.rest.mapper.ClinicHistoryMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Clinic History", description = "Clinic History")
@RequestMapping("/institutions/{institutionId}/clinic-history")
public class ClinicHistoryController {

	private static final Logger LOG = LoggerFactory.getLogger(ClinicHistoryController.class);
	private static final String OUTPUT = "Output -> {}";

	private final GetPatientClinicHistory getPatientClinicHistory;

	private final DownloadClinicHistoryDocuments downloadClinicHistoryDocuments;

	private final GetPatientClinicHistoryLastDownload getPatientClinicHistoryLastDownload;

	private final LocalDateMapper localDateMapper;

	private final ClinicHistoryMapper clinicHistoryMapper;

	private final ObjectMapper jackson;

	private final SharedHospitalUserPort sharedHospitalUserPort;

	private final FeatureFlagsService featureFlagsService;



	public ClinicHistoryController(GetPatientClinicHistory getPatientClinicHistory,
								   DownloadClinicHistoryDocuments downloadClinicHistoryDocuments,
								   GetPatientClinicHistoryLastDownload getPatientClinicHistoryLastDownload,
								   LocalDateMapper localDateMapper,
								   ClinicHistoryMapper clinicHistoryMapper,
								   ObjectMapper jackson,
								   SharedHospitalUserPort sharedHospitalUserPort,
								   FeatureFlagsService featureFlagsService)
	{
		this.getPatientClinicHistory = getPatientClinicHistory;
		this.downloadClinicHistoryDocuments = downloadClinicHistoryDocuments;
		this.getPatientClinicHistoryLastDownload = getPatientClinicHistoryLastDownload;
		this.localDateMapper = localDateMapper;
		this.clinicHistoryMapper = clinicHistoryMapper;
		this.jackson = jackson;
		this.sharedHospitalUserPort = sharedHospitalUserPort;
		this.featureFlagsService = featureFlagsService;
	}

	@GetMapping(value = "/{patientId}")
	@PreAuthorize("hasPermission(#institutionId, 'PERSONAL_DE_LEGALES')")
	public ResponseEntity<List<CHDocumentSummaryDto>> getPatientClinicHistory(@PathVariable(name="institutionId") Integer institutionId,
																		@PathVariable(name="patientId") Integer patientId,
																	   @RequestParam(name="startDate") String startDate,
																	   @RequestParam(name="endDate") String endDate,
																	   @RequestParam String searchFilterStr
	){
		LOG.debug("Input parameters -> patientId {}, startDate {}, endDate {}, searchFilterSrt{}", patientId, startDate, endDate, searchFilterStr);
		CHSearchFilterBo filter = mapFilter(searchFilterStr);
		List<CHDocumentSummaryDto> result = getPatientClinicHistory.run(patientId, localDateMapper.fromStringToLocalDate(startDate), localDateMapper.fromStringToLocalDate(endDate), filter)
				.stream()
				.map(clinicHistoryMapper::toDocumentSummaryDto)
				.collect(Collectors.toList());
		LOG.debug(OUTPUT, result.toString());
		return ResponseEntity.ok(result);
	}

	@GetMapping(value = "/download")
	@PreAuthorize("hasPermission(#institutionId, 'PERSONAL_DE_LEGALES')")
	public ResponseEntity<Resource> downloadClinicHistoryDocuments (@PathVariable(name = "institutionId") Integer institutionId,
														   @RequestParam(name = "ids") List<Long> ids) throws IOException, DocumentException {
		LOG.debug("Input parameters -> ids{}, institutionId{}", ids, institutionId);
		StoredFileBo result = downloadClinicHistoryDocuments.run(ids, institutionId);
		if (result!=null){
			return StoredFileResponse.sendFile(result);
		}
		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/{patientId}/lastDownload")
	@PreAuthorize("hasPermission(#institutionId, 'PERSONAL_DE_LEGALES')")
	public ResponseEntity<HistoricClinicHistoryDownloadDto> getPatientClinicHistoryLastDownload(@PathVariable(name = "institutionId") Integer institutionId,
																									   @PathVariable(name = "patientId") Integer patientId){
		LOG.debug("Input parameters -> insitutionId {}, patientId {}", institutionId, patientId);
		var result = getPatientClinicHistoryLastDownload.run(patientId, institutionId);
		return ResponseEntity.ok().body(this.mapToHistoricClinicHistoryDownloadDto(result));
	}

	private CHSearchFilterBo mapFilter(String searchFilterStr) {
		try {
			if(searchFilterStr != null && !searchFilterStr.equals("undefined") && !searchFilterStr.equals("null")) {
				CHSearchFilterDto filterDto = jackson.readValue(searchFilterStr, CHSearchFilterDto.class);
				return new CHSearchFilterBo(filterDto.getEncounterTypeList(), filterDto.getDocumentTypeList());
			}
		}
		catch(IOException e){
			LOG.error(String.format("Error mapping filter: %s", searchFilterStr), e);
		}
		return new CHSearchFilterBo();
	}

	private HistoricClinicHistoryDownloadDto mapToHistoricClinicHistoryDownloadDto(HistoricClinicHistoryDownloadBo bo){
		HistoricClinicHistoryDownloadDto result = new HistoricClinicHistoryDownloadDto();
		if(bo.getUserId() != null) {
			var userInfo = sharedHospitalUserPort.getUserCompleteInfo(bo.getUserId());
			String userFullName = (featureFlagsService.isOn(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS) && userInfo.getNameSelfDetermination() != null ? userInfo.getNameSelfDetermination() : userInfo.getFirstName()) + ' ' + userInfo.getLastName();
			result.setUser(userFullName);
		}
		result.setId(bo.getId());
		result.setInstitutionId(bo.getInstitutionId());
		result.setPatientId(bo.getPatientId());
		result.setDownloadDate(localDateMapper.toDateTimeDto(bo.getDownloadDate()));
		return result;
	}

}
