package ar.lamansys.sgx.shared.flavor.instances;

import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.featureflags.states.InitialFeatureStates;

import java.util.EnumMap;

public class HospitalsFeatureStates implements InitialFeatureStates {

	@Override
	public EnumMap<AppFeature, Boolean> getStates() {
		EnumMap<AppFeature, Boolean> map = new EnumMap<>(AppFeature.class);

		map.put(AppFeature.HABILITAR_ALTA_SIN_EPICRISIS, false);
		map.put(AppFeature.MAIN_DIAGNOSIS_REQUIRED, true);
		map.put(AppFeature.RESPONSIBLE_DOCTOR_REQUIRED, true);
		map.put(AppFeature.HABILITAR_CARGA_FECHA_PROBABLE_ALTA, true);
		map.put(AppFeature.HABILITAR_GESTION_DE_TURNOS, true);
		map.put(AppFeature.HABILITAR_HISTORIA_CLINICA_AMBULATORIA, true);
		map.put(AppFeature.HABILITAR_UPDATE_DOCUMENTS, false);
		map.put(AppFeature.HABILITAR_EDITAR_PACIENTE_COMPLETO, false);
		map.put(AppFeature.HABILITAR_MODULO_GUARDIA, false);
		map.put(AppFeature.HABILITAR_MODULO_PORTAL_PACIENTE, true);
		map.put(AppFeature.HABILITAR_CONFIGURACION, true);
		map.put(AppFeature.HABILITAR_BUS_INTEROPERABILIDAD, false);
		map.put(AppFeature.HABILITAR_ODONTOLOGY, false);
		map.put(AppFeature.HABILITAR_REPORTES, false);
		map.put(AppFeature.HABILITAR_INFORMES, false);
		map.put(AppFeature.HABILITAR_LLAMADO, false);
		map.put(AppFeature.HABILITAR_HISTORIA_CLINICA_EXTERNA, false);
		map.put(AppFeature.HABILITAR_SERVICIO_RENAPER, true);
		map.put(AppFeature.RESTRINGIR_DATOS_EDITAR_PACIENTE, true);
		map.put(AppFeature.HABILITAR_INTERCAMBIO_TEMAS, false);
		map.put(AppFeature.HABILITAR_CREACION_USUARIOS, false);
		map.put(AppFeature.HABILITAR_REPORTE_EPIDEMIOLOGICO, false);
		return map;
	}

}