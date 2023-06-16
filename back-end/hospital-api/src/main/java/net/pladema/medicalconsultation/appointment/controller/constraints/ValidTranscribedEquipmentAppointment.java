package net.pladema.medicalconsultation.appointment.controller.constraints;

import net.pladema.medicalconsultation.appointment.controller.constraints.validator.TranscribedEquipmentAppointmentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TranscribedEquipmentAppointmentValidator.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTranscribedEquipmentAppointment {

    String message() default "{appointment.error.validation}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
