import SGXPermissions from '../../libs/sgx/auth/SGXPermissions';

import RuleCreate from "./RuleCreate";
import RuleList from "./RuleList";

import { ADMINISTRADOR_DE_ACCESO_DOMINIO } from "../roles";


const rules = (permissions: SGXPermissions) => ({
    create: permissions.hasAnyAssignment(ADMINISTRADOR_DE_ACCESO_DOMINIO) ? RuleCreate : undefined,
    list: permissions.hasAnyAssignment(ADMINISTRADOR_DE_ACCESO_DOMINIO) ? RuleList : undefined,
    options: {
        submenu: 'masterData'
    }
})

export default rules;

