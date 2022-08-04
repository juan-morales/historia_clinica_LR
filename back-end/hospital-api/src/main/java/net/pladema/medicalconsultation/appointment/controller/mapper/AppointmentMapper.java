package net.pladema.medicalconsultation.appointment.controller.mapper;

import net.pladema.medicalconsultation.appointment.controller.dto.AssignedAppointmentDto;
import net.pladema.medicalconsultation.appointment.controller.dto.EmptyAppointmentDto;
import net.pladema.medicalconsultation.appointment.service.domain.EmptyAppointmentBo;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentAssignedBo;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentSearchBo;
import net.pladema.medicalconsultation.appointment.controller.dto.AppointmentSearchDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ar.lamansys.sgx.shared.dates.configuration.LocalDateMapper;
import net.pladema.medicalconsultation.appointment.controller.dto.AppointmentBasicPatientDto;
import net.pladema.medicalconsultation.appointment.controller.dto.AppointmentDailyAmountDto;
import net.pladema.medicalconsultation.appointment.controller.dto.AppointmentDto;
import net.pladema.medicalconsultation.appointment.controller.dto.AppointmentListDto;
import net.pladema.medicalconsultation.appointment.controller.dto.CreateAppointmentDto;
import net.pladema.medicalconsultation.appointment.controller.dto.UpdateAppointmentDto;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentBo;
import net.pladema.medicalconsultation.appointment.service.domain.AppointmentDailyAmountBo;
import net.pladema.medicalconsultation.appointment.service.domain.UpdateAppointmentBo;

@Mapper(uses = {LocalDateMapper.class})
public interface AppointmentMapper {

    @Named("toAppointmentListDto")
    @Mapping(target = "id", source = "appointmentBo.id")
    @Mapping(target = "date", source = "appointmentBo.date")
    @Mapping(target = "hour", source = "appointmentBo.hour")
    @Mapping(target = "overturn", source = "appointmentBo.overturn")
    @Mapping(target = "appointmentStateId", source = "appointmentBo.appointmentStateId")
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "phoneNumber", source = "appointmentBo.phoneNumber")
    AppointmentListDto toAppointmentListDto(AppointmentBo appointmentBo, AppointmentBasicPatientDto patient);
  
    @Named("toAppointmentDto")
    AppointmentDto toAppointmentDto(AppointmentBo appointmentBo);

    @Named("toAppointmentBo")
    AppointmentBo toAppointmentBo(CreateAppointmentDto createAppointmentDto);

    @Named("toAppointmentDailyAmountDto")
    AppointmentDailyAmountDto toAppointmentDailyAmountDto(AppointmentDailyAmountBo appointmentDailyAmountBo);

	@Named("toAssignedAppointmentDto")
	AssignedAppointmentDto toAssignedAppointmentDto(AppointmentAssignedBo appointmentAssignedBo);

	@Named("toUpdateAppointmentBo")
	UpdateAppointmentBo toUpdateAppointmentBo(UpdateAppointmentDto updateAppointmentDto);

	@Named("toAppointmentSearchBo")
	AppointmentSearchBo toAppointmentSearchBo(AppointmentSearchDto appointmentSearchDto);

	@Named("toEmptyAppointmentDto")
	EmptyAppointmentDto toEmptyAppointmentDto(EmptyAppointmentBo emptyAppointmentBo);

}
