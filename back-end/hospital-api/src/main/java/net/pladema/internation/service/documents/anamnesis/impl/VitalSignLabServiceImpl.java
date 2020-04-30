package net.pladema.internation.service.documents.anamnesis.impl;

import net.pladema.internation.repository.ips.ObservationLabRepository;
import net.pladema.internation.repository.ips.ObservationVitalSignRepository;
import net.pladema.internation.repository.ips.entity.ObservationLab;
import net.pladema.internation.repository.ips.entity.ObservationVitalSign;
import net.pladema.internation.service.documents.DocumentService;
import net.pladema.internation.service.documents.anamnesis.VitalSignLabService;
import net.pladema.internation.service.domain.ips.AnthropometricDataBo;
import net.pladema.internation.service.domain.ips.ClinicalObservationBo;
import net.pladema.internation.service.domain.ips.MapVitalSigns;
import net.pladema.internation.service.domain.ips.VitalSignBo;
import net.pladema.internation.service.domain.ips.enums.EObservationLab;
import net.pladema.internation.service.domain.ips.enums.EVitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VitalSignLabServiceImpl implements VitalSignLabService {

    public static final String OUTPUT = "Output -> {}";

    private static final Logger LOG = LoggerFactory.getLogger(VitalSignLabServiceImpl.class);

    private final ObservationVitalSignRepository observationVitalSignRepository;

    private final ObservationLabRepository observationLabRepository;

    private final DocumentService documentService;

    public VitalSignLabServiceImpl(ObservationVitalSignRepository observationVitalSignRepository,
                                   ObservationLabRepository observationLabRepository,
                                   DocumentService documentService) {
        this.observationVitalSignRepository = observationVitalSignRepository;
        this.observationLabRepository = observationLabRepository;
        this.documentService = documentService;
    }

    @Override
    public VitalSignBo loadVitalSigns(Integer patientId, Long documentId, Optional<VitalSignBo> optVitalSigns) {
        LOG.debug("Input parameters -> documentId {}, patientId {}, optVitalSigns {}", documentId, patientId, optVitalSigns);
        optVitalSigns.ifPresent(vitalSign -> {
            if(mustSaveClinicalObservation(vitalSign.getSystolicBloodPressure())){
                ObservationVitalSign systolicBloodPressure = createObservationVitalSign(patientId,
                        vitalSign.getSystolicBloodPressure(), EVitalSign.SYSTOLIC_BLOOD_PRESSURE);
                documentService.createDocumentVitalSign(documentId, systolicBloodPressure.getId());
                vitalSign.setSystolicBloodPressure(createObservationFromVitalSign(systolicBloodPressure));
            }

            if(mustSaveClinicalObservation(vitalSign.getDiastolicBloodPressure())) {
                ObservationVitalSign diastolicBloodPressure = createObservationVitalSign(patientId,
                        vitalSign.getDiastolicBloodPressure(), EVitalSign.DIASTOLIC_BLOOD_PRESSURE);
                documentService.createDocumentVitalSign(documentId, diastolicBloodPressure.getId());
                vitalSign.setDiastolicBloodPressure(createObservationFromVitalSign(diastolicBloodPressure));
            }

            if(mustSaveClinicalObservation(vitalSign.getMeanPressure())) {
                ObservationVitalSign meanPressure = createObservationVitalSign(patientId,
                        vitalSign.getMeanPressure(), EVitalSign.MEAN_PRESSURE);
                documentService.createDocumentVitalSign(documentId, meanPressure.getId());
                vitalSign.setMeanPressure(createObservationFromVitalSign(meanPressure));
            }

            if(mustSaveClinicalObservation(vitalSign.getTemperature())) {
                ObservationVitalSign temperature = createObservationVitalSign(patientId,
                        vitalSign.getTemperature(), EVitalSign.TEMPERATURE);
                documentService.createDocumentVitalSign(documentId, temperature.getId());
                vitalSign.setTemperature(createObservationFromVitalSign(temperature));
            }

            if(mustSaveClinicalObservation(vitalSign.getHeartRate())) {
                ObservationVitalSign heartRate = createObservationVitalSign(patientId,
                        vitalSign.getHeartRate(), EVitalSign.HEART_RATE);
                documentService.createDocumentVitalSign(documentId, heartRate.getId());
                vitalSign.setHeartRate(createObservationFromVitalSign(heartRate));
            }

            if(mustSaveClinicalObservation(vitalSign.getRespiratoryRate())) {
                ObservationVitalSign respiratoryRate = createObservationVitalSign(patientId,
                        vitalSign.getRespiratoryRate(), EVitalSign.RESPIRATORY_RATE);
                documentService.createDocumentVitalSign(documentId, respiratoryRate.getId());
                vitalSign.setRespiratoryRate(createObservationFromVitalSign(respiratoryRate));
            }

            if(mustSaveClinicalObservation(vitalSign.getBloodOxygenSaturation())) {
                ObservationVitalSign bloodOxygenSaturation = createObservationVitalSign(patientId,
                        vitalSign.getBloodOxygenSaturation(), EVitalSign.BLOOD_OXYGEN_SATURATION);
                documentService.createDocumentVitalSign(documentId, bloodOxygenSaturation.getId());
                vitalSign.setBloodOxygenSaturation(createObservationFromVitalSign(bloodOxygenSaturation));
            }
        });
        LOG.debug(OUTPUT, optVitalSigns);
        return optVitalSigns.orElse(null);
    }

    @Override
    public AnthropometricDataBo loadAnthropometricData(Integer patientId, Long documentId, Optional<AnthropometricDataBo> optAnthropometricDatas) {
        LOG.debug("Input parameters -> documentId {}, patientId {}, optAnthropometricDatas {}", documentId, patientId, optAnthropometricDatas);
        optAnthropometricDatas.ifPresent(anthropometricData -> {
            if(mustSaveClinicalObservation(anthropometricData.getHeight())) {
                ObservationVitalSign height = createObservationVitalSign(patientId, anthropometricData.getHeight(),
                        EVitalSign.HEIGHT);
                documentService.createDocumentVitalSign(documentId, height.getId());
                anthropometricData.setHeight(createObservationFromVitalSign(height));
            }

            if(mustSaveClinicalObservation(anthropometricData.getWeight())) {
                ObservationVitalSign weight = createObservationVitalSign(patientId, anthropometricData.getWeight(),
                        EVitalSign.WEIGHT);
                documentService.createDocumentVitalSign(documentId, weight.getId());
                anthropometricData.setWeight(createObservationFromVitalSign(weight));
            }

            if(mustSaveClinicalObservation(anthropometricData.getBloodType())) {
                ObservationLab bloodType = createObservationLab(patientId, anthropometricData.getBloodType(),
                        EObservationLab.BLOOD_TYPE);
                documentService.createDocumentLab(documentId, bloodType.getId());
                anthropometricData.setBloodType(createObservationFromLab(bloodType));
            }
        });
        LOG.debug(OUTPUT, optAnthropometricDatas);
        return optAnthropometricDatas.orElse(null);
    }

    private boolean mustSaveClinicalObservation(ClinicalObservationBo co) {
        return co != null;
    }

    private ObservationVitalSign createObservationVitalSign(Integer patientId, ClinicalObservationBo observation, EVitalSign eVitalSign) {
        LOG.debug("Input parameters -> patientId {}, ClinicalObservation {}, eVitalSign {}", patientId, observation, eVitalSign);
        ObservationVitalSign observationVitalSign = observationVitalSignRepository.save(
                new ObservationVitalSign(patientId, observation.getValue(), eVitalSign));
        LOG.debug(OUTPUT, observationVitalSign);
        return observationVitalSign;
    }

    private ObservationLab createObservationLab(Integer patientId, ClinicalObservationBo observation, EObservationLab eObservationLab) {
        LOG.debug("Input parameters -> patientId {}, ClinicalObservation {}, eLab {}", patientId, observation, eObservationLab);
        ObservationLab observationLab = observationLabRepository.save(
                new ObservationLab(patientId, observation.getValue(), eObservationLab));
        LOG.debug(OUTPUT, observationLab);
        return observationLab;
    }


    private ClinicalObservationBo createObservationFromVitalSign(ObservationVitalSign vitalSign) {
        LOG.debug("Input parameters -> VitalSign {}", vitalSign);
        ClinicalObservationBo result = new ClinicalObservationBo(vitalSign.getId(), vitalSign.getValue());
        LOG.debug(OUTPUT, result);
        return result;
    }

    private ClinicalObservationBo createObservationFromLab(ObservationLab lab) {
        LOG.debug("Input parameters -> ObservationLab {}", lab);
        ClinicalObservationBo result = new ClinicalObservationBo(lab.getId(), lab.getValue());
        LOG.debug(OUTPUT, result);
        return result;
    }

    @Override
    public List<AnthropometricDataBo> getAnthropometricDataGeneralState(Integer internmentEpisodeId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
        return Collections.emptyList();
    }

    @Override
    public List<VitalSignBo> getLast2VitalSignsGeneralState(Integer internmentEpisodeId) {
        LOG.debug("Input parameters -> internmentEpisodeId {}", internmentEpisodeId);
        MapVitalSigns resultQuery = observationVitalSignRepository.getVitalSignsGeneralStateLastSevenDays(internmentEpisodeId);
        List<VitalSignBo> result = new ArrayList<>();
        for (int i=0;i<2;i++){
            loadVitalSignBo(resultQuery, i).ifPresent(v -> {
                result.add(v);
            });
        }
        LOG.debug(OUTPUT, result);
        return result;
    }

    private Optional<VitalSignBo> loadVitalSignBo(MapVitalSigns mapVitalSigns, int i) {
        LOG.debug("Input parameters -> mapVitalSigns {}, pos {}", mapVitalSigns, i);
        VitalSignBo vitalSignBo = new VitalSignBo();
        mapVitalSigns.getLastNVitalSign(EVitalSign.BLOOD_OXYGEN_SATURATION.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setBloodOxygenSaturation(new ClinicalObservationBo(v));
        });
        mapVitalSigns.getLastNVitalSign(EVitalSign.DIASTOLIC_BLOOD_PRESSURE.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setDiastolicBloodPressure(new ClinicalObservationBo(v));
        });
        mapVitalSigns.getLastNVitalSign(EVitalSign.SYSTOLIC_BLOOD_PRESSURE.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setSystolicBloodPressure(new ClinicalObservationBo(v));
        });
        mapVitalSigns.getLastNVitalSign(EVitalSign.HEART_RATE.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setHeartRate(new ClinicalObservationBo(v));
        });
        mapVitalSigns.getLastNVitalSign(EVitalSign.TEMPERATURE.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setTemperature(new ClinicalObservationBo(v));
        });
        mapVitalSigns.getLastNVitalSign(EVitalSign.RESPIRATORY_RATE.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setRespiratoryRate(new ClinicalObservationBo(v));
        });
        mapVitalSigns.getLastNVitalSign(EVitalSign.MEAN_PRESSURE.getSctidCode(),i).ifPresent(v -> {
            vitalSignBo.setMeanPressure(new ClinicalObservationBo(v));
        });
        LOG.debug(OUTPUT, vitalSignBo);
        if (vitalSignBo.hasValues())
            return Optional.of(vitalSignBo);
        return Optional.empty();
    }


}
