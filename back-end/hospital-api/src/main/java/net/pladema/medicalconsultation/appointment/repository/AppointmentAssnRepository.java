package net.pladema.medicalconsultation.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentAssn;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentAssnPK;

@Repository
public interface AppointmentAssnRepository extends JpaRepository<AppointmentAssn, AppointmentAssnPK> {
	
    @Transactional
    @Modifying
    @Query("UPDATE AppointmentAssn AS aassn " +
            "SET aassn.pk.openingHoursId = :openingHoursId " +
            "WHERE aassn.pk.appointmentId = :appointmentId")
    void updateOpeningHoursId(@Param("openingHoursId") Integer openingHoursId,
                                   @Param("appointmentId") Integer appointmentId);

}
