package net.pladema.internation.service.ips.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.internation.repository.ips.generalstate.DocumentObservationsVo;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DocumentObservationsBo implements Serializable {

    private String otherNote;

    private String physicalExamNote;

    private String studiesSummaryNote;

    private String evolutionNote;

    private String clinicalImpressionNote;

    private String currentIllnessNote;

    private String indicationsNote;

    public DocumentObservationsBo(DocumentObservationsVo source) {
        this.otherNote = source.getOtherNote();
        this.physicalExamNote = source.getPhysicalExamNote();
        this.studiesSummaryNote = source.getStudiesSummaryNote();
        this.evolutionNote = source.getEvolutionNote();
        this.clinicalImpressionNote = source.getClinicalImpressionNote();
        this.currentIllnessNote = source.getCurrentIllnessNote();
        this.indicationsNote = source.getIndicationsNote();
    }
}
