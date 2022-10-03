package net.pladema.medicalconsultation.diary.repository;

import net.pladema.medicalconsultation.diary.controller.dto.DiaryProtectedAppointmentsSearch;
import net.pladema.medicalconsultation.diary.service.domain.DiaryAvailableProtectedAppointmentsInfoBo;

import java.util.List;

public interface DiaryAvailableProtectedAppointmentsSearchRepository {

	List<DiaryAvailableProtectedAppointmentsInfoBo> getAllDiaryProtectedAppointmentsByFilter(DiaryProtectedAppointmentsSearch diaryProtectedAppointmentsSearch);

}
