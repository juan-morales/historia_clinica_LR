package ar.lamansys.sgh.publicapi.application.port.out.exceptions;

import lombok.Getter;

@Getter
public enum SipPlusExceptionEnum {
	INVALID_TOKEN,
	NULL_IDENTIFICATION_TYPE,
	NULL_IDENTIFICATION_NUMBER
}
