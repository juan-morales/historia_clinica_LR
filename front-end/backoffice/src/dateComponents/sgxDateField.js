import React from "react";
import {
    DateField
} from 'react-admin';

const SgxDateField = ({ source, ...props})  => {
    return <DateField source={source} {...props} />
}

SgxDateField.defaultProps = {
    locales: "es-AR",
    options: {timeZone: 'UTC'},
    addLabel: true
}

export default SgxDateField;