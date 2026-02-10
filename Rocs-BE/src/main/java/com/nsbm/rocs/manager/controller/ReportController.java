package com.nsbm.rocs.manager.controller;

import com.nsbm.rocs.manager.service.JasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager/reports")
public class ReportController {

    @Autowired
    private JasperReportService jasperReportService;

    @GetMapping("/approvals/pdf")
    public ResponseEntity<byte[]> downloadApprovalHistoryPdf() {
        try {
            byte[] pdfBytes = jasperReportService.generateApprovalHistoryPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "approval_history.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
