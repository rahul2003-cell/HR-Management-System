package com.hrms.controller;
import com.hrms.dto.*;
import com.hrms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/employees") @RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService svc;
    @GetMapping public ResponseEntity<ApiResponse<Page<EmployeeDto.Response>>> getAll(
        @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size,
        @RequestParam(defaultValue="createdAt") String sort) {
        return ResponseEntity.ok(ApiResponse.ok(svc.getAll(page,size,sort)));
    }
    @GetMapping("/search") public ResponseEntity<ApiResponse<Page<EmployeeDto.Response>>> search(
        @RequestParam String q, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(svc.search(q,page,size)));
    }
    @GetMapping("/{id}") public ResponseEntity<ApiResponse<EmployeeDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(svc.getById(id)));
    }
    @GetMapping("/me") public ResponseEntity<ApiResponse<EmployeeDto.Response>> getMe(@AuthenticationPrincipal UserDetails ud) {
        var emp = svc.search(ud.getUsername(),0,1).getContent();
        if(emp.isEmpty()) return ResponseEntity.ok(ApiResponse.fail("Profile not found"));
        return ResponseEntity.ok(ApiResponse.ok(emp.get(0)));
    }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<EmployeeDto.Response>> create(@Valid @RequestBody EmployeeDto.Request req) {
        return ResponseEntity.ok(ApiResponse.ok("Employee created", svc.create(req)));
    }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<ApiResponse<EmployeeDto.Response>> update(@PathVariable Long id, @RequestBody EmployeeDto.UpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated", svc.update(id,req)));
    }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        svc.delete(id); return ResponseEntity.ok(ApiResponse.ok("Employee terminated", null));
    }
}