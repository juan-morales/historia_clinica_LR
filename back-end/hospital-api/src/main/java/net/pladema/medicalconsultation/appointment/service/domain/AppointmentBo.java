package net.pladema.medicalconsultation.appointment.service.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import ar.lamansys.sgh.clinichistory.domain.ips.DiagnosticReportBo;
import ar.lamansys.sgh.clinichistory.domain.ips.TranscribedDiagnosticReportBo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.medicalconsultation.appointment.domain.enums.EAppointmentModality;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentDiaryVo;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentVo;
import net.pladema.medicalconsultation.appointment.repository.entity.Appointment;
import net.pladema.medicalconsultation.diary.service.domain.ProfessionalPersonBo;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AppointmentBo {

	private Integer id;

	private Integer diaryId;

	private Integer patientId;

	private LocalDate date;

	private LocalTime hour;

	private Short appointmentStateId;

	private boolean overturn;

	private Integer openingHoursId;

	private Integer patientMedicalCoverageId;

	private Short medicalAttentionTypeId;

	private String stateChangeReason;

	private String phonePrefix;

	private String phoneNumber;

	private Integer snomedId;

	private String observation;

	private Integer observationBy;

	private Short appointmentBlockMotiveId;

	private boolean isProtected;

	private LocalDateTime createdOn;

	private ProfessionalPersonBo professionalPersonBo;

	private String patientEmail;

	private String callId;

	private Short modalityId;
	
	private DiagnosticReportBo orderData;

	private TranscribedDiagnosticReportBo transcribedData;

	private String applicantHealthcareProfessionalEmail;

	public static AppointmentBo fromAppointmentDiaryVo(AppointmentDiaryVo appointmentDiaryVo) {
		return AppointmentBo.builder()
				.id(appointmentDiaryVo.getId())
				.diaryId(appointmentDiaryVo.getDiaryId())
				.patientId(appointmentDiaryVo.getPatientId())
				.date(appointmentDiaryVo.getDate())
				.hour(appointmentDiaryVo.getHour())
				.appointmentStateId(appointmentDiaryVo.getAppointmentStateId())
				.overturn(appointmentDiaryVo.isOverturn())
				.patientMedicalCoverageId(appointmentDiaryVo.getPatientMedicalCoverageId())
				.medicalAttentionTypeId(appointmentDiaryVo.getMedicalAttentionTypeId())
				.phonePrefix(appointmentDiaryVo.getPhonePrefix())
				.phoneNumber(appointmentDiaryVo.getPhoneNumber())
				.appointmentBlockMotiveId(appointmentDiaryVo.getAppointmentBlockMotiveId())
				.createdOn(appointmentDiaryVo.getCreatedOn())
				.professionalPersonBo(appointmentDiaryVo.getProfessionalPersonVo() != null ? new ProfessionalPersonBo(appointmentDiaryVo.getProfessionalPersonVo()) : null)
				.build();
	}

	public static AppointmentBo fromAppointmentVo(AppointmentVo appointmentVo) {
		return AppointmentBo.builder()
				.id(appointmentVo.getId())
				.patientId(appointmentVo.getPatientId())
				.date(appointmentVo.getDate())
				.hour(appointmentVo.getHour())
				.appointmentStateId(appointmentVo.getAppointmentStateId())
				.overturn(appointmentVo.isOverturn())
				.patientMedicalCoverageId(appointmentVo.getPatientMedicalCoverageId())
				.medicalAttentionTypeId(appointmentVo.getMedicalAttentionTypeId())
				.stateChangeReason(appointmentVo.getStateChangeReason())
				.diaryId(appointmentVo.getDiaryId())
				.observation(appointmentVo.getObservation())
				.observationBy(appointmentVo.getObservationBy())
				.callId(appointmentVo.getCallId())
				.modalityId(appointmentVo.getModalityId())
				.build();
	}

	public static AppointmentBo newFromAppointment(Appointment appointment) {
		return AppointmentBo.builder()
				.id(appointment.getId())
				.patientId(appointment.getPatientId())
				.date(appointment.getDateTypeId())
				.hour(appointment.getHour())
				.appointmentStateId(appointment.getAppointmentStateId())
				.overturn(appointment.getIsOverturn())
				.patientMedicalCoverageId(appointment.getPatientMedicalCoverageId())
				.phonePrefix(appointment.getPhonePrefix())
				.phoneNumber(appointment.getPhoneNumber())
				.snomedId(appointment.getSnomedId())
				.build();
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AppointmentBo)) return false;
		AppointmentBo that = (AppointmentBo) o;
		return getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
