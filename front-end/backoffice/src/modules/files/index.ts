import SGXPermissions from '../../libs/sgx/auth/SGXPermissions';

import DescriptionIcon from '@material-ui/icons/Description';
import FileShow from './FileShow';
import FileList from './FileList';

import { ROOT, ADMINISTRADOR } from '../roles';
import FileEdit from "./FileEdit";

const files = (permissions: SGXPermissions) => ({
    icon: DescriptionIcon,
    show: FileShow,
    edit: FileEdit,
    list: permissions.hasAnyAssignment(ROOT, ADMINISTRADOR) ? FileList : undefined,
    options: {
        submenu: 'debug'
    }
});

export default files;
