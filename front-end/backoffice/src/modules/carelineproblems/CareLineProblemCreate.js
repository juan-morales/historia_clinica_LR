import React from 'react';
import {
    ReferenceInput,
    AutocompleteInput,
    Create,
    SimpleForm,
    required
} from 'react-admin';
import CustomToolbar from '../components/CustomToolbar';

const redirect = (basePath, id, data) => `/carelines/${data.careLineId}/show`;

const CareLineProblemCreate = props => (
    <Create {...props}>
        <SimpleForm redirect={redirect} toolbar={<CustomToolbar />}>
            <ReferenceInput
                source="careLineId"
                reference="carelines"
                label="resources.carelineproblems.fields.careLineId"
            >
                <AutocompleteInput optionText="description" optionValue="id" options={{ disabled: true }} />
            </ReferenceInput>

            <ReferenceInput
                source="conceptSctid"   
                reference="snowstormproblems"
                label="resources.carelineproblems.fields.snomedId"
                filterToQuery={searchText => ({ term: searchText})}
                perPage={30}
            >
                <AutocompleteInput resettable={false} optionText="term" optionValue="id" validate={[required()]} />
            </ReferenceInput>
        </SimpleForm>
    </Create>
);

export default CareLineProblemCreate;
