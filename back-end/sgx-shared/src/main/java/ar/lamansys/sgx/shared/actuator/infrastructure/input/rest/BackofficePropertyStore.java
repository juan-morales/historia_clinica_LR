package ar.lamansys.sgx.shared.actuator.infrastructure.input.rest;


import ar.lamansys.sgx.shared.actuator.domain.PropertyBo;
import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.featureflags.application.FeatureFlagsService;
import net.pladema.sgx.backoffice.repository.BackofficeStore;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BackofficePropertyStore implements BackofficeStore<PropertyBo, String> {

	private final Set<PropertyBo> properties;

	private final FeatureFlagsService featureFlagsService;

	public BackofficePropertyStore(Optional<EnvironmentEndpoint> environ, FeatureFlagsService featureFlagsService) {
		this.featureFlagsService = featureFlagsService;
		String include = "(spring\\.*|email-*|ws\\.*|logging\\.*|ws\\.*|jobs\\.*|"
				+ "app\\.*|api\\.*|admin\\.*|token\\.*|management\\.*|"
				+ "oauth\\.*|externalurl\\.*|integration\\.*|actuator\\.*|"
				+ "internment\\.*|images\\.*|mail\\.*|recaptcha\\.*|.*\\.cron\\.*|"
				+ "habilitar\\.*|hsi\\.*"
				+ ")";
		properties = environ.map(env -> env.environment(include))
				.map(environmentDescriptor -> generateSource(environmentDescriptor.getPropertySources()))
				.orElse(Collections.emptySet());

	}

	private Set<PropertyBo> generateSource(List<EnvironmentEndpoint.PropertySourceDescriptor> propertySources) {
		Set<PropertyBo> sortedSet = new LinkedHashSet<>();
		propertySources.forEach(propertySourceDescriptor -> {
				Set<PropertyBo> subProperties = buildProperty(propertySourceDescriptor);
				sortedSet.addAll(subProperties);
		});
		return sortedSet;

	}

	private Set<PropertyBo> buildProperty(EnvironmentEndpoint.PropertySourceDescriptor propertySourceDescriptor) {
		Set<PropertyBo> sortedSet = new LinkedHashSet();
		propertySourceDescriptor
				.getProperties()
				.forEach((s, propertyValueDescriptor) -> sortedSet.add(new PropertyBo(s, (String)(propertyValueDescriptor.getValue()), propertySourceDescriptor.getName())));
		return sortedSet;
	}

	@Override
	public Page<PropertyBo> findAll(PropertyBo entity, Pageable pageable) {
		if (!featureFlagsService.isOn(AppFeature.HABILITAR_VISUALIZACION_PROPIEDADES_SISTEMA))
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		var result = properties.stream()
				.filter(propertyBo -> entity.getId() == null || propertyBo.getId().contains(entity.getId()))
				.collect(Collectors.toList());
		int from = pageable.getPageSize() * pageable.getPageNumber();
		int to = Math.min(result.size(), (from + pageable.getPageSize()));
		return new PageImpl<>(result.subList(from, to), pageable, result.size());
	}

	@Override
	public List<PropertyBo> findAll() {
		return featureFlagsService.isOn(AppFeature.HABILITAR_VISUALIZACION_PROPIEDADES_SISTEMA) ?
				new ArrayList<>(properties) :
				Collections.emptyList();
	}

	@Override
	public List<PropertyBo> findAllById(List<String> ids) {
		return featureFlagsService.isOn(AppFeature.HABILITAR_VISUALIZACION_PROPIEDADES_SISTEMA) ?
				properties.stream()
				.filter(propertyBo -> ids.contains(propertyBo.getId()))
				.collect(Collectors.toList()) :
				Collections.emptyList();
	}

	@Override
	public Optional<PropertyBo> findById(String id) {
		return featureFlagsService.isOn(AppFeature.HABILITAR_VISUALIZACION_PROPIEDADES_SISTEMA) ?
				properties.stream()
				.filter(propertyBo -> propertyBo.getId().equals(id))
				.findFirst() :
				Optional.empty();
	}

	@Override
	public PropertyBo save(PropertyBo dto) {
		return dto;
	}
	
	@Override
	public void deleteById(String id) {
		return;
	}

	@Override
	public Example<PropertyBo> buildExample(PropertyBo entity) {
		return Example.of(entity);
	}


}
