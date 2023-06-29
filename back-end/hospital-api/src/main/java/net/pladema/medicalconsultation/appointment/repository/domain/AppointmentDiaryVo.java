package net.pladema.medicalconsultation.appointment.repository.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.ToString;
import lombok.Value;
import net.pladema.emergencycare.repository.domain.ProfessionalPersonVo;

@Value
@ToString
public class AppointmentDiaryVo {

	private final Integer id;

	private final Integer patientId;

	private final Integer diaryId;

	private final LocalDate date;

	private final LocalTime hour;

	private final Short appointmentStateId;

	private final boolean overturn;

	private final Integer patientMedicalCoverageId;

	private final Short medicalAttentionTypeId;

	private final String phonePrefix;

	private final String phoneNumber;

	private final Short appointmentBlockMotiveId;

	private final LocalDateTime updatedOn;

	private final LocalDateTime createdOn;

	private final ProfessionalPersonVo professionalPersonVo;
	
	public AppointmentDiaryVo(
			Integer diaryId,
			Integer id,
			Integer patientId,
			LocalDate date,
			LocalTime hour,
			Short appointmentStateId,
			boolean overturn,
			Integer patientMedicalCoverageId,
			String phonePrefix,
			String phoneNumber,
			Short medicalAttentionTypeId,
			Short appointmentBlockMotiveId,
			LocalDateTime updatedOn,
			LocalDateTime createdOn,
			Integer personId,
			String firstName,
			String lastName,
			String otherLastName
	) {
		this.diaryId = diaryId;
		this.id = id;
		this.patientId = patientId;
		this.date = date;
		this.hour = hour;
		this.appointmentStateId = appointmentStateId;
		this.overturn = overturn;
		this.patientMedicalCoverageId = patientMedicalCoverageId;
		this.phonePrefix = phonePrefix;
		this.phoneNumber = phoneNumber;
		this.medicalAttentionTypeId = medicalAttentionTypeId;
		this.appointmentBlockMotiveId = appointmentBlockMotiveId;
		this.updatedOn = updatedOn;
		this.createdOn = createdOn;
		this.professionalPersonVo = personId != null ? new ProfessionalPersonVo(personId, firstName, lastName, otherLastName) : null;
	}

}
