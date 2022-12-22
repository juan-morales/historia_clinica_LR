import React from 'react';
import {
    TextInput,
    Edit,
    SimpleForm,
    required,
    maxLength,
    usePermissions,
    NumberInput, maxValue,
} from 'react-admin';
import CustomToolbar from "../components/CustomToolbar";
import { ROOT } from "../roles";

const ImageLvlPacEdit = props => {
    const { permissions } = usePermissions();
    const userIsRoot = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ROOT.role)).length > 0;
    return (<Edit {...props} hasEdit={userIsRoot}>
        <SimpleForm redirect="show" toolbar={<CustomToolbar />}>

            {/* Name */}
            <TextInput source="name" validate={[
                required(),
                maxLength(100)]}/>

            {/* AETITLE */}
            <TextInput source="aetitle" validate={[
                required(),
                maxLength(100)]}/>

            {/* Domain */}
            <TextInput source="domain" validate={[
                required(),
                maxLength(100)]}/>

            {/* Port */}
            <NumberInput inputProps={{ maxLength: 10 }} source={"port"} validate={[
                required(),
                maxValue(9999999999)]}
            />

            {/* Sector ID */}
            <NumberInput source={"sectorId"} disabled={true} validate={[
                required()]}
            />

        </SimpleForm>
    </Edit>)
};

export default ImageLvlPacEdit;
