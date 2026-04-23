package com.hrms.controller;
import com.hrms.dto.*;
import com.hrms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/attendance") @RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService svc;
    @GetMapping("/date/{date}") public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> byDate(@PathVariable @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date) { return ResponseEntity.ok(ApiResponse.ok(svc.getByDate(date))); }
    @GetMapping("/employee/{id}") public ResponseEntity<ApiResponse<List<AttendanceDto.Response>>> byEmployee(@PathVariable Long id, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) { return ResponseEntity.ok(ApiResponse.ok(svc.getByEmployeeAndRange(id,from,to))); }
    @GetMapping("/summary/{id}") public ResponseEntity<ApiResponse<AttendanceDto.Summary>> summary(@PathVariable Long id, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) { return ResponseEntity.ok(ApiResponse.ok(svc.getSummary(id,from,to))); }
    @PostMapping("/mark") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<AttendanceDto.Response>> mark(@RequestBody AttendanceDto.MarkRequest req) { return ResponseEntity.ok(ApiResponse.ok("Marked",svc.mark(req))); }
    @PostMapping("/checkin") public ResponseEntity<ApiResponse<AttendanceDto.Response>> checkIn(@RequestBody AttendanceDto.MarkRequest req) { req.setCheckIn(java.time.LocalTime.now()); req.setStatus(com.hrms.entity.Attendance.AttendanceStatus.PRESENT); return ResponseEntity.ok(ApiResponse.ok("Checked in",svc.mark(req))); }
}