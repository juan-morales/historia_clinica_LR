import React from 'react';
import {
    SelectInput,
    Edit, FormDataConsumer,
    ReferenceInput,
    required,
    SimpleForm,
    TextInput,
} from 'react-admin';
import SgxSelectInput from '../../sgxSelectInput/SgxSelectInput';
import CustomToolbar from '../components/CustomToolbar';

const AMBULATORIA = 1;

const SectorField = ({formData}) => {
    return <ReferenceInput 
        source="sectorId"
        reference="sectors"
        sort={{ field: 'description', order: 'ASC' }}
        filter={{sectorTypeId: AMBULATORIA, institutionId: formData.institutionId}}>
        <SelectInput optionText="description" optionValue="id" allowEmpty={false} options={{ disabled: true }} />
    </ReferenceInput>
}

const ShockRoomEdit = (props) => {
    return (
        <Edit {...props}>
            <SimpleForm redirect="show" toolbar={<CustomToolbar isEdit={true} />}>
                <TextInput source="description" validate={[required()]} />
                <SgxSelectInput source="institutionId"
                    element="institutions"
                    optionText="name"
                    alwaysOn
                    allowEmpty={false}
                    options={{ disabled: true }}/>
                <FormDataConsumer>
                    {formDataProps => (<SectorField {...formDataProps} source="sectorId"/>)}
                </FormDataConsumer>
            </SimpleForm>
        </Edit>
    )
};

export default ShockRoomEdit;