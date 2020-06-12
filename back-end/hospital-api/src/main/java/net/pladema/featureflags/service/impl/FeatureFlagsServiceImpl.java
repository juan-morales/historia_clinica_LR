package net.pladema.featureflags.service.impl;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import net.pladema.featureflags.service.FeatureFlagsService;
import net.pladema.featureflags.service.domain.FeatureFlagBo;
import net.pladema.featureflags.service.domain.FlavorBo;
import net.pladema.flavor.service.FlavorService;
import net.pladema.sgx.featureflags.AppFeature;

@Service
public class FeatureFlagsServiceImpl implements FeatureFlagsService {

	private static final Logger LOG = LoggerFactory.getLogger(FeatureFlagsServiceImpl.class);

	private final EnumMap<AppFeature, Boolean> flags;

	private final Environment env;

	public FeatureFlagsServiceImpl(Environment env, FlavorService flavorService) {
		this.env = env;
		this.flags = buildFFMap(flavorService.getFlavor());
	}

	public boolean isOn(AppFeature feature) {
		return flags.getOrDefault(feature, false);
	}

	private EnumMap<AppFeature, Boolean> buildFFMap(FlavorBo flavor) {
		EnumMap<AppFeature, Boolean> map = new EnumMap<>(AppFeature.class);
		buildFFList(flavor).forEach(featureFlagBo -> map.put(featureFlagBo.key, featureFlagBo.active));
		return map;
	}

	private List<FeatureFlagBo> buildFFList(FlavorBo flavor) {
		List<FeatureFlagBo> allFlags = new ArrayList<>();
		allFlags.add(flag(AppFeature.RESPONSIBLE_DOCTOR_REQUIRED, flavor.anyMatch(FlavorBo.HOSPITALES)));
		
		allFlags.add(flag(AppFeature.HABILITAR_ALTA_SIN_EPICRISIS, flavor.anyMatch(FlavorBo.TANDIL, FlavorBo.CHACO)));

		allFlags.add(flag(AppFeature.MAIN_DIAGNOSIS_REQUIRED, flavor.anyMatch(FlavorBo.HOSPITALES)));

		return allFlags;
	}

	private FeatureFlagBo flag(AppFeature feature, boolean active) {

		// Si se definió una property para este flag, se utiliza con mas prioridad
		String featureFlagProperty = "togglz.features." + feature.name() + ".enabled";
		boolean isActive = Boolean.parseBoolean(env.getProperty(featureFlagProperty, Boolean.toString(active)));

		FeatureFlagBo flag = new FeatureFlagBo(feature, isActive);
		LOG.info("Using {}", flag);
		return flag;
	}

}