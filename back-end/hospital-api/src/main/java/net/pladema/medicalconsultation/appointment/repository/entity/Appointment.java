package net.pladema.medicalconsultation.appointment.repository.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import ar.lamansys.sgx.shared.auditable.entity.SGXAuditableEntity;
import ar.lamansys.sgx.shared.auditable.listener.SGXAuditListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentBo;

@Entity
@Table(name = "appointment")
@EntityListeners(SGXAuditListener.class)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Appointment extends SGXAuditableEntity<Integer> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_type_id", nullable = false)
    private LocalDate dateTypeId;

    @Column(name = "hour", nullable = false)
    private LocalTime hour;

    @Column(name = "appointment_state_id", nullable = false)
    private Short appointmentStateId;

    @Column(name = "is_overturn", nullable = false)
    @ColumnDefault("false")
    private Boolean isOverturn;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "patient_medical_coverage_id")
    private Integer patientMedicalCoverageId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

	@Column(name = "phone_prefix", length = 10)
	private String phonePrefix;

    public static Appointment newFromAppointmentBo(AppointmentBo appointmentBo) {
        Appointment result = new Appointment();
        result.setDateTypeId(appointmentBo.getDate());
        result.setHour(appointmentBo.getHour());
        result.setIsOverturn(appointmentBo.isOverturn());
        result.setPatientId(appointmentBo.getPatientId());
        result.setAppointmentStateId(AppointmentState.ASSIGNED);
        result.setPatientMedicalCoverageId(appointmentBo.getPatientMedicalCoverageId());
		result.setPhonePrefix(appointmentBo.getPhonePrefix());
        result.setPhoneNumber(appointmentBo.getPhoneNumber());
        return result;
        
    }

    public boolean isAssigned(){
        return Short.valueOf(AppointmentState.ASSIGNED).equals(appointmentStateId);
    }
}
