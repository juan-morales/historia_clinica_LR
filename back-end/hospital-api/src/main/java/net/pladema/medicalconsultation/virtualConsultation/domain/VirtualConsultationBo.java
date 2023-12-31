package net.pladema.medicalconsultation.virtualConsultation.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualConsultationBo {

	private Integer id;

	private Integer patientId;

	private String patientName;

	private String patientSelfPerceivedName;

	private String patientLastName;

	private Integer patientAge;

	private String patientGender;

	private String problem;

	private String motive;

	private String clinicalSpecialty;

	private String careLine;

	private Integer institutionId;

	private String institutionName;

	private Short statusId;

	private String responsibleFirstName;

	private String responsibleLastName;

	private Integer responsibleHealthcareProfessionalId;

	private Boolean responsibleAvailability;

	private Short priorityId;

	private LocalDateTime creationDateTime;

	private String callId;

	private Integer availableProfessionalsAmount;

	public VirtualConsultationBo(Integer id, Integer patientId, String patientName, String patientSelfPerceivedName, String patientLastName, Integer patientAge,
								 String patientGender, String problem, String motive, String clinicalSpecialty, String careLine, Integer institutionId, Short statusId,
								 String responsibleFirstName, String responsibleLastName, Integer responsibleHealthcareProfessionalId, Boolean responsibleAvailability,
								 Short priorityId, LocalDateTime creationDateTime, Integer availableProfessionalsAmount) {
		this.id = id;
		this.patientId = patientId;
		this.patientName = patientName;
		this.patientSelfPerceivedName = patientSelfPerceivedName;
		this.patientLastName = patientLastName;
		this.patientAge = patientAge;
		this.patientGender = patientGender;
		this.problem = problem;
		this.motive = motive;
		this.clinicalSpecialty = clinicalSpecialty;
		this.careLine = careLine;
		this.institutionId = institutionId;
		this.statusId = statusId;
		this.responsibleFirstName = responsibleFirstName;
		this.responsibleLastName = responsibleLastName;
		this.responsibleHealthcareProfessionalId = responsibleHealthcareProfessionalId;
		this.responsibleAvailability = responsibleAvailability;
		this.priorityId = priorityId;
		this.creationDateTime = creationDateTime;
		this.availableProfessionalsAmount = availableProfessionalsAmount;
	}
}
