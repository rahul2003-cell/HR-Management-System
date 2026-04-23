package com.hrms.service;
import com.hrms.dto.DepartmentDto;
import com.hrms.entity.Department;
import com.hrms.exception.*;
import com.hrms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repo;
    private final EmployeeRepository empRepo;
    public List<DepartmentDto.Response> getAll() {
        return repo.findAll().stream().map(this::map).toList();
    }
    public DepartmentDto.Response getById(Long id) {
        return map(repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Department",id)));
    }
    public DepartmentDto.Response create(DepartmentDto.Request req) {
        if(repo.existsByName(req.getName())) throw new BadRequestException("Department already exists: "+req.getName());
        return map(repo.save(Department.builder().name(req.getName()).description(req.getDescription()).headName(req.getHeadName()).build()));
    }
    public DepartmentDto.Response update(Long id, DepartmentDto.Request req) {
        Department d = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Department",id));
        d.setName(req.getName()); d.setDescription(req.getDescription()); d.setHeadName(req.getHeadName());
        return map(repo.save(d));
    }
    public void delete(Long id) {
        Department d = repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Department",id));
        d.setActive(false); repo.save(d);
    }
    private DepartmentDto.Response map(Department d) {
        return DepartmentDto.Response.builder().id(d.getId()).name(d.getName())
            .description(d.getDescription()).headName(d.getHeadName()).active(d.isActive())
            .employeeCount(empRepo.countByDepartmentId(d.getId())).build();
    }
}