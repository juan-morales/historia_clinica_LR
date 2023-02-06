package net.pladema.sipplus.application.createmother;

import ar.lamansys.sgx.shared.restclient.configuration.resttemplate.exception.RestTemplateApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.pladema.sipplus.application.port.SipPlusWSStorage;

import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateMother {

	private final SipPlusWSStorage sipPlusWSStorage;

	public void run(Integer patientId, Integer pregnancyNumber) throws RestTemplateApiException {
		log.debug("Input parameters -> patientId {}, pregnancyNumber {}", patientId, pregnancyNumber);
		sipPlusWSStorage.createMother(patientId, pregnancyNumber);
	}
}
