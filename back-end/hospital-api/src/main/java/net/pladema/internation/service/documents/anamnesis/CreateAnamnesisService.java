package net.pladema.internation.service.documents.anamnesis;

import net.pladema.internation.service.documents.anamnesis.domain.AnamnesisBo;

public interface CreateAnamnesisService {

    AnamnesisBo createDocument(Integer intermentEpisodeId, Integer patientId, AnamnesisBo anamnesis);
}
