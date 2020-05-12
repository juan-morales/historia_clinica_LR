import React from 'react';
import {
    Show,
    SimpleShowLayout,
    ReferenceField,
    TextField,
    ReferenceManyField,
    Datagrid,
    DeleteButton
} from 'react-admin';
import CreateRelatedButton from '../components/CreateRelatedButton';
import SectionTitle from '../components/SectionTitle';

const SectorShow = props => (
    <Show {...props}>
        <SimpleShowLayout>
            <TextField source="description" />
            <ReferenceField source="institutionId" reference="institutions">
                <TextField source="name"/>
            </ReferenceField>
            <SectionTitle label="resources.sectors.fields.clinicalspecialtysectors"/>
            <CreateRelatedButton
                reference="clinicalspecialtysectors"
                refFieldName="sectorId"
                label="resources.clinicalspecialtysectors.createRelated"
            />
            <ReferenceManyField
                addLabel={false}
                reference="clinicalspecialtysectors"
                target="sectorId"
                sort={{ field: 'description', order: 'DESC' }}
            >
                <Datagrid rowClick="show">
                    <TextField source="description" />
                    <ReferenceField source="clinicalSpecialtyId" reference="clinicalspecialties">
                        <TextField source="name" />
                    </ReferenceField>
                    <DeleteButton />
                </Datagrid>
            </ReferenceManyField>
        </SimpleShowLayout>
    </Show>
);

export default SectorShow;
