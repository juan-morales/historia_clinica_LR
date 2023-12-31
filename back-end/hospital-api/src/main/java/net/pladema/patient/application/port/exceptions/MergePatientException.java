package net.pladema.patient.application.port.exceptions;

import lombok.Getter;

@Getter
public class MergePatientException extends RuntimeException {

    public final MergePatientExceptionEnum code;

    public MergePatientException(MergePatientExceptionEnum code, String message) {
        super(message);
        this.code = code;
    }

}

