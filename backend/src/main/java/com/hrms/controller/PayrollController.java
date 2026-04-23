package com.hrms.controller;
import com.hrms.dto.*;
import com.hrms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/payroll") @RequiredArgsConstructor
public class PayrollController {
    private final PayrollService svc;
    private final ExportService exportSvc;
    @GetMapping("/employee/{id}") public ResponseEntity<ApiResponse<List<PayrollDto.Response>>> byEmployee(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.ok(svc.getByEmployee(id))); }
    @GetMapping("/monthly") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<List<PayrollDto.Response>>> monthly(@RequestParam int month, @RequestParam int year) { return ResponseEntity.ok(ApiResponse.ok(svc.getMonthly(month,year))); }
    @PostMapping("/generate") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<List<PayrollDto.Response>>> generate(@RequestBody PayrollDto.GenerateRequest req) { return ResponseEntity.ok(ApiResponse.ok("Payroll generated",svc.generate(req))); }
    @PatchMapping("/{id}/pay") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<PayrollDto.Response>> pay(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.ok("Marked as paid",svc.markPaid(id))); }
    @GetMapping("/{id}/slip/pdf") public ResponseEntity<byte[]> downloadSlip(@PathVariable Long id) {
        byte[] pdf = exportSvc.generatePayslipPdf(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=payslip-"+id+".pdf").body(pdf);
    }
    @GetMapping("/monthly/excel") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<byte[]> downloadExcel(@RequestParam int month, @RequestParam int year) {
        byte[] excel = exportSvc.generatePayrollExcel(month,year);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=payroll-"+month+"-"+year+".xlsx").body(excel);
    }
}