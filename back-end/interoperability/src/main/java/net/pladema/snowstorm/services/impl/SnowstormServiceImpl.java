package net.pladema.snowstorm.services.impl;

import ar.lamansys.sgx.shared.restclient.configuration.resttemplate.exception.RestTemplateApiException;
import ar.lamansys.sgx.shared.restclient.services.RestClient;
import ar.lamansys.sgx.shared.restclient.services.RestClientInterface;
import net.pladema.snowstorm.configuration.SnowstormRestTemplateAuth;
import net.pladema.snowstorm.configuration.SnowstormWSConfig;
import net.pladema.snowstorm.repository.SnowstormRepository;
import net.pladema.snowstorm.repository.entity.ManualClassification;
import net.pladema.snowstorm.services.SnowstormService;
import net.pladema.snowstorm.services.domain.ManualClassificationBo;
import net.pladema.snowstorm.services.domain.SnowstormConcept;
import net.pladema.snowstorm.services.domain.SnowstormItemResponse;
import net.pladema.snowstorm.services.domain.SnowstormSearchResponse;
import net.pladema.snowstorm.services.domain.semantics.SnomedECL;
import net.pladema.snowstorm.services.domain.semantics.SnomedSemantics;
import net.pladema.snowstorm.services.exceptions.SnowstormEnumException;
import net.pladema.snowstorm.services.exceptions.SnowstormApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnowstormServiceImpl implements SnowstormService {

    public static final String RESPUESTA_NULA = "Respuesta nula";
     public static final String OUTPUT = "Output -> {}";
    private final Logger logger;

    private final SnowstormWSConfig snowstormWSConfig;

    private final SnomedSemantics snomedSemantics;

    private final RestClientInterface restClientInterface;

    private final SnowstormRepository snowstormRepository;

    public SnowstormServiceImpl(SnowstormRestTemplateAuth restTemplateSSL,
                                SnowstormWSConfig wsConfig,
                                SnomedSemantics snomedSemantics,
                                SnowstormRepository snowstormRepository) {
        this.snowstormWSConfig = wsConfig;
        this.snomedSemantics = snomedSemantics;
        this.restClientInterface = new RestClient(restTemplateSSL, wsConfig);
        this.snowstormRepository = snowstormRepository;
        this.logger = LoggerFactory.getLogger(CalculateCie10CodesServiceImpl.class);
    }

    @Override
    public SnowstormSearchResponse getConcepts(String term, String eclKey) throws SnowstormApiException {

        StringBuilder urlWithParams = new StringBuilder(snowstormWSConfig.getConceptsUrl());

        urlWithParams.append("?termActive=").append(snowstormWSConfig.getTermActive());
        urlWithParams.append("&limit=").append(snowstormWSConfig.getConceptsLimit());

        for (Long preferredOrAcceptableIn : snowstormWSConfig.getPreferredOrAcceptableIn()) {
            if (preferredOrAcceptableIn != null) {
                urlWithParams.append("&preferredOrAcceptableIn=").append(preferredOrAcceptableIn);
            }
        }

        urlWithParams.append("&term=").append(term);

        if (eclKey != null) {
            var snomedEcl = SnomedECL.map(eclKey);
            urlWithParams.append("&ecl=").append(snomedSemantics.getEcl(snomedEcl));
        }

        ResponseEntity<SnowstormSearchResponse> response = null;
        try {
            response = restClientInterface.exchangeGet(urlWithParams.toString(), SnowstormSearchResponse.class);
        } catch (RestTemplateApiException e) {
            throw mapException(e);
        }
        SnowstormSearchResponse result = response.getBody();
        if (result == null)
            throw new SnowstormApiException(SnowstormEnumException.NULL_RESPONSE, response.getStatusCode(), RESPUESTA_NULA);
        return result;
    }

    @Override
    public List<SnowstormItemResponse> getConceptAncestors(String conceptId) throws SnowstormApiException {
        String urlWithParams = snowstormWSConfig.getBrowserConceptUrl()
                .concat(conceptId)
                .concat("/ancestors")
                .concat("?form=inferred");
        ResponseEntity<SnowstormConcept> response;
        try {
            response = restClientInterface.exchangeGet(urlWithParams, SnowstormConcept.class);
        } catch (RestTemplateApiException e) {
            throw mapException(e);
        }
        SnowstormConcept result = response.getBody();
        if (result == null)
            throw new SnowstormApiException(SnowstormEnumException.NULL_RESPONSE, response.getStatusCode(), RESPUESTA_NULA);
        return result.getItems();
    }

    public <T> T getRefsetMembers(String referencedComponentId, String referenceSetId, String limit, Class<T> type) throws SnowstormApiException {
        StringBuilder urlWithParams = new StringBuilder(snowstormWSConfig.getRefsetMembersUrl());

        urlWithParams.append("?referenceSet=" + referenceSetId);
        urlWithParams.append("&referencedComponentId=" + referencedComponentId);
        urlWithParams.append("&active=true");
        urlWithParams.append("&offset=0");
        urlWithParams.append("&limit=" + limit);

        ResponseEntity<T> response;
        try {
            response = restClientInterface.exchangeGet(urlWithParams.toString(), type);
        } catch (RestTemplateApiException e) {
            throw mapException(e);
        }
        T result = response.getBody();
        if (result == null)
            throw new SnowstormApiException(SnowstormEnumException.NULL_RESPONSE, response.getStatusCode(), RESPUESTA_NULA);
        return result;
    }

    @Override
    public SnowstormSearchResponse getConcepts(String ecl) throws SnowstormApiException {
        StringBuilder urlWithParams = new StringBuilder(snowstormWSConfig.getConceptsUrl());

        urlWithParams.append("?termActive=" + snowstormWSConfig.getTermActive());
        urlWithParams.append("&ecl=" + ecl);
        ResponseEntity<SnowstormSearchResponse> response;
        try {
            response = restClientInterface.exchangeGet(urlWithParams.toString(), SnowstormSearchResponse.class);
        } catch (RestTemplateApiException e) {
            throw mapException(e);
        }

        SnowstormSearchResponse result = response.getBody();
        if (result == null)
            throw new SnowstormApiException(SnowstormEnumException.NULL_RESPONSE, response.getStatusCode(), RESPUESTA_NULA);
        return result;
    }

    @Override
    public List<ManualClassificationBo> isSnvsReportable(String sctid, String pt) {
        logger.debug("Input parameters -> sctid {}, pt {}", sctid, pt);
        List<ManualClassification> resultQuery = snowstormRepository.isSnvsReportable(sctid, pt);
        List<ManualClassificationBo> result = resultQuery.stream().map(ManualClassificationBo::new).collect(Collectors.toList());
        logger.debug(OUTPUT, result);
        return result;
    }

    private SnowstormApiException mapException(RestTemplateApiException apiException) {
        if (apiException.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR)
            return new SnowstormApiException(SnowstormEnumException.SERVER_ERROR, apiException.getStatusCode(), "El servicio de snowstorm tiene un error interno");
        if (apiException.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (apiException.getStatusCode() == HttpStatus.REQUEST_TIMEOUT)
                return new SnowstormApiException(SnowstormEnumException.API_TIMEOUT, apiException.getStatusCode(), "El servicio de snowstorm esta tardando en responder");
            if (apiException.getStatusCode() == HttpStatus.NOT_FOUND)
                return new SnowstormApiException(SnowstormEnumException.NOT_FOUND, apiException.getStatusCode(), "El servicio consultado no existe");
            if (apiException.getStatusCode() == HttpStatus.BAD_REQUEST)
                return new SnowstormApiException(SnowstormEnumException.BAD_REQUEST, apiException.getStatusCode(), "No se están cumpliendo con los requisitos de snowstorm");
        }
        return new SnowstormApiException(SnowstormEnumException.UNKNOWN_ERROR, apiException.getStatusCode(), "Estado de error desconocido");
    }
}
