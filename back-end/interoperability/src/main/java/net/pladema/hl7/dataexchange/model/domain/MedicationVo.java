package net.pladema.hl7.dataexchange.model.domain;

import lombok.Getter;
import lombok.Setter;
import net.pladema.hl7.dataexchange.model.adaptor.FhirCode;
import net.pladema.hl7.foundation.lifecycle.ResourceStatus;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MedicationVo implements Serializable {

    private final BidiMap<Short, String> UNIT_TIME;

    public MedicationVo(){
        //Todo: add to DB
        UNIT_TIME = new DualHashBidiMap<>();
        UNIT_TIME.put((short)1, "s");
        UNIT_TIME.put((short)2, "min");
        UNIT_TIME.put((short)3, "h");
        UNIT_TIME.put((short)4, "d");
        UNIT_TIME.put((short)5, "wk");
        UNIT_TIME.put((short)6, "mo");
        UNIT_TIME.put((short)7, "a");
    }

    //====================Medication=====================
    private String id;

    //Medication info
    private String sctidCode;
    private String sctidTerm;

    //presentation of the medicinal product
    private String formCode;
    private String formTerm;

    private List<MedicationIngredientVo> ingredients = new ArrayList<>();

    //=====================Statement=====================
    private String statementId;

    //route administration
    private String routeCode;
    private String routeTerm;

    private String status;

    //Dosage timing unit time
    private Short unitTime;

    //Dose Quantity
    private BigDecimal doseQuantityValue;
    private String doseQuantityCode;
    private String doseQuantityUnit;

    private LocalDate effectiveTime;

    public FhirCode get() {
        return new FhirCode(sctidCode, sctidTerm);
    }

    public FhirCode getForm() {
        return new FhirCode(formCode, formTerm);
    }

    public String getStatus(){
        return ResourceStatus.getStatus(status);
    }

    public String getUnitTime(){
        return UNIT_TIME.get(unitTime);
    }

    public FhirCode getRoute(){
        return new FhirCode(routeCode, routeTerm);
    }

    public String getDoseQuantityData(){
        return doseQuantityCode + " " + doseQuantityUnit + " " + doseQuantityValue;
    }

    public void setUnitTime(String unitTime){
        this.unitTime = UNIT_TIME.getKey(unitTime);
    }
}
