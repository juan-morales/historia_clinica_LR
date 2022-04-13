package net.pladema.medicalconsultation.appointment.infraestructure.output.notification;

import ar.lamansys.mqtt.infraestructure.input.rest.dto.MqttMetadataDto;
import ar.lamansys.mqtt.infraestructure.input.service.MqttCallExternalService;
import lombok.AllArgsConstructor;
import net.pladema.events.HospitalApiPublisher;
import net.pladema.medicalconsultation.appointment.service.domain.notifypatient.NotifyPatientBo;
import net.pladema.medicalconsultation.appointment.service.domain.notifypatient.SendAppointmentNotification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SendAppointmentNotificationImpl implements SendAppointmentNotification {

    private final MqttCallExternalService mqttCallExternalService;

	private final HospitalApiPublisher hospitalApiPublisher;

    @Override
    public void run(NotifyPatientBo notifyPatientBo) {
        mqttCallExternalService.publish(
                mapTo(notifyPatientBo)
        );
    }

    private MqttMetadataDto mapTo(NotifyPatientBo notifyPatientBo) {
        return new MqttMetadataDto(
                notifyPatientBo.getTopic(),
                getMessage(notifyPatientBo),
                true,
                2,
                "add"
        );
    }

    protected String getMessage(NotifyPatientBo notifyPatientBo) {
            return String.format(
                    "\"data\":{\"appointmentId\":%s,\"patient\":\"%s\",\"sector\":%s,\"doctor\":\"%s\",\"doctorsOffice\":\"%s\"}",
                    notifyPatientBo.getAppointmentId(),
                    notifyPatientBo.getPatientName(),
                    notifyPatientBo.getSectorId(),
                    notifyPatientBo.getDoctorName(),
                    notifyPatientBo.getDoctorsOfficeName()
            );
    }
}
