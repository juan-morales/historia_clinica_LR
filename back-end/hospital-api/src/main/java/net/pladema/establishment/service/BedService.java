package net.pladema.establishment.service;

import java.util.Optional;

import net.pladema.establishment.controller.dto.BedDto;
import net.pladema.establishment.repository.entity.Bed;
public interface BedService {

    BedDto updateBedStatusOccupied(Integer id);

	public Optional<Bed> freeBed(Integer bedId);
	
}
