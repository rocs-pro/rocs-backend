package com.nsbm.rocs.manager.service;

import com.nsbm.rocs.entity.inventory.GRN;
import com.nsbm.rocs.entity.inventory.Supplier;
import com.nsbm.rocs.inventory.repository.GRNRepository;
import com.nsbm.rocs.inventory.repository.SupplierRepository;
import com.nsbm.rocs.manager.dto.ActivityLogDTO;
import com.nsbm.rocs.manager.dto.ApprovalDTO;
import com.nsbm.rocs.manager.dto.ManagerCustomerDTO;
import com.nsbm.rocs.manager.dto.SalesReportDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JasperReportService {

    @Autowired
    private ManagerService managerService;
    
    @Autowired
    private ManagerCustomerService managerCustomerService;
    
    @Autowired
    private GRNRepository grnRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;

    public byte[] generateApprovalHistoryPdf() throws Exception {
        List<ApprovalDTO> approvals = managerService.getApprovals(null);
        return generatePdf("/reports/approval_history.jrxml", approvals, null);
    }
    
    public byte[] generateSalesReportsPdf(String startDate, String endDate) throws Exception {
        List<SalesReportDTO> reports = managerService.getSalesReports(startDate, endDate);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate);
        parameters.put("endDate", endDate);
        return generatePdf("/reports/sales_report.jrxml", reports, parameters);
    }

    public byte[] generateBranchActivityLogPdf(int limit) throws Exception {
        List<ActivityLogDTO> activities = managerService.getBranchActivityLog(limit);
        return generatePdf("/reports/branch_activity_log.jrxml", activities, null);
    }
    
    public byte[] generateLoyaltyCustomersPdf() throws Exception {
        List<ManagerCustomerDTO> customers = managerCustomerService.getAllCustomers();
        // Since we updated the report to use Strings for points and spend, ManagerCustomerDTO fits directly
        return generatePdf("/reports/loyalty_customers.jrxml", customers, null);
    }
    
    public byte[] generateGrnListPdf() throws Exception {
        List<GRN> grns = grnRepository.findAll();
        Map<Long, String> supplierNames = supplierRepository.findAll().stream()
                .collect(Collectors.toMap(Supplier::getSupplierId, Supplier::getName));

        List<GrnReportDTO> reportData = grns.stream()
                .map(grn -> GrnReportDTO.builder()
                        .grnNo(grn.getGrnNo())
                        .supplierName(supplierNames.getOrDefault(grn.getSupplierId(), "Unknown"))
                        .grnDate(grn.getGrnDate() != null ? grn.getGrnDate().toString() : "")
                        .totalAmount(grn.getTotalAmount() != null ? grn.getTotalAmount() : BigDecimal.ZERO)
                        .status(grn.getStatus())
                        .build())
                .collect(Collectors.toList());

        return generatePdf("/reports/inventory_grn_list.jrxml", reportData, null);
    }

    private byte[] generatePdf(String templatePath, List<?> data, Map<String, Object> parameters) throws Exception {
        InputStream reportStream = getClass().getResourceAsStream(templatePath);
        if (reportStream == null) {
            throw new RuntimeException("Report design file not found: " + templatePath);
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put("createdBy", "ROCS System");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrnReportDTO {
        private String grnNo;
        private String supplierName;
        private String grnDate;
        private BigDecimal totalAmount;
        private String status;
    }
}
