package com.nsbm.rocs.manager.service;

import com.nsbm.rocs.manager.dto.ApprovalDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {

    @Autowired
    private ManagerService managerService;

    public byte[] generateApprovalHistoryPdf() throws Exception {
        List<ApprovalDTO> approvals = managerService.getApprovals(null);
        
        // Load file from resources
        InputStream reportStream = getClass().getResourceAsStream("/reports/approval_history.jrxml");
        if (reportStream == null) {
            throw new RuntimeException("Report design file not found: /reports/approval_history.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(approvals);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Manager");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
