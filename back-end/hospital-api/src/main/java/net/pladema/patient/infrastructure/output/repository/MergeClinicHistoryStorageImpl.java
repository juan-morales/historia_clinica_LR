package net.pladema.patient.infrastructure.output.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ar.lamansys.refcounterref.infraestructure.output.repository.counterreference.CounterReferenceRepository;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.DocumentRepository;
import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.ESourceType;
import ar.lamansys.sgh.shared.infrastructure.input.service.immunization.SharedImmunizationPort;
import ar.lamansys.sgh.shared.infrastructure.input.service.nursing.SharedNursingConsultationPort;
import ar.lamansys.sgx.shared.migratable.SGXDocumentEntity;
import ar.lamansys.sgx.shared.migratable.SGXDocumentEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pladema.clinichistory.hospitalization.repository.InternmentEpisodeRepository;
import net.pladema.clinichistory.outpatient.repository.OutpatientConsultationRepository;
import net.pladema.clinichistory.requests.medicationrequests.repository.MedicationRequestRepository;
import net.pladema.clinichistory.requests.servicerequests.repository.ServiceRequestRepository;
import net.pladema.patient.application.port.MergeClinicHistoryStorage;
import net.pladema.patient.application.port.MigratePatientStorage;
import net.pladema.patient.infrastructure.output.repository.entity.EMergeTable;
import net.pladema.snvs.infrastructure.output.repository.report.SnvsReportRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class MergeClinicHistoryStorageImpl implements MergeClinicHistoryStorage {

	private final InternmentEpisodeRepository internmentEpisodeRepository;
	private final OutpatientConsultationRepository outpatientConsultationRepository;
	private final MedicationRequestRepository medicationRequestRepository;
	private final ServiceRequestRepository serviceRequestRepository;
	private final SharedNursingConsultationPort sharedNursingConsultationPort;
	private final SharedImmunizationPort sharedImmunizationPort;
	private final CounterReferenceRepository counterReferenceRepository;
	private final SnvsReportRepository snvsReportRepository;
	private final DocumentRepository documentRepository;
	private final MigratePatientStorage migratePatientStorage;
	private final MigratableRepositoryMap repositoryMap;


	@Override
	public <T extends SGXDocumentEntityRepository> void migratePatientIdFromItem(Class<T> clazz, List<Long> ids, Integer newPatientId) {
		List<SGXDocumentEntity> entities = repositoryMap.get(clazz).getEntitiesByDocuments(ids);
		entities.forEach(item-> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.map(item.getClass().getSimpleName())));
	}

	@Override
	public List<Integer> getInternmentEpisodesIds(List<Integer> oldPatientsIds) {
		log.debug("Input parameters -> oldPatientsIds{}", oldPatientsIds);
		return internmentEpisodeRepository.getInternmentEpisodeIdsFromPatients(oldPatientsIds);
	}

	@Override
	public List<Integer> getOutpatientConsultationIds(List<Integer> oldPatients) {
		return outpatientConsultationRepository.getOutpatientConsultationIdsFromPatients(oldPatients);
	}

	@Override
	public List<Integer> getMedicationRequestIds(List<Integer> oldPatients) {
		return medicationRequestRepository.getMedicatoinRequestIdsFromPatients(oldPatients);
	}

	@Override
	public List<Integer> getServiceRequestIds(List<Integer> oldPatients) {
		return serviceRequestRepository.getServiceRequestIdsFromPatients(oldPatients);
	}

	@Override
	public List<Integer> getNursingConsultationIds(List<Integer> oldPatients) {
		return sharedNursingConsultationPort.getNursingConsultationIdsFromPatients(oldPatients);
	}
	
	@Override
	public List<Integer> getVaccineConsultationIds(List<Integer> oldPatients) {
		return sharedImmunizationPort.getVaccineConsultationIdsFromPatients(oldPatients);
	}

	@Override
	public List<Integer> getCounterReferenceIds(List<Integer> oldPatients) {
		return counterReferenceRepository.getCounterReferenceIdsFromPatients(oldPatients);
	}

	@Override
	public List<Integer> getServiceRequestIdsFromIdSourceType(List<Integer> ids, Short sourceType) {
		return serviceRequestRepository.getServiceRequestIdsFromIdSourceType(ids,sourceType);
	}

	@Override
	public void modifyDocument(List<Long> ids, Integer newPatientId) {
		log.debug("Document ids to modify {}", ids);
		documentRepository.findAllById(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId().intValue(), item.getPatientId(), newPatientId, EMergeTable.DOCUMENT));
	}

	@Override
	public List<Long> getDocumentsIds(List<Integer> ids, List<ESourceType> sourceTypes) {
		log.debug("Input parameters -> ids{}", ids);
		return documentRepository.getIdsBySourceIdType(ids, sourceTypes.stream().map(sts -> sts.getId()).collect(Collectors.toList()));
	}

	@Override
	public void modifyInternmentEpisode(List<Integer> ids, Integer newPatientId) {
		log.debug("Internment episode ids to modify {}", ids);
		internmentEpisodeRepository.findAllById(ids)
				.forEach(item-> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.INTERNMENT_EPISODE));
	}

	@Override
	public void modifyOutpatientConsultation(List<Integer> ids, Integer newPatientId) {
		log.debug("Outpatient consultation ids to modify {}", ids);
		outpatientConsultationRepository.findAllById(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.OUTPATIENT_CONSULTATION));
	}


	@Override
	public void modifyMedicationRequest(List<Integer> ids, Integer newPatientId) {
		log.debug("Medication request ids to modify {}", ids);
		medicationRequestRepository.findAllById(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.MEDICATION_REQUEST));
	}

	@Override
	public void modifyServiceRequest(List<Integer> ids, Integer newPatientId) {
		log.debug("Service request ids to modify {}", ids);
		serviceRequestRepository.findAllById(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.SERVICE_REQUEST));
	}

	@Override
	public void modifyNursingConsultation(List<Integer> ids, Integer newPatientId) {
		log.debug("Nursing consultation ids to modify {}", ids);
		sharedNursingConsultationPort.findAllById(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.NURSING_CONSULTATION));
	}

	@Override
	public void modifyVaccineConsultation(List<Integer> ids, Integer newPatientId) {
		log.debug("Vaccine consultation ids to modify {}", ids);
		sharedImmunizationPort.findAllVaccineConsultationByIds(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.VACCINE_CONSULTATION));
	}

	@Override
	public void modifyCounterReference(List<Integer> ids, Integer newPatientId) {
		log.debug("Counter reference ids to modify {}",ids);
		counterReferenceRepository.findAllById(ids)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.COUNTER_REFERENCE));
	}

	@Override
	public void modifySnvsReport(List<Integer> oldPatients, Integer newPatientId) {
		snvsReportRepository.findAllByPatients(oldPatients)
				.forEach(item -> migratePatientStorage.migrateItem(item.getId(), item.getPatientId(), newPatientId, EMergeTable.SNVS_REPORT));
	}
	
	@Override
	public void unmergeClinicData(Integer inactivePatientId) {
		log.debug("Input parameters -> inactivePatientId {}", inactivePatientId);
		migratePatientStorage.undoMigrateByInactivePatient(inactivePatientId);
	}


}
