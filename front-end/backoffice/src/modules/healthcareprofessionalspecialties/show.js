import React from 'react';
import {
    Show,
    SimpleShowLayout,
    ReferenceField,
    FunctionField,
    TextField,
} from 'react-admin';

const renderPerson = (choice) => `${choice.identificationNumber} ${choice.lastName} ${choice.firstName}`;
const HealthcareProfessionalSpecialtyShow = props => (
    <Show {...props}>
        <SimpleShowLayout>
            <ReferenceField label="resources.healthcareprofessionalspecialties.fields.personId"
                source="professionalProfessionId" reference="professionalprofessions" link={false}>
                <ReferenceField source="personId" reference="person">
                    <FunctionField render={renderPerson}/>
                </ReferenceField>
            </ReferenceField>
            <ReferenceField label="resources.healthcareprofessionalspecialties.fields.professionalSpecialtyId"
                source="professionalProfessionId" reference="professionalprofessions" link={false}>
                <ReferenceField source="professionalSpecialtyId" reference="professionalspecialties" link={false}>
                    <TextField source="description" />
                </ReferenceField>
            </ReferenceField>
            <ReferenceField source="clinicalSpecialtyId" reference="clinicalspecialties">
                <TextField source="name" />
            </ReferenceField>
        </SimpleShowLayout>
    </Show>
);

export default HealthcareProfessionalSpecialtyShow;
