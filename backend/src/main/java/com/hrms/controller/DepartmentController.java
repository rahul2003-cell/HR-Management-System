package com.hrms.controller;
import com.hrms.dto.*;
import com.hrms.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/departments") @RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService svc;
    @GetMapping public ResponseEntity<ApiResponse<List<DepartmentDto.Response>>> getAll() { return ResponseEntity.ok(ApiResponse.ok(svc.getAll())); }
    @GetMapping("/{id}") public ResponseEntity<ApiResponse<DepartmentDto.Response>> getById(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.ok(svc.getById(id))); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<DepartmentDto.Response>> create(@Valid @RequestBody DepartmentDto.Request req) { return ResponseEntity.ok(ApiResponse.ok("Created",svc.create(req))); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','HR')") public ResponseEntity<ApiResponse<DepartmentDto.Response>> update(@PathVariable Long id, @RequestBody DepartmentDto.Request req) { return ResponseEntity.ok(ApiResponse.ok("Updated",svc.update(id,req))); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) { svc.delete(id); return ResponseEntity.ok(ApiResponse.ok("Deleted",null)); }
}