package com.hrms.controller;
import com.hrms.dto.*;
import com.hrms.entity.Leave;
import com.hrms.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/leaves") @RequiredArgsConstructor
public class LeaveController {
    private final LeaveService svc;
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<Page<LeaveDto.Response>>> getAll(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) { return ResponseEntity.ok(ApiResponse.ok(svc.getAll(page,size))); }
    @GetMapping("/pending") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<Page<LeaveDto.Response>>> pending(@RequestParam(defaultValue="0") int page) { return ResponseEntity.ok(ApiResponse.ok(svc.getByStatus(Leave.LeaveStatus.PENDING,page,20))); }
    @GetMapping("/employee/{id}") public ResponseEntity<ApiResponse<Page<LeaveDto.Response>>> byEmployee(@PathVariable Long id, @RequestParam(defaultValue="0") int page) { return ResponseEntity.ok(ApiResponse.ok(svc.getByEmployee(id,page,10))); }
    @PostMapping("/apply/{empId}") public ResponseEntity<ApiResponse<LeaveDto.Response>> apply(@PathVariable Long empId, @RequestBody LeaveDto.Request req) { return ResponseEntity.ok(ApiResponse.ok("Leave applied",svc.apply(empId,req))); }
    @PatchMapping("/{id}/action") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<LeaveDto.Response>> action(@PathVariable Long id, @RequestBody LeaveDto.ActionRequest req, @AuthenticationPrincipal UserDetails ud) { return ResponseEntity.ok(ApiResponse.ok("Leave "+req.getStatus().name().toLowerCase(),svc.action(id,req,ud.getUsername()))); }
}