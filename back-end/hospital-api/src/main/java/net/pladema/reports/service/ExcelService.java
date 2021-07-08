package net.pladema.reports.service;

import ar.lamansys.sgx.shared.reports.util.struct.IWorkbook;
import net.pladema.reports.repository.OutpatientDetail;

import java.util.List;

public interface ExcelService {

    IWorkbook buildExcelFromQuery(String title, String[] headers, List<OutpatientDetail> query);
}
