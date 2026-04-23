package com.hrms.service;

import com.hrms.entity.*;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.*;

// iText PDF - explicit imports (NO wildcard to avoid Font clash)
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Apache POI Excel - explicit imports (NO wildcard to avoid Font clash)
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class ExportService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository empRepo;

    // ── PDF Payslip ────────────────────────────────────────────────────────────
    public byte[] generatePayslipPdf(Long payrollId) {
        Payroll p = payrollRepo.findById(payrollId)
            .orElseThrow(() -> new ResourceNotFoundException("Payroll", payrollId));
        Employee e = p.getEmployee();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // iText Font — no ambiguity now, fully explicit import
            Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(30, 58, 138));
            Font headFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font netFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(21, 128, 61));
            Font footFont   = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);

            Paragraph title = new Paragraph("NEXUS HR — SALARY SLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("Month: " + getMonthName(p.getMonth()) + " " + p.getYear(), headFont));
            doc.add(Chunk.NEWLINE);

            // Employee info table
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            addCell(infoTable, "Employee ID",  boldFont);  addCell(infoTable, e.getEmployeeId(), normalFont);
            addCell(infoTable, "Name",         boldFont);  addCell(infoTable, e.getFullName(), normalFont);
            addCell(infoTable, "Designation",  boldFont);  addCell(infoTable, nvl(e.getDesignation()), normalFont);
            addCell(infoTable, "Department",   boldFont);  addCell(infoTable, e.getDepartment() != null ? e.getDepartment().getName() : "", normalFont);
            addCell(infoTable, "Bank Account", boldFont);  addCell(infoTable, nvl(e.getBankAccount()), normalFont);
            addCell(infoTable, "PAN Number",   boldFont);  addCell(infoTable, nvl(e.getPanNumber()), normalFont);
            doc.add(infoTable);
            doc.add(Chunk.NEWLINE);

            // Earnings & Deductions table
            PdfPTable salTable = new PdfPTable(4);
            salTable.setWidthPercentage(100);
            BaseColor hdrColor = new BaseColor(30, 58, 138);
            addHeaderCell(salTable, "EARNINGS",     hdrColor, headFont);
            addHeaderCell(salTable, "AMOUNT",        hdrColor, headFont);
            addHeaderCell(salTable, "DEDUCTIONS",    hdrColor, headFont);
            addHeaderCell(salTable, "AMOUNT",        hdrColor, headFont);
            addCell(salTable, "Basic Salary",       boldFont); addCell(salTable, fmt(p.getBasicSalary()),    normalFont);
            addCell(salTable, "PF (12%)",           boldFont); addCell(salTable, fmt(p.getPf()),             normalFont);
            addCell(salTable, "HRA",                boldFont); addCell(salTable, fmt(p.getHra()),            normalFont);
            addCell(salTable, "Income Tax",         boldFont); addCell(salTable, fmt(p.getTax()),            normalFont);
            addCell(salTable, "Allowances",         boldFont); addCell(salTable, fmt(p.getAllowances()),     normalFont);
            addCell(salTable, "Other Deductions",   boldFont); addCell(salTable, fmt(p.getOtherDeductions()), normalFont);
            addCell(salTable, "Gross Salary",       boldFont); addCell(salTable, fmt(p.getGrossSalary()),   boldFont);
            addCell(salTable, "Total Deductions",   boldFont); addCell(salTable, fmt(p.getTotalDeductions()), boldFont);
            doc.add(salTable);
            doc.add(Chunk.NEWLINE);

            Paragraph net = new Paragraph("NET SALARY: " + fmt(p.getNetSalary()), netFont);
            net.setAlignment(Element.ALIGN_RIGHT);
            doc.add(net);
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph(
                "Attendance: Present=" + p.getPresentDays() +
                " | Absent=" + p.getAbsentDays() +
                " | Leave=" + p.getLeaveDays(), normalFont));
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("This is a computer-generated payslip. No signature required.", footFont));
            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            log.error("PDF generation error", ex);
            throw new RuntimeException("PDF generation failed: " + ex.getMessage(), ex);
        }
    }

    // ── Excel Payroll ──────────────────────────────────────────────────────────
    public byte[] generatePayrollExcel(int month, int year) {
        List<Payroll> payrolls = payrollRepo.findByMonthAndYearOrderByEmployeeFirstNameAsc(month, year);
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Payroll " + getMonthName(month) + " " + year);

            // Header style — XSSFFont is explicit, no ambiguity
            XSSFCellStyle headerStyle = wb.createCellStyle();
            XSSFFont headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 30, (byte) 58, (byte) 138}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"#", "Emp ID", "Name", "Designation", "Department",
                    "Basic", "HRA", "Allowances", "Gross", "PF", "Tax", "Deductions", "Net Salary",
                    "Present", "Absent", "Status"};
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Alternating row style
            XSSFCellStyle altStyle = wb.createCellStyle();
            altStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 240, (byte) 245, (byte) 255}, null));
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < payrolls.size(); i++) {
                Payroll p = payrolls.get(i);
                Employee emp = p.getEmployee();
                Row row = sheet.createRow(i + 1);
                if (i % 2 == 1) {
                    for (int j = 0; j < headers.length; j++) row.createCell(j).setCellStyle(altStyle);
                }
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(emp.getEmployeeId());
                row.createCell(2).setCellValue(emp.getFullName());
                row.createCell(3).setCellValue(nvl(emp.getDesignation()));
                row.createCell(4).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "");
                row.createCell(5).setCellValue(bd(p.getBasicSalary()));
                row.createCell(6).setCellValue(bd(p.getHra()));
                row.createCell(7).setCellValue(bd(p.getAllowances()));
                row.createCell(8).setCellValue(bd(p.getGrossSalary()));
                row.createCell(9).setCellValue(bd(p.getPf()));
                row.createCell(10).setCellValue(bd(p.getTax()));
                row.createCell(11).setCellValue(bd(p.getTotalDeductions()));
                row.createCell(12).setCellValue(bd(p.getNetSalary()));
                row.createCell(13).setCellValue(p.getPresentDays() != null ? p.getPresentDays() : 0);
                row.createCell(14).setCellValue(p.getAbsentDays() != null ? p.getAbsentDays() : 0);
                row.createCell(15).setCellValue(p.getStatus().name());
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Excel payroll error", e);
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }

    // ── Excel Employees ────────────────────────────────────────────────────────
    public byte[] exportEmployeesExcel() {
        List<Employee> employees = empRepo.findAll();
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Employees");
            String[] headers = {"Emp ID", "First Name", "Last Name", "Email", "Phone",
                    "Designation", "Department", "Status", "Joining Date", "Basic Salary"};

            XSSFCellStyle headerStyle = wb.createCellStyle();
            XSSFFont hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 30, (byte) 58, (byte) 138}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row hRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            for (int i = 0; i < employees.size(); i++) {
                Employee e = employees.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(e.getEmployeeId());
                row.createCell(1).setCellValue(e.getFirstName());
                row.createCell(2).setCellValue(e.getLastName());
                row.createCell(3).setCellValue(e.getEmail());
                row.createCell(4).setCellValue(nvl(e.getPhone()));
                row.createCell(5).setCellValue(nvl(e.getDesignation()));
                row.createCell(6).setCellValue(e.getDepartment() != null ? e.getDepartment().getName() : "");
                row.createCell(7).setCellValue(e.getStatus().name());
                row.createCell(8).setCellValue(e.getJoiningDate() != null ? e.getJoiningDate().toString() : "");
                row.createCell(9).setCellValue(bd(e.getBasicSalary()));
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Employee export failed: " + e.getMessage(), e);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private void addCell(PdfPTable t, String v, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(v, f));
        c.setPadding(5);
        t.addCell(c);
    }

    private void addHeaderCell(PdfPTable t, String v, BaseColor bg, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(v, f));
        c.setBackgroundColor(bg);
        c.setPadding(5);
        t.addCell(c);
    }

    private String fmt(BigDecimal v) {
        return v != null ? "Rs. " + String.format("%,.2f", v) : "Rs. 0.00";
    }

    private double bd(BigDecimal v) {
        return v != null ? v.doubleValue() : 0.0;
    }

    private String nvl(String v) {
        return v != null ? v : "";
    }

    private String getMonthName(int m) {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        return (m >= 1 && m <= 12) ? months[m - 1] : "";
    }
}
