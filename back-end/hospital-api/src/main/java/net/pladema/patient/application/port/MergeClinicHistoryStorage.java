package net.pladema.patient.application.port;

import ar.lamansys.sgh.clinichistory.infrastructure.output.repository.document.ESourceType;

import ar.lamansys.sgx.shared.migratable.SGXDocumentEntityRepository;

import java.util.List;

public interface MergeClinicHistoryStorage {

	<T extends SGXDocumentEntityRepository> void migratePatientIdFromItem(Class<T> clazz, List<Long> ids, Integer newPatientId);
	List<Integer> getInternmentEpisodesIds(List<Integer> oldPatients);
	List<Integer> getOutpatientConsultationIds(List<Integer> oldPatients);
	List<Integer> getEmergencyCareEpisodeIds(List<Integer> oldPatients);
	List<Integer> getMedicationRequestIds(List<Integer> oldPatients);
	List<Integer> getServiceRequestIds(List<Integer> oldPatients);
	List<Integer> getNursingConsultationIds(List<Integer> oldPatients);
	List<Integer> getVaccineConsultationIds(List<Integer> oldPatients);
	List<Integer> getCounterReferenceIds(List<Integer> oldPatients);
	List<Integer> getServiceRequestIdsFromIdSourceType(List<Integer> ids, Short sourceType);
	List<Integer> getOdontologyConsultationIds(List<Integer> oldPatients);
	void modifyDocument(List<Long> dIds, Integer newPatientId);
	List<Long> getDocumentsIds(List<Integer> ids, List<ESourceType> sourceTypes);
	void modifyInternmentEpisode(List<Integer> ieIds, Integer newPatientId);
	void modifyOutpatientConsultation(List<Integer> ocIds, Integer newPatientId);
	void modifyEmergencyCareEpisode(List<Integer> ocIds, Integer newPatientId);
	void modifyMedicationRequest(List<Integer> mrIds, Integer newPatientId);
	void modifyServiceRequest(List<Integer> mrIds, Integer newPatientId);
	void modifyNursingConsultation(List<Integer> ncIds, Integer newPatientId);
	void modifyVaccineConsultation(List<Integer> ids, Integer newPatientId);
	void modifyCounterReference(List<Integer> crIds, Integer newPatientId);
	void modifyTriageRiskFactor(List<Integer> ids, Integer newPatientId);
	void modifySnvsReport(List<Integer> oldPatients, Integer newPatientId);
	void modifyOdontologyConsultation(List<Integer> ids, Integer newPatientId);
	void modifyAppointment(List<Integer> oldPatients, Integer newPatientId);
	void rebuildDocumentsFiles(List<Long> documentsIds);
	void unmergeClinicData(Integer inactivePatientId);


	}