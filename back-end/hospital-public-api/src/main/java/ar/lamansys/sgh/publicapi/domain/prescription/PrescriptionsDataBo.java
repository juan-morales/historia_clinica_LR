package ar.lamansys.sgh.publicapi.domain.prescription;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class PrescriptionsDataBo {
	private String domain;
	private String prescriptionId;
	private LocalDateTime prescriptionDate;
	private LocalDateTime dueDate;

	private String link;

	private ProfessionalPrescriptionBo professionalData;

	private PrescriptionSpecialtyBo prescriptionSpecialty;

	public PrescriptionsDataBo(String domain,
                               String prescriptionId,
                               LocalDateTime prescriptionDate,
                               LocalDateTime dueDate,
                               String link,
                               ProfessionalPrescriptionBo professionalPrescriptionBo,
							   PrescriptionSpecialtyBo prescriptionSpecialtyBo) {
		this.domain = domain;
		this.prescriptionDate = prescriptionDate;
		this.prescriptionId = prescriptionId;
		this.dueDate = dueDate;
		this.link = link;
		this.professionalData = professionalPrescriptionBo;
		this.prescriptionSpecialty = prescriptionSpecialtyBo;
	}
}
