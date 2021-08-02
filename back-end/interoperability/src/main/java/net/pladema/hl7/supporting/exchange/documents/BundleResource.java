//
//FHIR Documents.
//FHIR resources can be used to build documents that represent a composition: a coherent set
//of information that is a statement of healthcare information. A document is an immutable
//set of resources with a fixed presentation that is authored and/or attested by humans,
//organizations and devices.
//FHIR documents are for documents that are authored and assembled in FHIR, while the
// document reference resource is for general references to pre-existing documents.
//

package net.pladema.hl7.supporting.exchange.documents;

import ca.uhn.fhir.rest.param.TokenParam;

import net.pladema.hl7.concept.administration.OrganizationResource;
import net.pladema.hl7.concept.administration.PatientResource;
import net.pladema.hl7.dataexchange.IResourceFhir;
import net.pladema.hl7.dataexchange.clinical.AllergyIntoleranceResource;
import net.pladema.hl7.dataexchange.clinical.ConditionResource;
import net.pladema.hl7.dataexchange.medications.ImmunizationResource;
import net.pladema.hl7.dataexchange.medications.MedicationStatementResource;
import net.pladema.hl7.dataexchange.model.adaptor.FhirID;
import net.pladema.hl7.dataexchange.model.domain.BundleVo;
import net.pladema.hl7.supporting.conformance.InteroperabilityCondition;
import net.pladema.hl7.supporting.exchange.database.FhirPersistentStore;
import net.pladema.hl7.dataexchange.model.domain.PatientSummaryVo;
import net.pladema.hl7.supporting.exchange.documents.profile.FhirDocument;
import net.pladema.hl7.supporting.exchange.documents.ips.PatientSummaryDocument;
import net.pladema.hl7.supporting.exchange.restful.validator.DocumentReferenceValidation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Conditional(InteroperabilityCondition.class)
public class BundleResource extends IResourceFhir {

    private final DocumentReferenceResource documentReferenceResource;
    private final DocumentReferenceValidation documentReferenceValidation;
    private final FhirDocument fhirDocument;

    @Value("${app.default.language}")
    private String language;

    @Autowired
    public BundleResource(FhirPersistentStore store,
                          DocumentReferenceResource documentReferenceResource,
                          DocumentReferenceValidation documentReferenceValidation,
                          FhirDocument fhirDocument) {
        super(store);
        this.documentReferenceResource = documentReferenceResource;
        this.documentReferenceValidation=documentReferenceValidation;
        this.fhirDocument=fhirDocument;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Bundle;
    }

    public Bundle getExistingDocumentsReferences (TokenParam subject,
                                                  TokenParam custodian, TokenParam type) {
        //Input parameters required validation
        return documentReferenceValidation.inputParameter(subject, custodian, type, getDominio()).orElseGet(
                //returns a default value directly if the Optional is empty (the data of document is valid)
                () -> {
                    BundleVo data = documentReferenceResource.getData(subject.getValue());
                    //Data required validation
                    return documentReferenceValidation.data(data, subject.getValue()).orElseGet(
                            () -> { Bundle validDocument = empty();
                                validDocument.addEntry(documentReferenceResource.fetchEntry(data));
                                validDocument.setTotal(validDocument.getEntry().size());
                                return validDocument;
                            });
                }
        );
    }

    public Bundle assembleDocument(IdType id) {
        //TODO should be replaced by database real search
        Coding code = PatientSummaryDocument.TYPE;

        Bundle resource = new Bundle();
        resource.setId(FhirID.autoGenerated());
        resource.setIdentifier(newIdentifier(resource));
        resource.setMeta(new Meta().setLastUpdated(new Date()));
        resource.setType(Bundle.BundleType.DOCUMENT);
        resource.setTimestamp(new Date());
        resource.setLanguage(language);

        //=======================Content=======================
        fhirDocument.get(code).ifPresent(document ->
                resource.setEntry(document.getContent(id.getIdPart()))
        );
        return resource;
    }

    public static Bundle empty(){
        Bundle document = new Bundle();
        document.setId(FhirID.autoGenerated());
        document.setType(Bundle.BundleType.SEARCHSET);
        document.setMeta(new Meta().setLastUpdated(new Date()));
        document.setTotal(0);
        return document;
    }

    public PatientSummaryVo encodeResourceToSummary(Bundle resource) {
        PatientSummaryVo summary = new PatientSummaryVo();
        List<Bundle.BundleEntryComponent> entries = resource.getEntry();
        entries.forEach(entry -> {
            switch(entry.getResource().getResourceType()){
                case Patient:
                    summary.setPatient(PatientResource.encode(entry.getResource()));
                    break;
                case Condition:
                    summary.addCondition(ConditionResource.encode(entry.getResource()));
                    break;
                case MedicationStatement:
                    Optional<Resource> medication = getMedication(entry.getResource(), entries);
                    summary.addMedication(MedicationStatementResource.encode(entry.getResource(), medication));
                    break;
                case Immunization:
                    summary.addImmunization(ImmunizationResource.encode(entry.getResource()));
                    break;
                case AllergyIntolerance:
                    summary.addAllergy(AllergyIntoleranceResource.encode(entry.getResource()));
                    break;
                case Organization:
                    summary.setOrganization(OrganizationResource.encode(entry.getResource()));
                    break;
                default:
            }
        });
        return summary;
    }

    private Optional<Resource> getMedication(Resource resource,
                                               List<Bundle.BundleEntryComponent> entries) {
        if( ((MedicationStatement) resource).hasMedicationReference()){
            String reference = ((MedicationStatement) resource).getMedicationReference().getReference();
            return entries.stream()
                    .filter(r -> r.getResource().getId().equals(reference))
                    .map(Bundle.BundleEntryComponent::getResource)
                    .findFirst();
        }
        return Optional.empty();
    }
}
