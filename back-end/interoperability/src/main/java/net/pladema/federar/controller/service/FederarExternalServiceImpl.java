package net.pladema.federar.controller.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.pladema.federar.controller.FederarExternalService;
import net.pladema.federar.services.FederarService;
import net.pladema.federar.services.domain.FederarResourceAttributes;
import net.pladema.federar.services.exceptions.FederarApiException;


@Service
public class FederarExternalServiceImpl implements FederarExternalService {

    private static final Logger LOG = LoggerFactory.getLogger(FederarExternalServiceImpl.class);

    private final FederarService federarService;

    public FederarExternalServiceImpl(FederarService federarService){
        this.federarService = federarService;
    }

    @Override
    public Optional<Integer> federatePatient(FederarResourceAttributes attributes, Integer localId) throws FederarApiException {
        LOG.debug("Going to federate Patient => {} /n with data => {}", localId, attributes);
        return federarService.federatePatient(attributes, localId);

    }

}
