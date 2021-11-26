package net.pladema.snvs.infrastructure.output.repository.report;

import net.pladema.snvs.application.ports.report.ReportStorage;
import net.pladema.snvs.domain.report.SnvsReportBo;
import org.springframework.stereotype.Service;


@Service
public class ReportStorageImpl implements ReportStorage {

    private final SnvsReportRepository snvsReportRepository;

    public ReportStorageImpl(SnvsReportRepository snvsReportRepository) {
        this.snvsReportRepository = snvsReportRepository;
    }

    @Override
    public SnvsReportBo save(SnvsReportBo snvsReportBo) {
        SnvsReport entity = mapEntity(snvsReportBo);
        entity = snvsReportRepository.save(entity);
        snvsReportBo.setId(entity.getId());
        return snvsReportBo;
    }

    private SnvsReport mapEntity(SnvsReportBo snvsReportBo) {
        var result = new SnvsReport();
        result.setEventId(snvsReportBo.getEventId());
        result.setGroupEventId(snvsReportBo.getGroupEventId());
        result.setManualClassificationId(snvsReportBo.getManualClassificationId());
        result.setPatientId(snvsReportBo.getPatientId());
        result.setInstitutionId(snvsReportBo.getInstitutionId());
        result.setResponseCode(snvsReportBo.getResponseCode());
        result.setLastUpdate(snvsReportBo.getLastUpdate());
        result.setProfessionalId(snvsReportBo.getProfessionalId());
        result.setSisaRegisteredId(snvsReportBo.getSisaRegisteredId());
        result.setStatus(snvsReportBo.getStatus());
        result.setId(snvsReportBo.getId());
        return result;
    }
}
