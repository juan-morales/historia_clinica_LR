package net.pladema.medicalconsultation.appointment.controller.dto;

import ar.lamansys.sgx.shared.dates.controller.dto.DateTimeDto;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAppointmentDateDto {

	@NotNull
	private Integer appointmentId;

	@NotNull
	private DateTimeDto date;

	@NotNull
	private Integer openingHoursId;
}
