package ar.lamansys.sgx.auth.user.infrastructure.output.notification;

import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.templating.NotificationTemplateInput;

/**
 * Define el nombre del template y los argumentos que se requieren como input para renderizar el mensaje.
 */
public class RestorePasswordTemplateInput extends NotificationTemplateInput<RestorePasswordNotificationArgs> {

	public final static String TEMPLATE_ID = "restore-password";

	public RestorePasswordTemplateInput(RestorePasswordNotificationArgs args){
		super(TEMPLATE_ID, args, AppFeature.HABILITAR_MAIL_RECUPERAR_CONTRASEÑA);
	}

}
