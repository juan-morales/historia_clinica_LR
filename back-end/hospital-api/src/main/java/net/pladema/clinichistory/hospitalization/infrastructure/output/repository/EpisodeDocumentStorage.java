package net.pladema.clinichistory.hospitalization.infrastructure.output.repository;

import net.pladema.clinichistory.hospitalization.infrastructure.output.entities.EpisodeDocument;
import net.pladema.clinichistory.hospitalization.service.domain.EpisodeDocumentBo;
import net.pladema.clinichistory.hospitalization.service.domain.EpisodeDocumentResponseBo;

public interface EpisodeDocumentStorage {

	EpisodeDocumentResponseBo saveEpisodeDocument(EpisodeDocumentBo bo);
}
