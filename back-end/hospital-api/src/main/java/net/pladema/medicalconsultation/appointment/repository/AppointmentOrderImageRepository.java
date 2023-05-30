package net.pladema.medicalconsultation.appointment.repository;


import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentOrderImage;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentOrderImagePK;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
public interface AppointmentOrderImageRepository extends JpaRepository<AppointmentOrderImage, AppointmentOrderImagePK> {

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage AS aoi " +
			"SET aoi.completed = :completed " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void updateCompleted(@Param("appointmentId") Integer appointmentId,
						 @Param("completed") Boolean completed);

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage AS aoi " +
			"SET aoi.imageId = :imageId " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void updateImageId(@Param("appointmentId") Integer appointmentId,
					   @Param("imageId") String imageId );

	@Transactional(readOnly = true)
	@Query("SELECT aoi.imageId " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId " +
			"AND aoi.completed = TRUE")
	Optional<String>  getIdImage(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query("SELECT 1 " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.orderId = :orderId")
	Optional<Integer>  findById(@Param("orderId") Integer orderId);

	@Transactional(readOnly = true)
	@Query("SELECT 1 " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId " +
			"AND aoi.completed = TRUE")
	Optional<Integer> isAlreadyCompleted(@Param("appointmentId") Integer appointmentId);

	@Transactional
	@Modifying
	@Query("DELETE " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void deleteByAppointment(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query("SELECT aoi.documentId " +
			"FROM AppointmentOrderImage aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId " )
	Optional<Long> getReportDocumentIdByAppointmentId(@Param("appointmentId") Integer appointmentId);

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage aoi " +
			"SET aoi.documentId = :documentId " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void setReportDocumentId(@Param("appointmentId") Integer appointmentId,
							 @Param("documentId") Long documentId );

}
