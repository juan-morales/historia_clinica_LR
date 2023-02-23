package net.pladema.patient.infraestructure.output.notification;

import org.springframework.stereotype.Service;

import ar.lamansys.sgx.shared.notifications.application.NotificationSender;
import ar.lamansys.sgx.shared.notifications.application.RecipientMapper;
import ar.lamansys.sgx.shared.notifications.application.RecipientNotificationSender;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementa el envío de mensajes al paciente
 */
@Slf4j
@Service
public class PatientNotificationSender extends RecipientNotificationSender<PatientRecipient> {
	public PatientNotificationSender(
			RecipientMapper<PatientRecipient> recipientMapper,
			NotificationSender notificationSender
	) {
		super(recipientMapper, notificationSender);
	}

}
