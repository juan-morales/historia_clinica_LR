package ar.lamansys.refcounterref.application.getreference;

import ar.lamansys.refcounterref.application.getreference.exceptions.ReferenceException;
import ar.lamansys.refcounterref.application.getreference.exceptions.ReferenceExceptionEnum;
import ar.lamansys.refcounterref.application.port.ReferenceStorage;
import ar.lamansys.refcounterref.domain.reference.ReferenceGetBo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetReference {

    private final ReferenceStorage referenceStorage;

    @Transactional
    public List<ReferenceGetBo> run(Integer institutionId, Integer patientId, List<Integer> clinicalSpecialtyIds) {
        log.debug("Input parameters -> institutionId {}, patientId {}, clinicalSpecialtyIds {} ", institutionId, patientId, clinicalSpecialtyIds);
        assertContextValid(institutionId, patientId, clinicalSpecialtyIds);
        List<ReferenceGetBo> result = referenceStorage.getReferences(institutionId, patientId, clinicalSpecialtyIds);
        return result;
    }

    private void assertContextValid(Integer institutionId, Integer patientId, List<Integer> clinicalSpecialtyIds) {
        if (institutionId == null)
            throw new ReferenceException(ReferenceExceptionEnum.NULL_INSTITUTION_ID, "El id de la institución es obligatorio");
        if (patientId == null)
            throw new ReferenceException(ReferenceExceptionEnum.NULL_PATIENT_ID, "El id del paciente es obligatorio");
        if (clinicalSpecialtyIds.isEmpty())
            throw new ReferenceException(ReferenceExceptionEnum.NULL_CLINICAL_SPECIALTY_ID, "Es obligatorio al menos un id de especialidad");
    }

}
