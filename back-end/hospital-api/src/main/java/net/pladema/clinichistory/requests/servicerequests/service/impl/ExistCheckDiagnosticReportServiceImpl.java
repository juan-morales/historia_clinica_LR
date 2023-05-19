package net.pladema.clinichistory.requests.servicerequests.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.pladema.clinichistory.requests.servicerequests.service.ExistCheckDiagnosticReportService;
import net.pladema.medicalconsultation.appointment.repository.AppointmentOrderImageRepository;

@Service
public class ExistCheckDiagnosticReportServiceImpl implements ExistCheckDiagnosticReportService {

    private final AppointmentOrderImageRepository appointmentOrderImageRepository;

    private static final Logger LOG = LoggerFactory.getLogger(ExistCheckDiagnosticReportServiceImpl.class);
    private final String OUTPUT = "Output -> {}";

    public ExistCheckDiagnosticReportServiceImpl(AppointmentOrderImageRepository appointmentOrderImageRepository){
        this.appointmentOrderImageRepository = appointmentOrderImageRepository;
    }
    @Override
    public Boolean execute(Integer diagnosticReportId) {
        LOG.debug("Input: diagnosticReportId: {}", diagnosticReportId);
        Optional<Integer> drOpt = appointmentOrderImageRepository.findById(diagnosticReportId);
        if (drOpt.isPresent())
            return true;
        return false;
    }
}
