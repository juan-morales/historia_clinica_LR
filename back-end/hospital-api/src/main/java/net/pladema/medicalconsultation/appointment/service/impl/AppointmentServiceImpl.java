package net.pladema.medicalconsultation.appointment.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.lamansys.sgh.shared.infrastructure.input.service.SharedStaffPort;
import ar.lamansys.sgx.shared.dates.configuration.DateTimeProvider;
import ar.lamansys.sgx.shared.featureflags.AppFeature;
import ar.lamansys.sgx.shared.featureflags.application.FeatureFlagsService;
import ar.lamansys.sgx.shared.security.UserInfo;
import lombok.extern.slf4j.Slf4j;
import net.pladema.establishment.controller.service.InstitutionExternalService;
import net.pladema.establishment.repository.MedicalCoveragePlanRepository;
import net.pladema.medicalconsultation.appointment.repository.AppointmentObservationRepository;
import net.pladema.medicalconsultation.appointment.repository.AppointmentRepository;
import net.pladema.medicalconsultation.appointment.repository.HistoricAppointmentStateRepository;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentShortSummaryBo;
import net.pladema.medicalconsultation.appointment.repository.domain.AppointmentTicketBo;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentObservation;
import net.pladema.medicalconsultation.appointment.repository.entity.AppointmentState;
import net.pladema.medicalconsultation.appointment.repository.entity.HistoricAppointmentState;
import net.pladema.medicalconsultation.appointment.service.AppointmentService;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentAssignedBo;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentBo;
import net.pladema.medicalconsultation.appointment.service.domain.UpdateAppointmentBo;
import net.pladema.medicalconsultation.appointment.service.exceptions.AppointmentNotFoundEnumException;
import net.pladema.medicalconsultation.appointment.service.exceptions.AppointmentNotFoundException;
import net.pladema.medicalconsultation.appointment.service.ports.AppointmentStorage;
import net.pladema.patient.controller.dto.PatientMedicalCoverageDto;
import net.pladema.patient.controller.service.PatientExternalMedicalCoverageService;
import net.pladema.patient.service.domain.PatientCoverageInsuranceDetailsBo;
import net.pladema.patient.service.domain.PatientMedicalCoverageBo;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

	private static final String OUTPUT = "Output -> {}";

	private final AppointmentRepository appointmentRepository;

	private final AppointmentObservationRepository appointmentObservationRepository;

	private final HistoricAppointmentStateRepository historicAppointmentStateRepository;

	private final SharedStaffPort sharedStaffPort;

	private final FeatureFlagsService featureFlagsService;

	private final DateTimeProvider dateTimeProvider;

	private final PatientExternalMedicalCoverageService patientExternalMedicalCoverageService;

	private final InstitutionExternalService institutionExternalService;

	private final MedicalCoveragePlanRepository medicalCoveragePlanRepository;

	private final AppointmentStorage appointmentStorage;


	public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentObservationRepository appointmentObservationRepository, HistoricAppointmentStateRepository historicAppointmentStateRepository, SharedStaffPort sharedStaffPort, DateTimeProvider dateTimeProvider, PatientExternalMedicalCoverageService patientExternalMedicalCoverageService, InstitutionExternalService institutionExternalService, MedicalCoveragePlanRepository medicalCoveragePlanRepository, FeatureFlagsService featureFlagsService, AppointmentStorage appointmentStorage) {
		this.appointmentRepository = appointmentRepository;
		this.appointmentObservationRepository = appointmentObservationRepository;
		this.historicAppointmentStateRepository = historicAppointmentStateRepository;
		this.sharedStaffPort = sharedStaffPort;
		this.featureFlagsService = featureFlagsService;
		this.dateTimeProvider = dateTimeProvider;
		this.patientExternalMedicalCoverageService = patientExternalMedicalCoverageService;
		this.institutionExternalService = institutionExternalService;
		this.medicalCoveragePlanRepository = medicalCoveragePlanRepository;
		this.appointmentStorage = appointmentStorage;
	}

	@Override
	public Collection<AppointmentBo> getAppointmentsByDiaries(List<Integer> diaryIds, LocalDate from, LocalDate to) {
		log.debug("Input parameters -> diaryIds {}", diaryIds);
		Collection<AppointmentBo> result = new ArrayList<>();
		if (!diaryIds.isEmpty())
			result = appointmentStorage.getAppointmentsByDiaries(diaryIds, from, to).stream()
					.distinct()
					.collect(Collectors.toList());
		log.debug("Result size {}", result.size());
		log.trace(OUTPUT, result);
		return result;
	}

	@Override
	public Collection<AppointmentBo> getAppointmentsByProfessionalInInstitution(Integer healthcareProfessionalId, Integer institutionId, LocalDate from, LocalDate to) {
		log.debug("Input parameters -> diaryIds {}", healthcareProfessionalId);
		Collection<AppointmentBo> result = new ArrayList<>();
		if (healthcareProfessionalId!=null)
			result = appointmentStorage.getAppointmentsByProfessionalInInstitution(healthcareProfessionalId, institutionId, from, to).stream()
					.distinct()
					.collect(Collectors.toList());
		log.debug("Result size {}", result.size());
		log.trace(OUTPUT, result);
		return result;
	}


	@Override
	public Collection<AppointmentBo> getFutureActiveAppointmentsByDiary(Integer diaryId) {
		log.debug("Input parameters -> diaryId {}", diaryId);
		Collection<AppointmentBo> result = appointmentRepository.getFutureActiveAppointmentsByDiary(diaryId).stream()
				.map(AppointmentBo::fromAppointmentDiaryVo)
				.distinct()
				.collect(Collectors.toList());
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public boolean existAppointment(Integer diaryId, Integer openingHoursId, LocalDate date, LocalTime hour) {
		log.debug("Input parameters -> diaryId {}, openingHoursId {}, date {}, hour {}", diaryId, openingHoursId, date, hour);
		boolean result = appointmentRepository.existAppointment(diaryId, openingHoursId, date, hour);
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public Optional<AppointmentBo> findAppointmentBy(Integer diaryId, LocalDate date, LocalTime hour) {
		log.debug("Input parameters -> diaryId {}, date {}, hour {}", diaryId, date, hour);
		var res = appointmentRepository.findAppointmentBy(diaryId, date, hour);
		log.debug(OUTPUT, res);
		return res.stream().findFirst().map(AppointmentBo::newFromAppointment);
	}

	public boolean existAppointment(Integer diaryId, LocalDate date, LocalTime hour) {
		log.debug("Input parameters -> diaryId {}, date {}, hour {}", diaryId, date, hour);
		boolean result = appointmentRepository.existAppointment(diaryId, date, hour);
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	@Transactional
	public boolean updateState(Integer appointmentId, short appointmentStateId, Integer userId, String reason) {
		log.debug("Input parameters -> appointmentId {}, appointmentStateId {}, userId {}, reason {}", appointmentId, appointmentStateId, userId, reason);
		appointmentRepository.updateState(appointmentId, appointmentStateId, userId);
		historicAppointmentStateRepository.save(new HistoricAppointmentState(appointmentId, appointmentStateId, reason));
		log.debug(OUTPUT, Boolean.TRUE);
		return Boolean.TRUE;
	}

	@Override
	public Optional<AppointmentBo> getAppointment(Integer appointmentId) {
		log.debug("Input parameters -> appointmentId {}", appointmentId);
		Optional<AppointmentBo>	result = appointmentRepository.getAppointment(appointmentId).stream().findFirst().map(AppointmentBo::fromAppointmentVo);
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public boolean hasCurrentAppointment(Integer patientId, Integer healthProfessionalId, LocalDate date) {
		log.debug("Input parameters -> patientId {}, healthProfessionalId {}, date {} ", patientId, healthProfessionalId, date);
		boolean result = !(appointmentRepository.getAppointmentsId(patientId, healthProfessionalId, date).isEmpty());
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public boolean hasOldAppointment(Integer patientId, Integer healthProfessionalId) {
		log.debug("Input parameters -> patientId {}, healthProfessionalId {}", patientId, healthProfessionalId);
		boolean result = !(appointmentRepository.getOldAppointmentsId(patientId, healthProfessionalId).isEmpty());
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public List<Integer> getOldAppointments(Integer patientId, Integer healthProfessionalId) {
		log.debug("Input parameters -> patientId {}, healthProfessionalId {}", patientId, healthProfessionalId);
		List<Integer> result = appointmentRepository.getOldAppointmentsId(patientId, healthProfessionalId);
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public List<Integer> getAppointmentsId(Integer patientId, Integer healthcareProfessionalId, LocalDate date) {
		log.debug("Input parameters -> patientId {}, healthcareProfessionalId {}", patientId, healthcareProfessionalId);
		List<Integer> result = appointmentRepository.getAppointmentsId(patientId, healthcareProfessionalId, date);
		log.debug(OUTPUT, result);
		return result;
	}

	@Override
	public boolean updatePhoneNumber(Integer appointmentId, String phonePrefix, String phoneNumber, Integer userId) {
		appointmentRepository.updatePhoneNumber(appointmentId,phonePrefix,phoneNumber,userId);
		log.debug(OUTPUT, Boolean.TRUE);
		return Boolean.TRUE;
	}

	@Override
	public boolean saveObservation(Integer appointmentId, String observation) {
		AppointmentObservation appointmentObservation = AppointmentObservation.builder()
				.appointmentId(appointmentId)
				.observation(observation)
				.createdBy(UserInfo.getCurrentAuditor())
				.build();
		appointmentObservationRepository.save(appointmentObservation);
		log.debug(OUTPUT, Boolean.TRUE);
		return Boolean.TRUE;
	}

	@Override
	public boolean updateDate(Integer appointmentId, LocalDate date, LocalTime time) {
		appointmentRepository.updateDate(appointmentId, date, time);
		log.debug(OUTPUT, Boolean.TRUE);
		return Boolean.TRUE;
	}

	@Override
	public boolean updateMedicalCoverage(Integer appointmentId, Integer patientMedicalCoverage) {
		appointmentRepository.findById(appointmentId).ifPresent(a -> {
			if(a.isAssigned()) {
				a.setPatientMedicalCoverageId(patientMedicalCoverage);
				appointmentRepository.save(a);
			}
		});
		log.debug(OUTPUT, Boolean.TRUE);
		return Boolean.TRUE;
	}

	@Override
	public Integer getMedicalCoverage(Integer patientId, Integer healthcareProfessionalId,
									  LocalDate currentDate) {
		List<Integer> medicalCoverages = appointmentRepository.getMedicalCoverage(patientId, currentDate,
				AppointmentState.CONFIRMED,AppointmentState.ASSIGNED, healthcareProfessionalId);
		Integer patientMedicalCoverageId = medicalCoverages.stream().findAny().orElse(null);
		log.debug(OUTPUT, patientMedicalCoverageId);
		return patientMedicalCoverageId;
	}

	@Override
	public PatientMedicalCoverageBo getCurrentAppointmentMedicalCoverage(Integer patientId, Integer institutionId) {
		log.debug("Input parameters -> patientId {}, institutionId {}", patientId, institutionId);

		var appointmentId = this.getCurrentAppointmentId(patientId, institutionId);
		if(appointmentId == null)
			return null;

		var medicalCoverageIdOpt = appointmentRepository.getAppointmentMedicalCoverageId(appointmentId);
		if(medicalCoverageIdOpt.isPresent()){
			var result = this.makePatientMedicalCoverageBo(patientExternalMedicalCoverageService
					.getCoverage(medicalCoverageIdOpt.get()));
			if(result != null) {
				log.trace(OUTPUT, result);
				return result;
			}
		}

		return new PatientMedicalCoverageBo();
	}

	private Integer getCurrentAppointmentId(Integer patientId, Integer institutionId) {
		log.debug("Input parameters -> patientId {}, institutionId {}", patientId, institutionId);

		ZoneId institutionZoneId = institutionExternalService.getTimezone(institutionId);
		var currentDateTime = dateTimeProvider.nowDateTimeWithZone(institutionZoneId);
		var userId = UserInfo.getCurrentAuditor();
		var getCurrentAppointmentsByPatient = appointmentRepository.getCurrentAppointmentsByPatient(patientId,
				institutionId, userId, currentDateTime.toLocalDate());
		if(getCurrentAppointmentsByPatient.isEmpty())
			return null;

		var result = getCurrentAppointmentsByPatient.get(0);
		log.debug(OUTPUT, result);
		return result;
	}

	private PatientMedicalCoverageBo makePatientMedicalCoverageBo(PatientMedicalCoverageDto dto){
		log.trace("Input parameters -> patientMedicalCoverageDto {}", dto);
		if(dto == null)
			return null;

		var coverageDto = dto.getMedicalCoverage();
		var medicalCoverageBo = new PatientCoverageInsuranceDetailsBo(coverageDto.getId(),
				coverageDto.getName(), coverageDto.getCuit(), coverageDto.getType());
		var vigencyDate = dto.getVigencyDate();

		var result = new PatientMedicalCoverageBo(dto.getId(),
				vigencyDate != null ? LocalDate.parse(vigencyDate) : null,
				dto.getActive(),
				dto.getAffiliateNumber(),
				medicalCoverageBo,
				dto.getCondition(),
				dto.getStartDate() != null ? LocalDate.parse(dto.getStartDate()) : null,
				dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate()) : null,
				dto.getPlanId(),
				dto.getPlanName());

		if (dto.getPlanId() != null) {
			var planIdOpt = medicalCoveragePlanRepository.findById(dto.getPlanId());
			planIdOpt.ifPresent(medicalCoveragePlan -> result.setPlanName(medicalCoveragePlan.getPlan()));
		}

		log.trace(OUTPUT, result);
		return result;
	}

	private Collection<AppointmentAssignedBo> getAssignedAppointmentsByPatient(Integer patientId) {
		log.debug("Input parameters -> patientId {}", patientId);
		Collection<AppointmentAssignedBo> result;
		result = appointmentRepository.getAssignedAppointmentsByPatient(patientId).stream().map(AppointmentAssignedBo::new)
				.collect(Collectors.toList());
		log.debug("Result size {}", result.size());
		log.trace(OUTPUT, result);
		return result;
	}

	@Override
	public Collection<AppointmentAssignedBo> getCompleteAssignedAppointmentInfo(Integer patientId){
		log.debug("Input parameters -> patientId {}", patientId);
		Collection<AppointmentAssignedBo> resultService = this.getAssignedAppointmentsByPatient(patientId);
		Collection<AppointmentAssignedBo> result = resultService.stream()
				.parallel()
				.map(appointmentAssigned ->{
					var basicHealtcareDtoMap = sharedStaffPort.getProfessionalCompleteInfo(appointmentAssigned.getProfessionalId());
					appointmentAssigned.setSpecialties(basicHealtcareDtoMap.getClinicalSpecialties().stream()
							.map(specialty -> {return specialty.getName();})
							.collect(Collectors.toList()));
					if(featureFlagsService.isOn(AppFeature.HABILITAR_DATOS_AUTOPERCIBIDOS) && basicHealtcareDtoMap.getNameSelfDetermination() != null && !basicHealtcareDtoMap.getNameSelfDetermination().isEmpty())
						appointmentAssigned.setProfessionalName(basicHealtcareDtoMap.getNameSelfDetermination() + ' ' + basicHealtcareDtoMap.getLastName() + ' ' + basicHealtcareDtoMap.getId());
					else
						appointmentAssigned.setProfessionalName(basicHealtcareDtoMap.getFirstName() + ' ' + basicHealtcareDtoMap.getLastName());
					return appointmentAssigned;
				}).collect(Collectors.toList());
		log.debug("Result size {}", result.size());
		log.trace(OUTPUT, result);
		return result;
	}

	@Override
	public AppointmentBo updateAppointment(UpdateAppointmentBo updateAppointmentBo) {
		var appointment = appointmentRepository.findById(updateAppointmentBo.getAppointmentId());
		if(appointment.isPresent()){
			appointment.get().setAppointmentStateId(updateAppointmentBo.getAppointmentStateId());
			appointment.get().setPatientId(updateAppointmentBo.getPatientId());
			appointment.get().setPatientMedicalCoverageId(updateAppointmentBo.getPatientMedicalCoverageId());
			appointment.get().setIsOverturn(updateAppointmentBo.isOverturn());
			appointment.get().setPhoneNumber(updateAppointmentBo.getPhoneNumber());
			return AppointmentBo.newFromAppointment(appointmentRepository.save(appointment.get()));
		}
		return new AppointmentBo();
	}

	@Override
	public void delete(AppointmentBo appointmentBo) {
		appointmentRepository.deleteById(appointmentBo.getId());
	}

	@Override
	public AppointmentTicketBo getAppointmentTicketData(Integer appointmentId) {
		log.debug("Input parameters -> appointmentId {}", appointmentId);
		var result = this.appointmentRepository.getAppointmentTicketData(appointmentId).orElseThrow(
				()-> new AppointmentNotFoundException(AppointmentNotFoundEnumException.APPOINTMENT_ID_NOT_FOUND, "el id no corresponde con ningun turno asignado")
		);
		log.trace(OUTPUT, result);
		return result;
	}

	@Override
	public AppointmentShortSummaryBo getAppointmentFromDeterminatedDate(Integer patientId, LocalDate date) {
		log.debug("Input parameters -> patientId {}, date {}", patientId, date);
		AppointmentShortSummaryBo result = null;
		List<AppointmentShortSummaryBo> appointmentShortSummaryBoList = this.appointmentRepository.getAppointmentFromDeterminatedDate(patientId, date);
		if (!appointmentShortSummaryBoList.isEmpty())
			result = appointmentShortSummaryBoList.get(0);
		return result;
	}

}
