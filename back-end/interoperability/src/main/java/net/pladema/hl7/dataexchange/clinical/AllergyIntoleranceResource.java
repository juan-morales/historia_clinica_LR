package net.pladema.hl7.dataexchange.clinical;

import net.pladema.hl7.dataexchange.IMultipleResourceFhir;
import net.pladema.hl7.dataexchange.model.adaptor.FhirDateMapper;
import net.pladema.hl7.dataexchange.model.adaptor.FhirID;
import net.pladema.hl7.supporting.conformance.InteroperabilityCondition;
import net.pladema.hl7.supporting.exchange.database.FhirPersistentStore;
import net.pladema.hl7.supporting.terminology.coding.CodingCode;
import net.pladema.hl7.supporting.terminology.coding.CodingProfile;
import net.pladema.hl7.supporting.terminology.coding.CodingSystem;
import net.pladema.hl7.dataexchange.model.domain.AllergyIntoleranceVo;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Conditional(InteroperabilityCondition.class)
public class AllergyIntoleranceResource extends IMultipleResourceFhir {

    @Autowired
    public AllergyIntoleranceResource(FhirPersistentStore store) {
        super(store);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.AllergyIntolerance;
    }


    @Override
    public List<AllergyIntolerance> fetch(String id, Reference[] references) {
        List<AllergyIntoleranceVo> allergies = store.findAllAllergies(id);

        if (allergies.isEmpty())
            return noInformationAvailable(references[0]);

        List<AllergyIntolerance> resources = new ArrayList<>();
        allergies.forEach((allergy) -> {

            AllergyIntolerance resource = new AllergyIntolerance();
            resource.setId(allergy.getId());
            resource.setType(AllergyIntolerance.AllergyIntoleranceType.fromCode(allergy.getType()));
            allergy.getCategories().forEach(c-> resource.addCategory(
                        AllergyIntolerance.AllergyIntoleranceCategory.fromCode(c))
            );
            resource.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.fromCode(allergy.getCriticality()));
            resource.setPatient(references[0]);
            resource.setCode(newCodeableConcept(CodingSystem.SNOMED, allergy.get()));
            resource.getOnsetDateTimeType().setValue(FhirDateMapper.toDate(allergy.getStartDate()));

            resource.setClinicalStatus(newCodeableConcept(
                    CodingSystem.Allergy.CLINICAL_STATUS,
                    allergy.getClinicalStatus())
            );
            resource.setVerificationStatus(newCodeableConcept(
                    CodingSystem.Allergy.VERIFICATION_STATUS,
                    allergy.getVerificationStatus())
            );

            resource.setMeta(newMeta(CodingProfile.Allergy.URL));
            resources.add(resource);
        });
        return resources;
    }

    private List<AllergyIntolerance> noInformationAvailable(Reference patientRef){
        AllergyIntolerance none = new AllergyIntolerance();
        none.setId(FhirID.autoGenerated());
        none.setPatient(patientRef);
        none.setCode(newCodeableConcept(CodingSystem.NODATA, CodingCode.Allergy.KNOWN_ABSENT));
        none.setClinicalStatus(newCodeableConcept(
                CodingSystem.Allergy.CLINICAL_STATUS,
                AllergyIntoleranceVo.defaultClinicalStatus())
        );
        none.addCategory(AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION);
        return Collections.singletonList(none);
    }

    public static AllergyIntoleranceVo encode(Resource baseResource) {
        AllergyIntoleranceVo data = new AllergyIntoleranceVo();
        AllergyIntolerance resource = (AllergyIntolerance) baseResource;

        data.setId(resource.getId());
        if(resource.hasCategory()){
            AllergyIntolerance.AllergyIntoleranceCategory category = resource
                    .getCategory().get(0).getValue();
            data.setCategories(Collections.singleton(category.getDisplay()));
        }

        if(resource.hasClinicalStatus())
            data.setClinicalStatus(decodeCode(resource.getClinicalStatus()));
        if(resource.hasVerificationStatus())
            data.setVerificationStatus(decodeCode(resource.getVerificationStatus()));
        if(resource.hasCriticality())
            data.setCriticality(resource.getCriticality().getDisplay());

        if(resource.hasCode()) {
            Pair<String, String> coding = decodeCoding(resource.getCode());
            data.setSctidCode(coding.getKey());
            data.setSctidTerm(coding.getValue());
        }
        if(resource.hasType())
            data.setType(resource.getType().getDisplay());
        if(resource.hasOnsetDateTimeType())
            data.setStartDate(FhirDateMapper.toLocalDate(resource.getOnsetDateTimeType().getValue()));
        return data;
    }
}
