import React from 'react';
import {
    Show,
    SimpleShowLayout,
    ReferenceField,
    TextField,
    BooleanField,
    FunctionField,
    ReferenceManyField,
    Datagrid,
    DeleteButton,
} from 'react-admin';

import CreateRelatedButton from '../components/CreateRelatedButton';
import SectionTitle from '../components/SectionTitle';

const renderPerson = (choice) => `${choice.identificationNumber} ${choice.lastName} ${choice.firstName}`;
const HealthcareProfessionalShow = props => (
    <Show {...props}>
        <SimpleShowLayout>
            <TextField source="licenseNumber" />
            <ReferenceField source="personId" reference="people">
                <FunctionField render={renderPerson}/>
            </ReferenceField>
            <BooleanField source="isMedicalDoctor" />

            <SectionTitle label="resources.healthcareprofessionals.fields.healthcareprofessionalspecialties"/>
            <CreateRelatedButton
                reference="healthcareprofessionalspecialties"
                refFieldName="healthcareProfessionalId"
                label="resources.healthcareprofessionalspecialties.createRelated"
            />
            {/*TODO: Aislar esto en un componente. También se usa en show.js*/}
            <ReferenceManyField
                addLabel={false}
                reference="healthcareprofessionalspecialties"
                target="healthcareProfessionalId"
                sort={{ field: 'description', order: 'DESC' }}
            >
                <Datagrid rowClick="show">
                    <TextField source="description" />
                    <DeleteButton />
                </Datagrid>
            </ReferenceManyField>

        </SimpleShowLayout>
    </Show>
);

export default HealthcareProfessionalShow;
