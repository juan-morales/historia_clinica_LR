package net.pladema.medicalconsultation.appointment.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import net.pladema.clinichistory.requests.servicerequests.domain.StudyAppointmentBo;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentStatus;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentType;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.SourceType;
import net.pladema.clinichistory.requests.servicerequests.domain.WorklistBo;
import net.pladema.establishment.repository.entity.HierarchicalUnit;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentEquipmentShortSummaryBo;
import ar.lamansys.sgx.shared.migratable.SGXDocumentEntityRepository;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentShortSummaryBo;

import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentTicketImageBo;

import net.pladema.medicalconsultation.appointment.repository.domain.MedicalCoverageAppoinmentOrderBo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ar.lamansys.sgx.shared.auditable.repository.SGXAuditableEntityJPARepository;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentAssignedForPatientVo;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentDiaryVo;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentTicketBo;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentVo;
import net.pladema.medicalconsultation.appointment.repository.domain.NotifyPatientVo;
import net.pladema.medicalconsultation.appointment.repository.entity.Appointment;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentState;

@Repository
public interface AppointmentRepository extends SGXAuditableEntityJPARepository<Appointment, Integer>, SGXDocumentEntityRepository<Appointment> {

    @Transactional(readOnly = true)
    @Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentVo(aa.pk.diaryId, a, doh.medicalAttentionTypeId, has.reason, ao.observation, ao.createdBy)" +
            "FROM Appointment AS a " +
            "JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"LEFT JOIN AppointmentObservation AS ao ON (a.id = ao.appointmentId) " +
            "LEFT JOIN HistoricAppointmentState AS has ON (a.id = has.pk.appointmentId) " +
            "JOIN Diary d ON (d.id = aa.pk.diaryId )" +
			"JOIN DiaryOpeningHours  AS doh ON (doh.pk.diaryId = d.id) " +
            "WHERE a.id = :appointmentId " +
			"AND doh.pk.openingHoursId = aa.pk.openingHoursId " +
			"AND a.deleteable.deleted = FALSE " +
            "AND ( has.pk.changedStateDate IS NULL OR has.pk.changedStateDate = " +
            "   ( SELECT MAX (subHas.pk.changedStateDate) FROM HistoricAppointmentState subHas WHERE subHas.pk.appointmentId = a.id) ) " +
			"ORDER BY has.pk.changedStateDate DESC")
    List<AppointmentVo> getAppointment(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentVo(aa.pk.diaryId, a, doh.medicalAttentionTypeId, ao.observation, ao.createdBy)" +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"LEFT JOIN AppointmentObservation AS ao ON (a.id = ao.appointmentId) " +
			"JOIN DiaryOpeningHours  AS doh ON (doh.pk.diaryId = aa.pk.diaryId AND doh.pk.openingHoursId = aa.pk.openingHoursId) " +
			"WHERE a.id = :appointmentId " +
			"AND a.deleteable.deleted = FALSE")
	List<AppointmentVo> getAppointmentSummary(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentVo(eaa.pk.equipmentDiaryId, a, edoh.medicalAttentionTypeId, has.reason, ao.observation, ao.createdBy)" +
			"FROM Appointment AS a " +
			"JOIN EquipmentAppointmentAssn AS eaa ON (a.id = eaa.pk.appointmentId) " +
			"LEFT JOIN AppointmentObservation AS ao ON (a.id = ao.appointmentId) " +
			"LEFT JOIN HistoricAppointmentState AS has ON (a.id = has.pk.appointmentId) " +
			"JOIN EquipmentDiary ed ON (ed.id = eaa.pk.equipmentDiaryId )" +
			"JOIN EquipmentDiaryOpeningHours  AS edoh ON (edoh.pk.equipmentDiaryId = ed.id) " +
			"WHERE a.id = :appointmentId " +
			"AND edoh.pk.openingHoursId = eaa.pk.openingHoursId " +
			"AND a.deleteable.deleted = FALSE OR a.deleteable.deleted IS NULL " +
			"AND ( has.pk.changedStateDate IS NULL OR has.pk.changedStateDate = " +
			"   ( SELECT MAX (subHas.pk.changedStateDate) FROM HistoricAppointmentState subHas WHERE subHas.pk.appointmentId = a.id) ) " +
			"ORDER BY has.pk.changedStateDate DESC")
	List<AppointmentVo> getEquipmentAppointment(@Param("appointmentId") Integer appointmentId);

    @Transactional(readOnly = true)
    @Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentDiaryVo(" +
            "aa.pk.diaryId, a.id, a.patientId, a.dateTypeId, a.hour, a.appointmentStateId, a.isOverturn, " +
            "a.patientMedicalCoverageId,a.phonePrefix, a.phoneNumber, doh.medicalAttentionTypeId, " +
			"a.appointmentBlockMotiveId, a.updateable.updatedOn, a.creationable.createdOn, p.id, p.firstName, p.lastName, pex.nameSelfDetermination, p.middleNames, p.otherLastNames) " +
            "FROM Appointment AS a " +
            "JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
            "JOIN DiaryOpeningHours  AS doh ON (doh.pk.diaryId = aa.pk.diaryId) " +
			"JOIN UserPerson us ON (a.creationable.createdBy = us.pk.userId) " +
			"JOIN Person p ON (us.pk.personId = p.id) " +
			"JOIN PersonExtended pex ON (p.id = pex.id) " +
            "WHERE aa.pk.diaryId = :diaryId AND a.appointmentStateId <> " + AppointmentState.CANCELLED_STR +
			"AND aa.pk.openingHoursId = doh.pk.openingHoursId " +
			"AND (a.deleteable.deleted = FALSE OR a.deleteable.deleted IS NULL ) " +
            " AND a.dateTypeId >= CURRENT_DATE ")
    List<AppointmentDiaryVo> getFutureActiveAppointmentsByDiary(@Param("diaryId") Integer diaryId);

	@Transactional(readOnly = true)
	@Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentDiaryVo(" +
			"eaa.pk.equipmentDiaryId, a.id, a.patientId, a.dateTypeId, a.hour, a.appointmentStateId, a.isOverturn, " +
			"a.patientMedicalCoverageId,a.phonePrefix, a.phoneNumber, edoh.medicalAttentionTypeId, " +
			"a.appointmentBlockMotiveId, a.updateable.updatedOn, a.creationable.createdOn, p.id, p.firstName, p.lastName, pex.nameSelfDetermination, p.middleNames, p.otherLastNames) " +
			"FROM Appointment AS a " +
			"JOIN EquipmentAppointmentAssn AS eaa ON (a.id = eaa.pk.appointmentId) " +
			"JOIN EquipmentDiaryOpeningHours AS edoh ON (edoh.pk.equipmentDiaryId = eaa.pk.equipmentDiaryId) " +
			"JOIN UserPerson us ON (a.creationable.createdBy = us.pk.userId) " +
			"JOIN Person p ON (us.pk.personId = p.id) " +
			"JOIN PersonExtended pex ON (p.id = pex.id) " +
			"WHERE eaa.pk.equipmentDiaryId = :diaryId AND a.appointmentStateId <> " + AppointmentState.CANCELLED_STR +
			"AND eaa.pk.openingHoursId = edoh.pk.openingHoursId " +
			"AND (a.deleteable.deleted = FALSE OR a.deleteable.deleted IS NULL ) " +
			" AND a.dateTypeId >= CURRENT_DATE ")
	List<AppointmentDiaryVo> getFutureActiveAppointmentsByEquipmentDiary(@Param("diaryId") Integer diaryId);

	@Transactional(readOnly = true)
	@Query( "SELECT (case when count(a.id)> 0 then true else false end) " +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"WHERE aa.pk.diaryId = :diaryId " +
			"AND aa.pk.openingHoursId = :openingHoursId " +
			"AND a.dateTypeId = :date " +
			"AND a.hour = :hour " +
			"AND NOT a.appointmentStateId = " + AppointmentState.CANCELLED_STR +
			"AND (a.deleteable.deleted = false OR a.deleteable.deleted is null )")
	boolean existAppointment(@Param("diaryId") Integer diaryId, @Param("openingHoursId") Integer openingHoursId,
							 @Param("date") LocalDate date, @Param("hour") LocalTime hour);

	@Transactional(readOnly = true)
	@Query( "SELECT (case when count(a.id)> 0 then true else false end) " +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"WHERE aa.pk.diaryId = :diaryId " +
			"AND a.dateTypeId = :date " +
			"AND a.hour = :hour " +
			"AND NOT a.appointmentStateId = " + AppointmentState.CANCELLED_STR +
			"AND (a.deleteable.deleted = false OR a.deleteable.deleted is null )")
	boolean existAppointment(@Param("diaryId") Integer diaryId,
							 @Param("date") LocalDate date, @Param("hour") LocalTime hour);

    @Transactional
    @Modifying
    @Query( "UPDATE Appointment  AS a " +
            "SET a.appointmentStateId = :appointmentStateId, " +
            "a.updateable.updatedOn = CURRENT_TIMESTAMP, " +
            "a.updateable.updatedBy = :userId " +
            "WHERE a.id = :appointmentId ")
    void updateState(@Param("appointmentId") Integer appointmentId,
                     @Param("appointmentStateId") short appointmentStateId,
                     @Param("userId") Integer userId);

    @Transactional(readOnly = true)
    @Query( "SELECT DISTINCT a.id, a.hour " +
            "FROM Appointment AS a " +
            "JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
            "JOIN Diary AS d ON (d.id = aa.pk.diaryId) " +
			"LEFT JOIN DiaryAssociatedProfessional AS dap ON (dap.diaryId = d.id) " +
            "WHERE a.patientId = :patientId " +
            "AND (d.healthcareProfessionalId = :healthProfessionalId " +
			"OR dap.healthcareProfessionalId = :healthProfessionalId) " +
            "AND a.dateTypeId = :appointmentDate " +
            "AND ( a.appointmentStateId = " + AppointmentState.CONFIRMED + " " +
			"OR a.appointmentStateId = " + AppointmentState.ASSIGNED + ") " +
            "ORDER BY a.hour ASC")
    List<Integer> getAppointmentsId(@Param("patientId") Integer patientId,
                                    @Param("healthProfessionalId")  Integer healthProfessionalId,
                                    @Param("appointmentDate")  LocalDate appointmentDate);

	@Transactional(readOnly = true)
	@Query( "SELECT DISTINCT a.id, a.hour " +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"JOIN Diary AS d ON (d.id = aa.pk.diaryId) " +
			"LEFT JOIN DiaryAssociatedProfessional AS dap ON (dap.diaryId = d.id) " +
			"WHERE a.patientId = :patientId " +
			"AND (d.healthcareProfessionalId = :healthProfessionalId " +
			"OR dap.healthcareProfessionalId = :healthProfessionalId) " +
			"AND a.appointmentStateId = " + AppointmentState.CONFIRMED + " " +
			"AND a.dateTypeId < CURRENT_DATE " +
			"ORDER BY a.hour DESC")
	List<Integer> getOldAppointmentsId(@Param("patientId") Integer patientId,
									@Param("healthProfessionalId")  Integer healthProfessionalId);


    @Transactional
    @Modifying
    @Query( "UPDATE Appointment  AS a " +
            "SET a.phoneNumber = :phoneNumber, " +
			"a.phonePrefix = :phonePrefix, " +
            "a.updateable.updatedOn = CURRENT_TIMESTAMP, " +
            "a.updateable.updatedBy = :userId " +
            "WHERE a.id = :appointmentId ")
    void updatePhoneNumber(@Param("appointmentId") Integer appointmentId,
						   @Param("phonePrefix") String phonePrefix,
                           @Param("phoneNumber") String phoneNumber,
                           @Param("userId") Integer userId);

	@Transactional
	@Modifying
	@Query( "UPDATE Appointment  AS a " +
			"SET a.dateTypeId = :date, " +
			"a.hour = :hour " +
			"WHERE a.id = :appointmentId ")
	void updateDate(@Param("appointmentId") Integer appointmentId,
						   @Param("date") LocalDate date,
						   @Param("hour") LocalTime hour);

	@Transactional
	@Modifying
	@Query( "UPDATE AppointmentObservation  AS ao " +
			"SET ao.observation = :observation, " +
			"ao.createdBy = :observationBy " +
			"WHERE ao.appointmentId = :appointmentId ")
	void updateObservation(@Param("appointmentId") Integer appointmentId,
						   @Param("observation") String observation,
						   @Param("observationBy") Integer observationBy);

	@Transactional
	@Modifying
	@Query( "UPDATE Appointment AS a " +
			"SET a.patientMedicalCoverageId = :newPatientMedicalCoverageId " +
			"WHERE a.patientId = :patientId " +
			"AND a.patientMedicalCoverageId = :patientMedicalCoverageId " +
			"AND (a.dateTypeId > :appointmentDate " +
			"OR (a.dateTypeId = :appointmentDate " +
			"AND a.hour >= :hour ))" +
			"AND (a.appointmentStateId = " + AppointmentState.ASSIGNED + " " +
			"OR a.appointmentStateId = " + AppointmentState.CONFIRMED + ")" )
	void updateAppointmentsIdByPatientMedicalCoverage(@Param("patientId") Integer patientId,
													  @Param("patientMedicalCoverageId") Integer patientMedicalCoverageId,
													  @Param("appointmentDate") LocalDate appointmentDate,
													  @Param("hour") LocalTime hour,
													  @Param("newPatientMedicalCoverageId") Integer newPatientMedicalCoverageId);

    @Transactional(readOnly = true)
    @Query(name = "Appointment.medicalCoverage")
    List<Integer> getMedicalCoverage(@Param("patientId") Integer patientId,
                                     @Param("currentDate") LocalDate currentDate,
                                     @Param("confirmedAppointmentState") Short confirmedAppointmentState,
									 @Param("assignedAppointmentState") Short assignedAppointmentState,
                                     @Param("professionalId") Integer professionalId);

	@Transactional(readOnly = true)
	@Query("SELECT a.patientMedicalCoverageId " +
			"FROM Appointment AS a " +
			"WHERE a.id = :appointmentId ")
	Optional<Integer> getAppointmentMedicalCoverageId(@Param("appointmentId") Integer appointmentId);

    @Transactional(readOnly = true)
    @Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.NotifyPatientVo(a.id, pp.lastName, pp.firstName, do.sectorId, php.lastName,php.firstName, do.description, do.topic)" +
            "FROM Appointment AS a " +
            "JOIN Patient AS p ON (p.id = a.patientId) " +
            "JOIN Person AS pp ON (pp.id = p.personId) " +
            "JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
            "JOIN Diary d ON (d.id = aa.pk.diaryId) " +
            "JOIN DoctorsOffice AS do ON (do.id = d.doctorsOfficeId) " +
            "JOIN HealthcareProfessional AS hp ON (d.healthcareProfessionalId = hp.id)" +
            "JOIN Person AS php ON (php.id = hp.personId)" +
            "WHERE a.id = :appointmentId ")
    Optional<NotifyPatientVo> getNotificationData(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query( "SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentAssignedForPatientVo(" +
			"hp.licenseNumber, up.pk.userId, a.dateTypeId, a.hour, do.description)" +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"JOIN Diary d ON (d.id = aa.pk.diaryId )" +
			"JOIN HealthcareProfessional  AS hp ON (hp.id = d.healthcareProfessionalId) " +
			"JOIN UserPerson AS up ON (up.pk.personId = hp.personId )" +
			"JOIN DoctorsOffice do ON (do.id = d.doctorsOfficeId )" +
			"WHERE a.patientId = :patientId AND d.deleteable.deleted = false " +
			"AND a.dateTypeId BETWEEN :minDate AND :maxDate " +
			"AND a.appointmentStateId = " + AppointmentState.ASSIGNED)
	List<AppointmentAssignedForPatientVo> getAssignedAppointmentsByPatient(@Param("patientId") Integer patientId,
																		   @Param("minDate") LocalDate minDate,
																		   @Param("maxDate") LocalDate maxDate);

	@Transactional(readOnly = true)
	@Query( "SELECT a.id " +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"JOIN Diary AS d ON (d.id = aa.pk.diaryId )" +
			"JOIN DoctorsOffice AS do ON (do.id = d.doctorsOfficeId ) " +
			"JOIN HealthcareProfessional AS hcp ON(d.healthcareProfessionalId = hcp.id) " +
			"JOIN UserPerson AS up ON(hcp.personId = up.pk.personId) " +
			"WHERE a.patientId = :patientId " +
			"AND do.institutionId = :institutionId " +
			"AND up.pk.userId = :userId " +
			"AND ( a.appointmentStateId = " + AppointmentState.CONFIRMED + " " +
			"OR a.appointmentStateId = " + AppointmentState.ASSIGNED + ") " +
			"AND a.dateTypeId = :currentDate " +
			"ORDER BY a.dateTypeId, a.hour ASC ")
	List<Integer> getCurrentAppointmentsByPatient(@Param("patientId") Integer patientId,
												  @Param("institutionId") Integer institutionId,
												  @Param("userId") Integer userId,
												  @Param("currentDate") LocalDate currentDate);

	@Transactional(readOnly = true)
	@Query( "SELECT a " +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
			"WHERE aa.pk.diaryId = :diaryId " +
			"AND a.dateTypeId = :date " +
			"AND a.hour = :hour " +
			"AND a.appointmentStateId = " + AppointmentState.BLOCKED +
			"AND (a.deleteable.deleted = false OR a.deleteable.deleted is null)")
	List<Appointment> findBlockedAppointmentBy(@Param("diaryId") Integer diaryId,
											   @Param("date") LocalDate date, @Param("hour") LocalTime hour);

	@Transactional(readOnly = true)
	@Query(	"SELECT DISTINCT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentTicketImageBo(" +
			"i.name, per.identificationNumber, per.lastName, per.otherLastNames, per.firstName, per.middleNames, " +
			"pex.nameSelfDetermination, mc.name, hi.acronym, a.dateTypeId, a.hour, s.description, sn.pt) " +
			"FROM Appointment a " +
			"JOIN EquipmentAppointmentAssn ea ON(a.id = ea.pk.appointmentId) " +
			"JOIN EquipmentDiary d ON(d.id = ea.pk.equipmentDiaryId) " +
			"JOIN Patient p ON(p.id = a.patientId) " +
			"JOIN Person per ON(per.id = p.personId) " +
			"JOIN PersonExtended pex ON(per.id = pex.id) " +
			"LEFT JOIN PatientMedicalCoverageAssn pmc ON(pmc.id = a.patientMedicalCoverageId) " +
			"LEFT JOIN MedicalCoverage mc ON(pmc.medicalCoverageId = mc.id) " +
			"LEFT JOIN HealthInsurance hi ON(hi.id = mc.id) " +
			"JOIN AppointmentOrderImage aoi ON(aoi.pk.appointmentId = a.id)" +
			"LEFT JOIN DiagnosticReport dr ON(aoi.studyId = dr.id)" +
			"LEFT JOIN Snomed sn ON(dr.snomedId = sn.id) " +
			"JOIN Equipment e ON(d.equipmentId = e.id) " +
			"JOIN Sector s ON(s.id = e.sectorId) " +
			"JOIN Institution i On(s.institutionId = i.id) " +
			"WHERE a.id = :appointmentId ")
	Optional<AppointmentTicketImageBo> getAppointmentImageTicketData(@Param("appointmentId") Integer appointmentId);


	@Transactional(readOnly = true)
	@Query(	"SELECT DISTINCT NEW net.pladema.medicalconsultation.appointment.repository.domain.MedicalCoverageAppoinmentOrderBo(" +
			"mc.name, hi.acronym) " +
			"FROM AppointmentOrderImage aoi " +
			"LEFT JOIN ServiceRequest sr ON(aoi.orderId = sr.id) " +
			"LEFT JOIN PatientMedicalCoverageAssn pmc ON(sr.medicalCoverageId = pmc.id) " +
			"LEFT JOIN MedicalCoverage mc ON(pmc.medicalCoverageId = mc.id) " +
			"LEFT JOIN HealthInsurance hi ON(hi.id = mc.id) " +
			"WHERE aoi.pk.appointmentId = :appointmentId ")
	Optional<MedicalCoverageAppoinmentOrderBo> getMedicalCoverageOrderByAppointment(@Param("appointmentId") Integer appointmentId);


	@Transactional(readOnly = true)
	@Query(	"SELECT DISTINCT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentTicketImageBo(" +
			"i.name, per.identificationNumber, per.lastName, per.otherLastNames, per.firstName, per.middleNames, " +
			"pex.nameSelfDetermination, mc.name, hi.acronym, a.dateTypeId, a.hour, s.description, sn.pt) " +
			"FROM Appointment a " +
			"JOIN EquipmentAppointmentAssn ea ON(a.id = ea.pk.appointmentId) " +
			"JOIN EquipmentDiary d ON(d.id = ea.pk.equipmentDiaryId) " +
			"JOIN Patient p ON(p.id = a.patientId) " +
			"JOIN Person per ON(per.id = p.personId) " +
			"JOIN PersonExtended pex ON(per.id = pex.id) " +
			"LEFT JOIN PatientMedicalCoverageAssn pmc ON(pmc.id = a.patientMedicalCoverageId) " +
			"LEFT JOIN MedicalCoverage mc ON(pmc.medicalCoverageId = mc.id) " +
			"LEFT JOIN HealthInsurance hi ON(hi.id = mc.id) " +
			"JOIN AppointmentOrderImage aoi ON(aoi.pk.appointmentId = a.id)" +
			"JOIN TranscribedServiceRequest ts ON(aoi.transcribedOrderId = ts.id)" +
			"LEFT JOIN DiagnosticReport dr ON(ts.studyId = dr.id)" +
			"LEFT JOIN Snomed sn ON(dr.snomedId = sn.id) " +
			"JOIN Equipment e ON(d.equipmentId = e.id) " +
			"JOIN Sector s ON(s.id = e.sectorId) " +
			"JOIN Institution i On(s.institutionId = i.id) " +
			"WHERE a.id = :appointmentId ")
	Optional<AppointmentTicketImageBo> getAppointmentImageTranscribedTicketData(@Param("appointmentId") Integer appointmentId);



	@Transactional(readOnly = true)
	@Query(	"SELECT DISTINCT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentTicketBo(" +
			"i.name, per.identificationNumber, per.lastName, per.otherLastNames, per.firstName, per.middleNames, " +
			"pex.nameSelfDetermination, mc.name, hi.acronym, a.dateTypeId, a.hour, do2.description, per2.lastName, " +
			"per2.otherLastNames, per2.firstName, per2.middleNames, pex2.nameSelfDetermination) " +
			"FROM Appointment a " +
			"JOIN AppointmentAssn aa ON(a.id = aa.pk.appointmentId) " +
			"JOIN Diary d ON(d.id = aa.pk.diaryId) " +
			"JOIN Patient p ON(p.id = a.patientId) " +
			"JOIN Person per ON(per.id = p.personId) " +
			"JOIN PersonExtended pex ON(per.id = pex.id) " +
			"LEFT JOIN PatientMedicalCoverageAssn pmc ON(pmc.id = a.patientMedicalCoverageId) " +
			"LEFT JOIN MedicalCoverage mc ON(pmc.medicalCoverageId = mc.id) " +
			"LEFT JOIN HealthInsurance hi ON(hi.id = mc.id) " +
			"JOIN DoctorsOffice do2 ON(d.doctorsOfficeId = do2.id) " +
			"JOIN Institution i On(do2.institutionId = i.id) " +
			"JOIN HealthcareProfessional hp ON(d.healthcareProfessionalId = hp.id) " +
			"JOIN Person per2 ON(hp.personId = per2.id) " +
			"JOIN PersonExtended pex2 ON(per2.id = pex2.id) " +
			"WHERE a.id = :appointmentId ")
	Optional<AppointmentTicketBo> getAppointmentTicketData(@Param("appointmentId") Integer appointmentId);




	@Transactional(readOnly = true)
	@Query("SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentShortSummaryBo(" +
			"i.name, a.dateTypeId, a.hour, per.lastName, per.otherLastNames, per.firstName, per.middleNames, pe.nameSelfDetermination) " +
			"FROM Appointment a " +
			"JOIN AppointmentAssn aa ON(a.id = aa.pk.appointmentId) " +
			"JOIN Diary d ON(d.id = aa.pk.diaryId) " +
			"JOIN Patient p ON(p.id = a.patientId) " +
			"JOIN DoctorsOffice do2 ON(d.doctorsOfficeId = do2.id) " +
			"JOIN Institution i On(do2.institutionId = i.id) " +
			"JOIN HealthcareProfessional hp ON(d.healthcareProfessionalId = hp.id) " +
			"JOIN Person per ON (hp.personId = per.id) " +
			"JOIN PersonExtended pe ON (per.id = pe.id) " +
			"WHERE a.patientId = :patientId " +
			"AND a.dateTypeId = :date " +
			"AND a.appointmentStateId NOT IN (" + AppointmentState.CANCELLED_STR + "," + AppointmentState.SERVED + "," + AppointmentState.ABSENT + ") " +
			"AND (a.deleteable.deleted = false OR a.deleteable.deleted is null ) " +
			"ORDER BY a.hour asc")
	List<AppointmentShortSummaryBo> getAppointmentFromDeterminatedDate(@Param("patientId") Integer patientId,
																				 @Param("date") LocalDate date);
	@Override
	@Transactional(readOnly = true)
	@Query("SELECT a " +
			"FROM DocumentAppointment da " +
			"JOIN Appointment a ON da.pk.appointmentId = a.id " +
			"WHERE da.pk.documentId IN :documentIds")
	List<Appointment> getEntitiesByDocuments(@Param("documentIds") List<Long> documentIds);

	@Transactional(readOnly = true)
	@Query("SELECT NEW net.pladema.medicalconsultation.appointment.repository.domain.AppointmentEquipmentShortSummaryBo(" +
			"i.name, a.dateTypeId, a.hour, e.name) " +
			"FROM Appointment a " +
			"JOIN EquipmentAppointmentAssn eaa ON(a.id = eaa.pk.appointmentId) " +
			"JOIN EquipmentDiary ed ON(ed.id = eaa.pk.equipmentDiaryId) " +
			"JOIN Patient p ON(p.id = a.patientId) " +
			"JOIN Equipment e ON(ed.equipmentId = e.id) " +
			"JOIN Sector s ON(e.sectorId = s.id) " +
			"JOIN Institution i On(s.institutionId = i.id) " +
			"WHERE a.patientId = :patientId " +
			"AND a.dateTypeId = :date " +
			"AND a.appointmentStateId NOT IN (" + AppointmentState.CANCELLED_STR + "," + AppointmentState.SERVED + "," + AppointmentState.ABSENT + ") " +
			"AND (a.deleteable.deleted = false OR a.deleteable.deleted is null ) " +
			"ORDER BY a.hour asc")
	List<AppointmentEquipmentShortSummaryBo> getAppointmentEquipmentFromDeterminatedDate(@Param("patientId") Integer patientId,
																						 @Param("date") LocalDate date);
	
	@Transactional(readOnly = true)
	@Query("SELECT a " +
			"FROM Appointment a " +
			"WHERE a.patientId IN :patients")
	List<Appointment> getAppointmentsFromPatients(@Param("patients") List<Integer> patients);

	@Transactional(readOnly = true)
	@Query( "SELECT new net.pladema.clinichistory.requests.servicerequests.domain.WorklistBo(p.id, pe.identificationTypeId, " +
			"pe.identificationNumber, pe.firstName, pe.middleNames, pe.lastName, pe.otherLastNames, pex.nameSelfDetermination," +
			" a.id, doi.completedOn, i.id, i.name) " +
			"FROM EquipmentAppointmentAssn eaa " +
			"JOIN EquipmentDiary ed ON eaa.pk.equipmentDiaryId = ed.id " +
			"JOIN Equipment e ON ed.equipmentId = e.id " +
			"JOIN Sector s ON s.id = e.sectorId " +
			"JOIN Institution i ON i.id = s.institutionId " +
			"JOIN AppointmentOrderImage aoi ON eaa.pk.appointmentId = aoi.pk.appointmentId " +
			"JOIN DetailsOrderImage doi ON eaa.pk.appointmentId = doi.appointmentId " +
			"JOIN Appointment a ON eaa.pk.appointmentId = a.id " +
			"JOIN Patient p ON a.patientId = p.id " +
			"JOIN Person pe ON p.personId = pe.id " +
			"JOIN PersonExtended pex ON pe.id = pex.id " +
			"WHERE e.modalityId = :modalityId " +
			"AND aoi.destInstitutionId = :institutionId " +
			"AND doi.completedOn BETWEEN :startDate AND :endDate " +
			"AND aoi.completed = true " +
			"AND a.id NOT IN ( SELECT aoi2.pk.appointmentId " +
			"					FROM AppointmentOrderImage aoi2 " +
			"					JOIN Document d ON aoi2.documentId = d.id " +
			"					WHERE d.statusId = '" + DocumentStatus.FINAL + "'" + " " +
			"					AND d.sourceTypeId =" + SourceType.MEDICAL_IMAGE + "  " +
			"					AND d.typeId =" + DocumentType.MEDICAL_IMAGE_REPORT + " " +
			"					AND (d.deleteable.deleted = false OR d.deleteable.deleted is null) " +
			"					AND aoi2.pk.appointmentId = a.id) ")
	List<WorklistBo> getPendingWorklistByModalityAndInstitution(@Param("modalityId") Integer modalityId,
																@Param("institutionId") Integer institutionId,
																@Param("startDate") LocalDateTime startDate,
																@Param("endDate") LocalDateTime endDate);

	@Transactional(readOnly = true)
	@Query( "SELECT new net.pladema.clinichistory.requests.servicerequests.domain.WorklistBo(p.id, pe.identificationTypeId," +
			" pe.identificationNumber, pe.firstName, pe.middleNames, pe.lastName, pe.otherLastNames, pex.nameSelfDetermination," +
			" a.id, doi.completedOn, i.id, i.name) " +
			"FROM EquipmentAppointmentAssn eaa " +
			"JOIN EquipmentDiary ed ON eaa.pk.equipmentDiaryId = ed.id " +
			"JOIN Equipment e ON ed.equipmentId = e.id " +
			"JOIN Sector s ON s.id = e.sectorId " +
			"JOIN Institution i ON i.id = s.institutionId " +
			"JOIN AppointmentOrderImage aoi ON eaa.pk.appointmentId = aoi.pk.appointmentId " +
			"JOIN DetailsOrderImage doi ON eaa.pk.appointmentId = doi.appointmentId " +
			"JOIN Appointment a ON eaa.pk.appointmentId = a.id " +
			"JOIN Patient p ON a.patientId = p.id " +
			"JOIN Person pe ON p.personId = pe.id " +
			"JOIN PersonExtended pex ON pe.id = pex.id " +
			"WHERE aoi.destInstitutionId = :institutionId " +
			"AND aoi.completed = true " +
			"AND doi.completedOn BETWEEN :startDate AND :endDate " +
			"AND a.id NOT IN ( SELECT aoi2.pk.appointmentId " +
			"					FROM AppointmentOrderImage aoi2 " +
			"					JOIN Document d ON aoi2.documentId = d.id " +
			"					WHERE d.statusId = '" + DocumentStatus.FINAL + "'" + " " +
			"					AND d.sourceTypeId =" + SourceType.MEDICAL_IMAGE + "  " +
			"					AND d.typeId =" + DocumentType.MEDICAL_IMAGE_REPORT + " " +
			"					AND (d.deleteable.deleted = false OR d.deleteable.deleted is null) " +
			"					AND aoi2.pk.appointmentId = a.id) ")
	List<WorklistBo> getPendingWorklistByInstitution(@Param("institutionId") Integer institutionId,
													 @Param("startDate") LocalDateTime startDate,
													 @Param("endDate") LocalDateTime endDate);

	@Transactional(readOnly = true)
	@Query("SELECT NEW net.pladema.clinichistory.requests.servicerequests.domain.StudyAppointmentBo(p.id, pe.firstName, pe.middleNames, pe.lastName, pe.otherLastNames, pex.nameSelfDetermination)" +
			"FROM Appointment a " +
			"JOIN Patient p ON p.id = a.patientId " +
			"JOIN Person pe ON p.personId = pe.id " +
			"JOIN PersonExtended pex ON pe.id = pex.id " +
			"WHERE a.id = :appointmentId " +
			"AND a.appointmentStateId = " + AppointmentState.SERVED + " " +
			"AND (a.deleteable.deleted = false OR a.deleteable.deleted is null)" )
	StudyAppointmentBo getPatientInfoByAppointmentId(@Param("appointmentId") Integer appointmentId);

	@Transactional(readOnly = true)
	@Query( "SELECT a.patientId " +
			"FROM Appointment a " +
			"WHERE a.id = :appointmentId")
	Integer getPatientByAppointmentId(@Param("appointmentId") Integer appointmentId);


	@Transactional(readOnly = true)
	@Query( "SELECT new net.pladema.clinichistory.requests.servicerequests.domain.WorklistBo(p.id, pe.identificationTypeId, " +
			"pe.identificationNumber, pe.firstName, pe.middleNames, pe.lastName, pe.otherLastNames, pex.nameSelfDetermination," +
			" a.id, d.updateable.updatedOn, i.id, i.name) " +
			"FROM EquipmentAppointmentAssn eaa " +
			"JOIN EquipmentDiary ed ON ed.id = eaa.pk.equipmentDiaryId " +
			"JOIN Equipment e ON ed.equipmentId = e.id " +
			"JOIN Sector s ON s.id = e.sectorId " +
			"JOIN Institution i ON i.id = s.institutionId " +
			"JOIN Appointment a ON eaa.pk.appointmentId = a.id " +
			"JOIN Patient p ON a.patientId = p.id " +
			"JOIN Person pe ON p.personId = pe.id " +
			"JOIN PersonExtended pex ON pe.id = pex.id " +
			"JOIN AppointmentOrderImage aoi ON a.id = aoi.pk.appointmentId " +
			"JOIN Document d ON aoi.documentId = d.id " +
			"WHERE e.modalityId = :modalityId " +
			"AND d.updateable.updatedOn BETWEEN :startDate AND :endDate " +
			"AND aoi.destInstitutionId = :institutionId " +
			"AND d.statusId = '" + DocumentStatus.FINAL + "'"	+ "  " +
			"AND d.sourceTypeId =" + SourceType.MEDICAL_IMAGE + "  " +
			"AND d.typeId =" + DocumentType.MEDICAL_IMAGE_REPORT + " " +
			"AND (d.deleteable.deleted = false OR d.deleteable.deleted is null)" )
	List<WorklistBo> getCompletedWorklistByModalityAndInstitution(@Param("modalityId") Integer modalityId,
																  @Param("institutionId") Integer institutionId,
																  @Param("startDate") LocalDateTime startDate,
																  @Param("endDate") LocalDateTime endDate);

	@Transactional(readOnly = true)
	@Query( "SELECT new net.pladema.clinichistory.requests.servicerequests.domain.WorklistBo(p.id, pe.identificationTypeId, " +
			"pe.identificationNumber, pe.firstName, pe.middleNames, pe.lastName, pe.otherLastNames, pex.nameSelfDetermination," +
			" a.id, d.updateable.updatedOn, i.id, i.name) " +
			"FROM Appointment a " +
			"JOIN Patient p ON a.patientId = p.id " +
			"JOIN Person pe ON p.personId = pe.id " +
			"JOIN PersonExtended pex ON pe.id = pex.id " +
			"JOIN AppointmentOrderImage aoi ON a.id = aoi.pk.appointmentId " +
			"JOIN Document d ON aoi.documentId = d.id " +
			"JOIN Institution i ON d.institutionId = i.id " +
			"WHERE aoi.destInstitutionId = :institutionId " +
			"AND d.updateable.updatedOn BETWEEN :startDate AND :endDate " +
			"AND d.statusId = '" + DocumentStatus.FINAL + "'"	+ "  " +
			"AND d.sourceTypeId =" + SourceType.MEDICAL_IMAGE + "  " +
			"AND d.typeId =" + DocumentType.MEDICAL_IMAGE_REPORT + " " +
			"AND (d.deleteable.deleted = false OR d.deleteable.deleted is null)" )
	List<WorklistBo> getCompletedWorklistByInstitution(@Param("institutionId") Integer institutionId,
													   @Param("startDate") LocalDateTime startDate,
													   @Param("endDate") LocalDateTime endDate);


	@Query("SELECT  (CASE WHEN COUNT(a.id) > 0 THEN TRUE ELSE FALSE END) " +
			"FROM Appointment a " +
			"WHERE a.patientId = :patientId " +
			"AND a.appointmentStateId IN (:states)")
	Boolean existsAppointmentByStatesAndPatientId(@Param("states") List<Short> states, @Param("patientId") Integer patientId);

	@Transactional(readOnly = true)
	@Query("SELECT hu " +
			"FROM Appointment AS a " +
			"JOIN AppointmentAssn AS aa ON a.id = aa.pk.appointmentId " +
			"JOIN Diary AS d ON aa.pk.diaryId = d.id " +
			"LEFT JOIN HierarchicalUnit AS hu ON hu.id = d.hierarchicalUnitId " +
			"WHERE a.id = :appointmentId " +
			"AND (d.deleteable.deleted = false OR d.deleteable.deleted is null)")
	Optional<HierarchicalUnit> findDiaryHierarchicalUnitIdByAppointment(@Param("appointmentId") Integer appointmentId);
}
