package net.pladema.medicalconsultation.appointment.repository;


import java.util.List;
import java.util.Optional;

import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentOrderImageExistCheckVo;

import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentState;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentOrderImage;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentOrderImagePK;

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

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage AS aoi " +
			"SET aoi.destInstitutionId = :destInstitutionId " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void updateDestInstitution(@Param("destInstitutionId") Integer destInstitutionId,
							   @Param("appointmentId") Integer appointmentId );

	@Transactional(readOnly = true)
	@Query("SELECT aoi.imageId " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId " +
			"AND aoi.completed = TRUE")
	Optional<String>  getIdImage(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query("SELECT aoi.studyId " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId ")
	Optional<Integer> getStudyId(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query("SELECT new net.pladema.medicalconsultation.appointment.repository.domain.AppointmentOrderImageExistCheckVo(aoi.pk.appointmentId, d.statusId, aoi.studyId) " +
			"FROM AppointmentOrderImage AS aoi "+
			"JOIN Appointment AS app ON (aoi.pk.appointmentId = app.id) "+
			"LEFT JOIN Document as d ON (aoi.documentId = d.id) "+
			"WHERE aoi.studyId = :orderId "+
			"AND (aoi.active = true " +
			"OR app.appointmentStateId = "+ AppointmentState.SERVED +" )")
	List<AppointmentOrderImageExistCheckVo> findAppointmentIdAndReportByOrderId(@Param("orderId") Integer orderId);

	@Transactional(readOnly = true)
	@Query("SELECT 1 " +
			"FROM AppointmentOrderImage AS aoi " +
			"WHERE aoi.pk.appointmentId = :appointmentId " +
			"AND aoi.completed = TRUE")
	Optional<Integer> isAlreadyCompleted(@Param("appointmentId") Integer appointmentId);

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage aoi " +
			"SET aoi.active = false " +
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

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage AS aoi " +
			"SET aoi.reportStatusId = :reportStatusId " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void updateReportStatusId(@Param("appointmentId") Integer appointmentId,
							   @Param("reportStatusId") Short reportStatusId);

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage AS aoi " +
			"SET aoi.orderId = :orderId, aoi.transcribedOrderId = NULL, aoi.studyId = :studyId " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void updateOrderId(@Param("appointmentId") Integer appointmentId,
					   @Param("studyId") Integer studyId,
					   @Param("orderId") Integer orderId );

	@Transactional
	@Modifying
	@Query("UPDATE AppointmentOrderImage AS aoi " +
			"SET aoi.transcribedOrderId = :orderId, aoi.orderId = NULL, aoi.studyId = NULL " +
			"WHERE aoi.pk.appointmentId = :appointmentId")
	void updateTranscribedOrderId(@Param("appointmentId") Integer appointmentId,
					   @Param("orderId") Integer orderId );
}
