import React from 'react';
import {
    List,
    Datagrid,
    TextField,
    FunctionField,
    ReferenceField,
    DeleteButton,
} from 'react-admin';
import SubReference from '../components/subreference';
import renderPerson from '../components/renderperson';

const HealthcareProfessionalSpecialtyList = props => (
    <List {...props} hasCreate={false}>
        <Datagrid rowClick="show">
            <TextField source="description"/>
            <ReferenceField source="healthcareProfessionalId" reference="healthcareprofessionals" link={false}>
                <SubReference source="personId" reference="people" link={false}>
                    <FunctionField render={renderPerson}/>
                </SubReference>
            </ReferenceField>
            <ReferenceField source="professionalSpecialtyId" reference="professionalspecialties">
                <TextField source="description" />
            </ReferenceField>
            <DeleteButton />
        </Datagrid>
    </List>
);

export default HealthcareProfessionalSpecialtyList;

