package net.pladema.medicalconsultation.diary.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.pladema.medicalconsultation.diary.repository.domain.DiaryOpeningHoursVo;
import net.pladema.medicalconsultation.diary.repository.domain.OccupationVo;
import net.pladema.medicalconsultation.diary.repository.entity.DiaryOpeningHours;
import net.pladema.medicalconsultation.diary.repository.entity.DiaryOpeningHoursPK;

@Repository
public interface DiaryOpeningHoursRepository extends JpaRepository<DiaryOpeningHours, DiaryOpeningHoursPK> {

    @Transactional(readOnly = true)
    @Query("select new net.pladema.medicalconsultation.diary.repository.domain.OccupationVo( " +
            "d.id, d.startDate, d.endDate, oh.dayWeekId, oh.from, oh.to) " +
            "from DiaryOpeningHours as doh " +
            "join Diary as d on ( doh.pk.diaryId = d.id ) " +
            "join OpeningHours as oh on ( doh.pk.openingHoursId = oh.id ) " +
            "where d.doctorsOfficeId = :doctorsOfficeId " +
            "and d.startDate <= :endDate " +
            "and d.endDate >= :startDate " +
            "order by oh.dayWeekId, oh.from")
    List<OccupationVo> findAllWeeklyDoctorsOfficeOccupation(@Param("doctorsOfficeId") Integer doctorsOfficeId,
                                                            @Param("startDate")LocalDate startDate,
                                                            @Param("endDate")LocalDate endDate);

    @Transactional(readOnly = true)
    @Query("SELECT NEW net.pladema.medicalconsultation.diary.repository.domain.DiaryOpeningHoursVo( " +
            "d.id, oh, doh.medicalAttentionTypeId, doh.overturnCount) " +
            "FROM DiaryOpeningHours AS doh " +
            "JOIN Diary AS d ON ( doh.pk.diaryId = d.id ) " +
            "JOIN OpeningHours AS oh ON ( doh.pk.openingHoursId = oh.id ) " +
            "WHERE doh.pk.diaryId IN (:diaryIds) " +
            "ORDER BY oh.dayWeekId, oh.from")
    List<DiaryOpeningHoursVo> getDiariesOpeningHours(@Param("diaryIds") List<Integer> diaryIds);

    @Transactional(readOnly = true)
    @Query( "SELECT (case when count(doh) > 0 then true else false end) " +
            "FROM DiaryOpeningHours AS doh " +
            "WHERE doh.pk.diaryId = :diaryId " +
            "AND doh.pk.openingHoursId = :openingHoursId " +
            "AND doh.overturnCount > (SELECT COUNT(a.id) " +
            "                           FROM Appointment AS a " +
            "                           JOIN AppointmentAssn AS aa ON (a.id = aa.pk.appointmentId) " +
            "                           WHERE aa.pk.diaryId = :diaryId " +
            "                           AND aa.pk.openingHoursId = :openingHoursId " +
            "                           AND a.isOverturn = true )" )
    boolean allowNewOverturn(@Param("diaryId") Integer diaryId, @Param("openingHoursId") Integer openingHoursId);
    
	@Transactional
	@Modifying
	@Query("DELETE FROM DiaryOpeningHours doh WHERE doh.pk.diaryId = :diaryId ")
	void deleteAll(@Param("diaryId") Integer diaryId);

}
