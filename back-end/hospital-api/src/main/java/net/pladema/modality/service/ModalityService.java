package net.pladema.modality.service;

import net.pladema.medicalconsultation.equipmentdiary.service.domain.EquipmentDiaryBo;
import net.pladema.modality.service.domain.ModalityBO;

import java.util.List;

public interface ModalityService {

	List<ModalityBO> getAllModality();
}
