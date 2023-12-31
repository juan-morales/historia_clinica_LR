package net.pladema.nursingreports.service;

import ar.lamansys.sgx.shared.reports.util.struct.IWorkbook;
import net.pladema.nursingreports.repository.NursingEmergencyConsultationDetail;
import net.pladema.nursingreports.repository.NursingHospitalizationConsultationDetail;
import net.pladema.nursingreports.repository.NursingOutpatientConsultationDetail;
import net.pladema.nursingreports.repository.NursingProceduresConsultationDetail;
import net.pladema.nursingreports.repository.NursingVaccineConsultationDetail;

import java.util.List;

public interface NursingReportExcelService {

	IWorkbook buildExcelNursingEmergency(String title, String[] headers, List<NursingEmergencyConsultationDetail> query);

	IWorkbook buildExcelNursingOutpatient(String title, String[] headers, List<NursingOutpatientConsultationDetail> query);

	IWorkbook buildExcelNursingHospitalization(String title, String[] headers, List<NursingHospitalizationConsultationDetail> query);

	IWorkbook buildExcelNursingProcedures(String title, String[] headers, List<NursingProceduresConsultationDetail> query);

	IWorkbook buildExcelNursingVaccine(String title, String[] headers, List<NursingVaccineConsultationDetail> query, String startDate, String endDate, String institutionName);

}
