import React, {useState} from 'react';
import {
    useNotify, fetchStart, fetchEnd, useRefresh,
} from 'react-admin';
import {useDispatch} from "react-redux";
import Button from '@material-ui/core/Button';
import {sgxFetchApiWithToken} from "../../libs/sgx/api/fetch";

const FileRebuildButton = ({ record }) => {
    const dispatch = useDispatch();
    const refresh = useRefresh();
    const notify = useNotify();
    const [loading, setLoading] = useState(false);
    const handleClick = () => {
        setLoading(true);
        dispatch(fetchStart());
        sgxFetchApiWithToken(`backoffice/documentfiles/${record.id}/rebuild-file`, { method: 'PUT' })
            .then((response) => {
                notify('Se regenero el documento exitosamente', { type: 'success' })
                refresh();
            })
            .catch((e) => {
                notify('La regeneración del documento tuvo un error', { type: 'warning' })
            })
            .finally(() => {
                setLoading(false);
                dispatch(fetchEnd());
            });
    };
    return (
        <Button onClick={handleClick} color="primary" size="small" disabled={loading}>
        Regenerar documento
        </Button>
    );
};
export default FileRebuildButton;
