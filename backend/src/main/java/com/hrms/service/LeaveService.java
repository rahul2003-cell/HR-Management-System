package com.hrms.service;
import com.hrms.dto.LeaveDto;
import com.hrms.entity.*;
import com.hrms.exception.*;
import com.hrms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
@Service @RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository repo;
    private final EmployeeRepository empRepo;
    private final UserRepository userRepo;
    public Page<LeaveDto.Response> getAll(int page, int size) { return repo.findAll(PageRequest.of(page,size,Sort.by("createdAt").descending())).map(this::map); }
    public Page<LeaveDto.Response> getByStatus(Leave.LeaveStatus status, int page, int size) { return repo.findByStatusOrderByCreatedAtDesc(status,PageRequest.of(page,size)).map(this::map); }
    public Page<LeaveDto.Response> getByEmployee(Long empId, int page, int size) { return repo.findByEmployeeIdOrderByCreatedAtDesc(empId,PageRequest.of(page,size)).map(this::map); }
    @Transactional
    public LeaveDto.Response apply(Long empId, LeaveDto.Request req) {
        Employee emp = empRepo.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee",empId));
        long days = ChronoUnit.DAYS.between(req.getFromDate(),req.getToDate())+1;
        Leave leave = Leave.builder().employee(emp).leaveType(req.getLeaveType())
            .fromDate(req.getFromDate()).toDate(req.getToDate()).totalDays((int)days)
            .reason(req.getReason()).status(Leave.LeaveStatus.PENDING).build();
        return map(repo.save(leave));
    }
    @Transactional
    public LeaveDto.Response action(Long id, LeaveDto.ActionRequest req, String adminEmail) {
        Leave leave = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Leave",id));
        leave.setStatus(req.getStatus()); leave.setAdminRemarks(req.getAdminRemarks());
        if(req.getStatus()==Leave.LeaveStatus.APPROVED) {
            leave.setApprovedAt(LocalDateTime.now());
            userRepo.findByEmail(adminEmail).ifPresent(leave::setApprovedBy);
        }
        return map(repo.save(leave));
    }
    private LeaveDto.Response map(Leave l) {
        Employee e = l.getEmployee();
        return LeaveDto.Response.builder().id(l.getId()).employeeId(e.getId()).employeeName(e.getFullName())
            .employeeCode(e.getEmployeeId()).leaveType(l.getLeaveType()).fromDate(l.getFromDate())
            .toDate(l.getToDate()).totalDays(l.getTotalDays()).reason(l.getReason())
            .status(l.getStatus()).adminRemarks(l.getAdminRemarks()).createdAt(l.getCreatedAt()).build();
    }
}