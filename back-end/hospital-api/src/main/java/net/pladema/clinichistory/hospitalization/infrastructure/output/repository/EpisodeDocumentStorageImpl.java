package net.pladema.clinichistory.hospitalization.infrastructure.output.repository;

import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import lombok.extern.slf4j.Slf4j;

import net.pladema.clinichistory.hospitalization.infrastructure.output.entities.EpisodeDocument;

import net.pladema.clinichistory.hospitalization.infrastructure.output.entities.EpisodeDocumentType;
import net.pladema.clinichistory.hospitalization.infrastructure.output.entities.VEpisodeDocument;
import net.pladema.clinichistory.hospitalization.service.domain.DocumentTypeBo;
import net.pladema.clinichistory.hospitalization.service.domain.EpisodeDocumentBo;

import net.pladema.clinichistory.hospitalization.service.domain.EpisodeDocumentResponseBo;

import net.pladema.clinichistory.hospitalization.service.domain.SavedEpisodeDocumentResponseBo;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EpisodeDocumentStorageImpl implements EpisodeDocumentStorage {

	private final SavedEpisodeDocumentRepository savedEpisodeDocumentRepository;

	private final EpisodeDocumentRepository episodeDocumentRepository;

	private final String OUTPUT = "Output -> {}";

	private final LocalDateMapper localDateMapper;

	private final InternmentEpisodeDocumentTypeRepository documentTypeRepository;

	public EpisodeDocumentStorageImpl(SavedEpisodeDocumentRepository savedEpisodeDocumentRepository, EpisodeDocumentRepository episodeDocumentRepository, LocalDateMapper localDateMapper, InternmentEpisodeDocumentTypeRepository documentTypeRepository) {
		this.savedEpisodeDocumentRepository = savedEpisodeDocumentRepository;
		this.episodeDocumentRepository = episodeDocumentRepository;
		this.localDateMapper = localDateMapper;
		this.documentTypeRepository = documentTypeRepository;
	}

	@Override
	public SavedEpisodeDocumentResponseBo saveEpisodeDocument(EpisodeDocumentBo bo) {
		EpisodeDocument ed = new EpisodeDocument(bo.getFilePath(), bo.getFileName(), bo.getUuid(), bo.getEpisodeDocumentTypeId(), bo.getInternmentEpisodeId());
		return this.mapToBo(this.savedEpisodeDocumentRepository.save(ed));
	}

	@Override
	public List<EpisodeDocumentResponseBo> getEpisodeDocuments(Integer internmentEpisodeId) {
		log.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
		List<EpisodeDocumentResponseBo> result = this.episodeDocumentRepository.findAll()
				.stream()
				.map(entity -> this.mapEntityToBo(entity))
				.collect(Collectors.toList());
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public List<DocumentTypeBo> getDocumentTypes() {
		return this.documentTypeRepository.findAll()
				.stream()
				.map(entity -> this.mapDocumentTypeToBo(entity))
				.collect(Collectors.toList());
	}

	private EpisodeDocumentResponseBo mapEntityToBo(VEpisodeDocument entity) {
		return new EpisodeDocumentResponseBo(
				entity.getId(),
				entity.getDescription(),
				entity.getFileName(),
				localDateMapper.toDateDto(entity.getCreatedOn())
		);
	}

	private DocumentTypeBo mapDocumentTypeToBo(EpisodeDocumentType entity) {
		return new DocumentTypeBo(entity.getId(), entity.getDescription());
	}

	SavedEpisodeDocumentResponseBo mapToBo(EpisodeDocument entity) {
		return new SavedEpisodeDocumentResponseBo(
				entity.getId(),
				entity.getFilePath(),
				entity.getFileName(),
				entity.getUuidFile(),
				localDateMapper.toDateDto(entity.getCreatedOn()),
				entity.getEpisodeDocumentTypeId(),
				entity.getInternmentEpisodeId()
		);
	}
}