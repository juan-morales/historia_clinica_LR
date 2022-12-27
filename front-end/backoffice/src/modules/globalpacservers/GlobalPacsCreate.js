import React from 'react';
import {
    TextInput,
    Create,
    SimpleForm,
    required,
    maxLength,
    FormDataConsumer,
    usePermissions,
    PasswordInput
} from 'react-admin';
import CustomToolbar from "../components/CustomToolbar";
import { ROOT } from "../roles";
import SgxSelectInput from "../../sgxSelectInput/SgxSelectInput";

const InstitutionPracticesCreate = props => {
    const { permissions } = usePermissions();
    const userIsRoot = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ROOT.role)).length > 0;
    return(
        <Create {...props} hasCreate={userIsRoot}>
            <SimpleForm redirect="show" toolbar={<CustomToolbar/>}>

                {/* Name */}
                <TextInput source="name" validate={[
                    required(),
                    maxLength(20)]}
                />

                {/* AETITLE */}
                <TextInput source="aetitle" validate={[
                    required(),
                    maxLength(15)]}
                />

                {/* Domain */}
                <TextInput source="domain" validate={[
                    required(),
                    maxLength(50)]}
                />

                {/* PAC Server Type */}
                <FormDataConsumer>
                    {formDataProps => ( <SgxSelectInput {...formDataProps} source="pacServerType" element="pacservertypes" optionText="description" allowEmpty={false} required={true} />)}
                </FormDataConsumer>

                {/* PAC Server Protocol */}
                <FormDataConsumer>
                    {formDataProps => ( <SgxSelectInput {...formDataProps} source="pacServerProtocol" element="pacserverprotocols" optionText="description" allowEmpty={false} required={true} />)}
                </FormDataConsumer>

                {/* Username */}
                <TextInput source="username" validate={[
                    required(),
                    maxLength(50)]}
                />

                {/* Password */}
                <PasswordInput source="password" validate={[
                    required()]}
                />

                {/* url_stow */}
                <TextInput source="urlStow" validate={[
                    required()]}
                />

                {/* url_auth */}
                <TextInput source="urlAuth" validate={[
                    required()]}
                />

            </SimpleForm>
        </Create>
    );
};

export default InstitutionPracticesCreate;