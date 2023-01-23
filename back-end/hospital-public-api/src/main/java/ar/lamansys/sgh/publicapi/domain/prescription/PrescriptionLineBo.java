package ar.lamansys.sgh.publicapi.domain.prescription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class PrescriptionLineBo {
	Integer prescriptionLineNumber;
	String prescriptionLineStatus;
	PrescriptionProblemBo prescriptionProblemBo;
	GenericMedicationBo genericMedicationBo;
	CommercialMedicationBo commercialMedicationBo;
	Integer unitDosis;
	Integer dayDosis;
	Double duration;
	String presentation;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PrescriptionLineBo that = (PrescriptionLineBo) o;
		return Objects.equals(prescriptionLineNumber, that.prescriptionLineNumber) && Objects.equals(prescriptionLineStatus, that.prescriptionLineStatus) && Objects.equals(prescriptionProblemBo, that.prescriptionProblemBo) && Objects.equals(genericMedicationBo, that.genericMedicationBo) && Objects.equals(commercialMedicationBo, that.commercialMedicationBo) && Objects.equals(unitDosis, that.unitDosis) && Objects.equals(dayDosis, that.dayDosis) && Objects.equals(duration, that.duration) && Objects.equals(presentation, that.presentation) && Objects.equals(presentationQuantity, that.presentationQuantity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(prescriptionLineNumber, prescriptionLineStatus, prescriptionProblemBo, genericMedicationBo, commercialMedicationBo, unitDosis, dayDosis, duration, presentation, presentationQuantity);
	}

	Integer presentationQuantity;
}
