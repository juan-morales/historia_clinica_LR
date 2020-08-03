package net.pladema.establishment.controller.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import net.pladema.establishment.controller.dto.BedDto;
import net.pladema.establishment.controller.dto.BedInfoDto;
import net.pladema.establishment.controller.dto.BedSummaryDto;
import net.pladema.establishment.controller.dto.PatientBedRelocationDto;
import net.pladema.establishment.repository.domain.BedInfoVo;
import net.pladema.establishment.repository.domain.BedSummaryVo;
import net.pladema.establishment.repository.entity.Bed;
import net.pladema.establishment.repository.entity.HistoricPatientBedRelocation;
import net.pladema.sgx.dates.configuration.LocalDateMapper;

@Mapper(uses = {LocalDateMapper.class})
public interface BedMapper {

    @Named("toBedDto")
    BedDto toBedDto(Bed bed);
    
    @Named("toListBedDto")
    @IterableMapping(qualifiedByName = "toBedDto")
    List<BedDto> toListBedDto(List<Bed> bedList);

    @Named("toBedInfoDto")
    @Mapping(target = "bed.bedCategory", source = "bedCategory")
    @Mapping(target = "bed", source = "bed")
    @Mapping(target = "bed.room", source = "room")
    @Mapping(target = "bed.room.sector", source = "sector")
    @Mapping(target = "bed.room.sector.specialty.name", source = "sector.specialtyName")
    BedInfoDto toBedInfoDto(BedInfoVo bedSummaryVo);
    
    @Named("toBedSummaryDto")
    BedSummaryDto toBedSummaryDto(BedSummaryVo bedSummary);
    
    @Named("toListBedSummaryDto")
    @IterableMapping(qualifiedByName = "toBedSummaryDto")
    List<BedSummaryDto> toListBedSummaryDto(List<BedSummaryVo> bedSummaryList);

    @Named("toPatientBedRelocationDto")
    PatientBedRelocationDto toPatientBedRelocationDto(HistoricPatientBedRelocation historicPatientBedRelocation);
    
    @Named("fromPatientBedRelocationDto")
    HistoricPatientBedRelocation fromPatientBedRelocationDto(PatientBedRelocationDto patientBedRelocationDto);
    
}
