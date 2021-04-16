package net.pladema.odontology.infrastructure.repository.tooth;

import net.pladema.odontology.application.plugin.exception.ToothServiceException;
import net.pladema.odontology.domain.TeethBo;
import net.pladema.odontology.domain.ToothStorage;
import org.springframework.stereotype.Service;

@Service
public class ToothStorageImpl implements ToothStorage {

    private final ToothRepository toothRepository;

    public ToothStorageImpl(ToothRepository toothRepository) {
        this.toothRepository = toothRepository;
    }

    @Override
    public TeethBo getTeeth() throws ToothServiceException {
        String teeth;
        try {
            teeth = toothRepository.getTeeth();
        } catch (Exception e) {
            throw new ToothServiceException("Fallo algo");
        }
        return new TeethBo(teeth);
    }
}
