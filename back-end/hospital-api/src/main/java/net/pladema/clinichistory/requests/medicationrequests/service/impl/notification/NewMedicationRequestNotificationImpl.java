package net.pladema.clinichistory.requests.medicationrequests.service.impl.notification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.clinichistory.requests.medicationrequests.service.GetMedicationRequestInfoService;
import net.pladema.clinichistory.requests.medicationrequests.service.NewMedicationRequestNotification;
import net.pladema.patient.infrastructure.output.notification.PatientNotificationSender;
import net.pladema.patient.infrastructure.output.notification.PatientRecipient;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewMedicationRequestNotificationImpl implements NewMedicationRequestNotification {

	private final PatientNotificationSender patientNotificationSender;

	private final GetMedicationRequestInfoService getMedicationRequestInfoService;

	@Value("${mail.subject.digital-recipe}")
	private String digitalRecipeSubject;

	@Override
	public void run(NewMedicationRequestNotificationArgs args, String... patientEmail) {
		log.debug("Input parameters -> args {}", args);
		String email = Stream.of(patientEmail).findFirst().orElse(null);
		LocalDate requestDate = getMedicationRequestInfoService.execute(args.getRecipeId()).getRequestDate();
		String subject = digitalRecipeSubject + String.format(" %s hasta %s", formatStringDate(requestDate), formatStringDate(requestDate.plusDays(30)));
		this.patientNotificationSender.send(new PatientRecipient(args.getPatient().getId(), email), new NewMedicationRequestTemplateInput(args, subject));
	}

	private String formatStringDate(LocalDate date){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return date.format(dateTimeFormatter);

	}
}
