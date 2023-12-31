package ar.lamansys.sgh.publicapi.prescription.application.fetchprescriptionsbyidanddni;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ar.lamansys.sgh.publicapi.prescription.application.port.out.PrescriptionIdentifier;
import ar.lamansys.sgh.publicapi.prescription.application.port.out.PrescriptionStorage;
import ar.lamansys.sgh.publicapi.prescription.domain.PrescriptionBo;

@Service
public class FetchPrescriptionsByIdAndDni {

	private final Logger logger;
	private final PrescriptionStorage prescriptionStorage;


	public FetchPrescriptionsByIdAndDni(PrescriptionStorage prescriptionStorage) {
		this.prescriptionStorage = prescriptionStorage;
		this.logger = LoggerFactory.getLogger(FetchPrescriptionsByIdAndDni.class);
	}

	public Optional<PrescriptionBo> run(PrescriptionIdentifier prescriptionIdentifier, String identificationNumber) {
		logger.debug("Input parameters -> prescriptionId {}, identificationNumber {}", prescriptionIdentifier, identificationNumber);
		Optional<PrescriptionBo> result = prescriptionStorage.getPrescriptionByIdAndDni(prescriptionIdentifier, identificationNumber);
		logger.debug("Output -> {}", result);
		return result;
	}


}
