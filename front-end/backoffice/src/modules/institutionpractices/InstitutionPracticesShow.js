import {
    Datagrid,
    DeleteButton,
    Pagination,
    ReferenceField,
    ReferenceManyField,
    SimpleShowLayout,
    TextField,
    useTranslate,
    ShowController,
    ShowView,
    usePermissions,
} from 'react-admin';
import SgxDateField from "../../dateComponents/sgxDateField";
import React from "react";
import { Link } from 'react-router-dom';
import Button from "@material-ui/core/Button";
import SectionTitle from "../components/SectionTitle";
import {ADMINISTRADOR, ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE, ROOT} from "../roles";

const CreateRelatedButton = ({ record, reference, label, disabled}) => {
    const newRecord = {groupId: record.id, parentGroupId: record.groupId};
    const translate = useTranslate();
    return (
        <Button
            component={Link}
            to={{
                pathname: `/${reference}/create`,
                state: { record: newRecord },
            }}
            disabled={disabled}
        >{translate(label)}</Button>
    );
};

const SnomedGroupShow = props => {
    const translate = useTranslate();
    const { permissions } = usePermissions();;
    const userIsRootOrAdmin = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ADMINISTRADOR.role) || (roleAssignment.role === ROOT.role)).length > 0;
    const userIsAdminInstitutional = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE.role)).length > 0;
    return (
        <ShowController {...props} >
            {controllerProps =>
                {
                    const isEditableGroup = controllerProps?.record?.groupId != null;
                    const isDomainGroup = !controllerProps?.record?.institutionId;
                    const institutionId = controllerProps?.record?.institutionId;
                    const userIsAdmin = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ADMINISTRADOR.role)).length > 0;
                    const userCanEditInstitution = permissions?.roleAssignments?.filter(roleAssignment => (roleAssignment.role === ADMINISTRADOR_INSTITUCIONAL_BACKOFFICE.role && roleAssignment.institutionId === institutionId)).length > 0;

                    const canBeEdited = isDomainGroup ? (isEditableGroup && userIsAdmin) : ((isEditableGroup && userCanEditInstitution) || userIsAdmin);

                    return <ShowView {...props} {...controllerProps} hasEdit={userIsAdminInstitutional}>
                        <SimpleShowLayout>

                            {/* Institution */}
                            <ReferenceField source="institutionId" reference="institutions" link="show"
                                            emptyText={translate('resources.institutionpractices.noInfo')}>
                                <TextField source="name"/>
                            </ReferenceField>

                            {/* Description */}
                            <TextField source="description"/>

                            {/* Custom ID */}
                            <TextField source="customId"/>

                            {/* Last update */}
                            <SgxDateField source="lastUpdate"/>

                            {/* Snomed concepts */}
                            <SectionTitle label="resources.institutionpractices.fields.snomedConcepts"/>

                            <CreateRelatedButton
                                reference="institutionpracticesrelatedgroups"
                                label="resources.institutionpractices.createRelated"
                                disabled={userIsRootOrAdmin}
                            />

                            <ReferenceManyField
                                reference="snomedgroupconcepts"
                                target="groupId"
                                sort={{field: 'conceptPt', order: 'ASC'}}
                                perPage={10}
                                pagination={<Pagination rowsPerPageOptions={[10, 25, 50]}/>}
                                addLabel={false}
                            >
                                <Datagrid>
                                    <TextField source="orden"/>
                                    <TextField source="conceptPt"/>
                                    <TextField source="conceptSctid"/>
                                    <SgxDateField source="lastUpdate"/>
                                    <DeleteButton redirect={false} disabled={!canBeEdited}/>
                                </Datagrid>
                            </ReferenceManyField>
                        </SimpleShowLayout>
                    </ShowView>
                }
            }
        </ShowController>
    );
}

export default SnomedGroupShow;
