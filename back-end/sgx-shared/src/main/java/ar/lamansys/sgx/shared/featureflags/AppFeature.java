package ar.lamansys.sgx.shared.featureflags;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum AppFeature implements Feature {

    @Label("Indica si se puede dar de alta una internación sin tener una epicrisis asociada")
    HABILITAR_ALTA_SIN_EPICRISIS,

    @Label("Indica si el diagnostico principal en una internación es obligatorio")
    MAIN_DIAGNOSIS_REQUIRED,

    @Label("Indica si el médico responsable de una internación es obligatorio")
    RESPONSIBLE_DOCTOR_REQUIRED,

    @Label("Indica si es posible cargar la fecha probable de alta de una internación")
    HABILITAR_CARGA_FECHA_PROBABLE_ALTA,

    @Label("Indica si se debe habilitar la funcionalidad gestión de turnos")
    HABILITAR_GESTION_DE_TURNOS,

    @Label("Indica si se debe habilitar la funcionalidad historia clinica ambulatoria")
    HABILITAR_HISTORIA_CLINICA_AMBULATORIA,

    @Label("Indica si esta habilitada la funcionalidad para editar un paciente")
    HABILITAR_EDITAR_PACIENTE_COMPLETO,

    @Label("Indica si esta habilitado el modulo de Guardia")
    HABILITAR_MODULO_GUARDIA,

    @Label("Indica si esta habilitado el módulo de Portal de Paciente")
    HABILITAR_MODULO_PORTAL_PACIENTE,

    @Label("Indica si esta habilitado el menu configuracion")
    HABILITAR_CONFIGURACION,

    @Label("Indica si está habilitado el Bus de Interoperabilidad")
    HABILITAR_BUS_INTEROPERABILIDAD,

    @Label("Indica si está habilitado el módulo de odontología")
    HABILITAR_ODONTOLOGY,

    @Label("Indica si está habilitado el módulo de Reportes")
    HABILITAR_REPORTES,
    
    @Label("Indica si está habilitada la funcionalidad para descargar informes desde turnos y desde el perfil del paciente")
    HABILITAR_INFORMES,

    @Label("Habilita los llamados")
    HABILITAR_LLAMADO,

    @Label("Habilita la pestaña para ver las historias clínicas externas del paciente")
    HABILITAR_HISTORIA_CLINICA_EXTERNA,

    @Label("Indica si se habilita la integración con RENAPER")
    HABILITAR_SERVICIO_RENAPER,

    @Label("Indica si se restringe la opción de editar paciente")
    RESTRINGIR_DATOS_EDITAR_PACIENTE,
    
    @Label("Indica si está habilitado la opción de cambiar el tema de la aplicación")
    HABILITAR_INTERCAMBIO_TEMAS,

    @Label("Indica si está habilitada la funcionalidad para crear usuarios")
    HABILITAR_CREACION_USUARIOS,
    
    @Label("Indica si esta habilitado la opcion de reportar epidemiológicamente un problema")
    HABILITAR_REPORTE_EPIDEMIOLOGICO,

    @Label("Indica si esta habilitado la opcion de agregar médicos de responsables/cabecera")
    AGREGAR_MEDICOS_ADICIONALES,

    @Label("Indica si esta habilitado la opcion para descargar los documentos pdfs generados en la historia clinica desde el backoffice")
    HABILITAR_DESCARGA_DOCUMENTOS_PDF,

    @Label("Indica si esta habilitada la opción para visualizar el nombre y genero autopercibido del paciente en lugar del nombre y el genero del documento")
	HABILITAR_DATOS_AUTOPERCIBIDOS,

    @Label("Indica si esta habilitada la opción para visualizar las propiedades configuradas en el sistema ")
    HABILITAR_VISUALIZACION_PROPIEDADES_SISTEMA,

	@Label("Indica si esta habilitada la opción para generar asincronicamente documentos pdf ")
	HABILITAR_GENERACION_ASINCRONICA_DOCUMENTOS_PDF,

	@Label("Indica si esta habilitada la búsqueda de conceptos en la base de datos local")
	HABILITAR_BUSQUEDA_LOCAL_CONCEPTOS,

	@Label("Indica si se debe avisar al paciente por mail al reservar un turno")
	HABILITAR_MAIL_RESERVA_TURNO,

	@Label("Indica si la api publica de reserva de turnos es abierta sin la necesidad de usar api-key")
	LIBERAR_API_RESERVA_TURNOS,

	@Label("Muestra los menus en backoffice para la administración de turnos online de la institución (Solo para administradores institucionales)")
	BACKOFFICE_MOSTRAR_ABM_RESERVA_TURNOS,

	@Label("Oculta el listado de profesiones asociado a un paciente en la webapp")
	OCULTAR_LISTADO_PROFESIONES_WEBAPP,
    
	@Label("Muestra en HC la seccion de Enfermeria")
	HABILITAR_MODULO_ENF_EN_DESARROLLO,

    @Label("Indica si está habilitado el doble factor de autenticación")
	HABILITAR_2FA,

	@Label("Indica si están habilitadas las extensiones por web components ")
	HABILITAR_EXTENSIONES_WEB_COMPONENTS,

    @Label("Indica si esta habilitado el servicio de notificaciones al asignar un turno")
	HABILITAR_NOTIFICACIONES_TURNOS,

	@Label("Habilita PopUp de guardado en consulta ambulatoria")
	HABILITAR_GUARDADO_CON_CONFIRMACION_CONSULTA_AMBULATORIA,

	@Label("Habilita reportes estadísticos de consultas ambulatorias y turnos")
	HABILITAR_REPORTES_ESTADISTICOS,

	@Label("Indica si esta habilitado el uso de card en HC e INTERNACION")
	HABILITAR_VISUALIZACION_DE_CARDS,

	@Label("Indica si esta habilitada la recuperación de contraseña por correo")
	HABILITAR_RECUPERAR_PASSWORD,

	@Label("Indica si esta habilitada la funcionalidad de red de imágenes")
	HABILITAR_DESARROLLO_RED_IMAGENES,

	@Label("Indica si esta habilitada la integración con SIP PLUS")
	HABILITAR_SIP_PLUS,
	
	@Label("Indica si esta habilitada la validacion de matriculas mediante SISA-REFEPS")
	HABILITAR_VALIDACION_MATRICULAS_SISA,
	
	@Label("Indica si esta habilitada la emision de recetas digitales")
	HABILITAR_RECETA_DIGITAL,

	@Label("Indica si esta habilitado el rol prescriptor")
	HABILITAR_PRESCRIPCION_RECETA,

	@Label("Indica si esta habilitada la seccion de auditoria de pacientes")
	HABILITAR_MODULO_AUDITORIA,
    
	@Label("Indica si están habilitadas las secciones relativas a CIPRES en epicrisis")
	HABILITAR_CAMPOS_CIPRES_EPICRISIS,

	@Label("Indica si está habilitada la acción de imprimir HC de pacientes")
	HABILITAR_IMPRESION_HISTORIA_CLINICA_EN_DESARROLLO,

	@Label("Indica si está habilitado el uso de API Pública con el Rol genérico")
	HABILITAR_API_CONSUMER,

	@Label("Indica si el módulo de telemedicina está activo")
	HABILITAR_TELEMEDICINA,

	@Label("Indica si está disponible la solapa referencias del módulo Gestión de turnos")
	HABILITAR_REPORTE_REFERENCIAS_EN_DESARROLLO,
	
	@Label("Habilita la obligatoriedad del campo de unidades jerárquicas como requerido")
	HABILITAR_OBLIGATORIEDAD_UNIDADES_JERARQUICAS,
	;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }

    public String getLabel() {
        return FeatureContext.getFeatureManager().getMetaData(this).getLabel();
    }

    public String propertyNameFor() {
        return String.format("%s%s", ToggleConfiguration.PREFIX_APP_FEATURE_PROPERTY, this);
    }


}