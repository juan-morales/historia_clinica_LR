package ar.lamansys.odontology.infrastructure.repository.consultation;

import ar.lamansys.odontology.domain.consultation.ToothIndicesStorage;
import ar.lamansys.odontology.domain.consultation.cpoCeoIndices.ToothIndicesBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToothIndicesStorageImpl implements ToothIndicesStorage {

    private static final Logger LOG = LoggerFactory.getLogger(ToothIndicesStorageImpl.class);

    private final ToothIndicesRepository toothIndicesRepository;

    public ToothIndicesStorageImpl(ToothIndicesRepository toothIndicesRepository) {
        this.toothIndicesRepository = toothIndicesRepository;
    }

    @Override
    public void save(Integer patientId, List<ToothIndicesBo> teethIndices) {
        LOG.debug("Input parameters -> patientId {}, teethIndices {}", patientId, teethIndices);
        toothIndicesRepository.saveAll(teethIndices.stream().map(t -> new ToothIndices(patientId, t)).collect(Collectors.toList()));
    }

}
