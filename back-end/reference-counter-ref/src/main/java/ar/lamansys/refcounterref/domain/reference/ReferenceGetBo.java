package ar.lamansys.refcounterref.domain.reference;

import ar.lamansys.refcounterref.domain.careline.CareLineBo;
import ar.lamansys.refcounterref.domain.clinicalspecialty.ClinicalSpecialtyBo;
import ar.lamansys.refcounterref.domain.professionalperson.ProfessionalPersonBo;
import ar.lamansys.refcounterref.domain.referencenote.ReferenceNoteBo;
import ar.lamansys.refcounterref.domain.referenceproblem.ReferenceProblemBo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ReferenceGetBo implements Serializable {

    private Integer id;

    private LocalDate referenceDate;

    private ReferenceNoteBo note;

    private CareLineBo careLine;

    private ClinicalSpecialtyBo clinicalSpecialty;

    private ProfessionalPersonBo professional;

    private List<ReferenceProblemBo> problems;

    public ReferenceGetBo(Integer id, LocalDate referenceDate, Integer referenceNoteId,
                          String referenceDescription, Integer careLineId, String careLineDescription,
                          Integer clinicalSpecialtyid, String clinicalSpecialtyName,
                          Integer professionalId, String professionalFirstName, String professionalLastName) {
        this.id = id;
        this.referenceDate = referenceDate;
        this.note = new ReferenceNoteBo(referenceNoteId, referenceDescription);
        this.careLine = new CareLineBo(careLineId, careLineDescription);
        this.clinicalSpecialty = new ClinicalSpecialtyBo(clinicalSpecialtyid, clinicalSpecialtyName);
        this.professional = new ProfessionalPersonBo(professionalId, professionalFirstName, professionalLastName);
    }

}
