package net.pladema.medicalconsultation.appointment.infraestructure.output.notification;
import net.pladema.establishment.service.InstitutionService;
import net.pladema.establishment.service.domain.InstitutionBo;
import net.pladema.medicalconsultation.appointment.infraestructure.output.notification.exceptions.NewAppointmentNotificationEnumException;
import net.pladema.medicalconsultation.appointment.infraestructure.output.notification.exceptions.NewAppointmentNotificationException;
import net.pladema.medicalconsultation.appointment.service.booking.BookingPersonService;
import net.pladema.medicalconsultation.diary.service.DiaryService;
import net.pladema.medicalconsultation.diary.service.domain.CompleteDiaryBo;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.medicalconsultation.appointment.application.port.NewAppointmentNotification;
import net.pladema.medicalconsultation.appointment.domain.NewAppointmentNotificationBo;
import net.pladema.patient.infraestructure.output.notification.PatientNotificationSender;
import net.pladema.patient.infraestructure.output.notification.PatientRecipient;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
@Service
public class NewAppointmentNotificationImpl implements NewAppointmentNotification {
	private final PatientNotificationSender patientNotificationSender;
	private final BookingPersonService bookingPersonService;
	private final InstitutionService institutionService;
	private final DiaryService diaryService;
	private final Environment env;

	@Override
	public void run(NewAppointmentNotificationBo newAppointmentNotification) {
		CompleteDiaryBo diaryBo = diaryService.getDiary(newAppointmentNotification.diaryId)
				.orElseThrow(()-> new NewAppointmentNotificationException(NewAppointmentNotificationEnumException.DIARY_NOT_FOUND, "La agenda solicitada no existe"));
		String professionalName = bookingPersonService.getProfessionalName(newAppointmentNotification.diaryId)
				.orElseThrow(()-> new NewAppointmentNotificationException(NewAppointmentNotificationEnumException.PROFESSIONAL_NAME_NOT_FOUND, String.format("No se encontro el profesional de la agenda con id ",newAppointmentNotification.diaryId)));
		InstitutionBo institutionBo = institutionService.get(diaryService.getInstitution(newAppointmentNotification.diaryId));
		String address = institutionService.getAddress(institutionBo.getId())
				.map(addressBo -> Stream.of(addressBo.getStreet(),addressBo.getNumber(),addressBo.getFloor(),addressBo.getCity().getDescription()).filter(Objects::nonNull).collect(Collectors.joining(" "))).orElse("Sin direccion");
		String day = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' YYYY").format(Date.valueOf(newAppointmentNotification.dateTypeId));
		var notificationArgs = NewAppointmentNotificationArgs.builder();
		// se resuelven los argumentos que requiere el mensaje a enviar a partir del BO
		notificationArgs
				.professionalFullName(professionalName)
				.address(address)
				.day(day)
				.time(String.format("%s", newAppointmentNotification.hour))
				.institution(institutionBo.getName())
				.recomendation("...")
				.specialty(diaryBo.getSpecialtyName())
				.fromFullName(env.getProperty("app.notification.mail.fromFullname"));
		this.patientNotificationSender.send(
				new PatientRecipient(newAppointmentNotification.patientId),
				new NewAppointmentTemplateInput(
						notificationArgs.build()
				)
		);
	}

}
