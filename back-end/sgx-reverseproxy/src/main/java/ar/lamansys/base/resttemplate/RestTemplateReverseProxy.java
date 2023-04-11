package ar.lamansys.base.resttemplate;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Map;

import ar.lamansys.base.ReverseProxy;
import ar.lamansys.base.configuration.HttpClientConfiguration;
import ar.lamansys.base.configuration.RestTemplateSSL;
import ar.lamansys.base.resttemplate.exceptions.ReverseProxyResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class RestTemplateReverseProxy implements ReverseProxy {
	private final RestTemplate restTemplate;
	private final String baseUrl;
	private final HttpHeaders defaultHeaders;

	public RestTemplateReverseProxy(String baseUrl, HttpClientConfiguration configuration, Map<String, String> defaultHeaders) throws Exception {
		this.restTemplate = new RestTemplateSSL(configuration);
		this.restTemplate.setErrorHandler(new ReverseProxyResponseErrorHandler());
		this.baseUrl = baseUrl;
		this.defaultHeaders = buildHeaders(defaultHeaders);
	}


	@Override
	public ResponseEntity<String> getAsString(String path, Map<String, String[]> parameterMap) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromHttpUrl(this.baseUrl)
				.path(path);

		parameterMap.forEach(uriBuilder::queryParam);

		HttpEntity<String> entity = new HttpEntity<>(defaultHeaders);

		log.trace("Headers to send {}", entity);

		return restTemplate.exchange(uriBuilder.build().toUri(), HttpMethod.GET, entity, String.class);
	}

	public void addHeaders(Map<String, String> headersValues) {
		headersValues.forEach(defaultHeaders::add);
	}

	@Override
	public void updateHeader(String key, String value) {
		if (defaultHeaders.containsKey(key)) defaultHeaders.set(key, value);
		else addHeaders(Map.of(key, value));
	}

	private static HttpHeaders buildHeaders(Map<String, String> headersValues) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_JSON);
		headersValues.forEach(headers::add);
		return headers;
	}
}
