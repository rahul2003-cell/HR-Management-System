package com.hrms.service;
import com.hrms.dto.EmployeeDto;
import com.hrms.entity.*;
import com.hrms.exception.*;
import com.hrms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Service @RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository empRepo;
    private final DepartmentRepository deptRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public Page<EmployeeDto.Response> getAll(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return empRepo.findAll(pageable).map(this::map);
    }
    public Page<EmployeeDto.Response> search(String q, int page, int size) {
        return empRepo.search(q, PageRequest.of(page,size)).map(this::map);
    }
    public EmployeeDto.Response getById(Long id) {
        return map(empRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee",id)));
    }
    public EmployeeDto.Response getByEmployeeId(String empId) {
        return map(empRepo.findByEmployeeId(empId).orElseThrow(()->new ResourceNotFoundException("Employee not found: "+empId)));
    }
    @Transactional
    public EmployeeDto.Response create(EmployeeDto.Request req) {
        if(empRepo.findByEmail(req.getEmail()).isPresent()) throw new BadRequestException("Email already in use");
        Department dept = deptRepo.findById(req.getDepartmentId()).orElseThrow(()->new ResourceNotFoundException("Department",req.getDepartmentId()));
        String empId = generateEmployeeId(dept);
        // Create user account
        User user = User.builder().email(req.getEmail())
            .password(encoder.encode(req.getPassword()!=null?req.getPassword():"Hrms@123"))
            .role(User.Role.EMPLOYEE).build();
        user = userRepo.save(user);
        Employee emp = Employee.builder()
            .employeeId(empId).firstName(req.getFirstName()).lastName(req.getLastName())
            .email(req.getEmail()).phone(req.getPhone()).address(req.getAddress())
            .city(req.getCity()).state(req.getState()).pincode(req.getPincode())
            .dateOfBirth(req.getDateOfBirth()).joiningDate(req.getJoiningDate()!=null?req.getJoiningDate():LocalDate.now())
            .designation(req.getDesignation()).basicSalary(req.getBasicSalary()).hra(req.getHra())
            .allowances(req.getAllowances()).deductions(req.getDeductions())
            .profileImageUrl(req.getProfileImageUrl()).panNumber(req.getPanNumber())
            .aadharNumber(req.getAadharNumber()).bankAccount(req.getBankAccount())
            .bankName(req.getBankName()).ifscCode(req.getIfscCode())
            .gender(req.getGender()!=null?req.getGender():Employee.Gender.OTHER)
            .employmentType(req.getEmploymentType()!=null?req.getEmploymentType():Employee.EmploymentType.FULL_TIME)
            .department(dept).user(user).status(Employee.Status.ACTIVE).build();
        return map(empRepo.save(emp));
    }
    @Transactional
    public EmployeeDto.Response update(Long id, EmployeeDto.UpdateRequest req) {
        Employee emp = empRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee",id));
        if(req.getFirstName()!=null) emp.setFirstName(req.getFirstName());
        if(req.getLastName()!=null) emp.setLastName(req.getLastName());
        if(req.getPhone()!=null) emp.setPhone(req.getPhone());
        if(req.getAddress()!=null) emp.setAddress(req.getAddress());
        if(req.getCity()!=null) emp.setCity(req.getCity());
        if(req.getState()!=null) emp.setState(req.getState());
        if(req.getPincode()!=null) emp.setPincode(req.getPincode());
        if(req.getDesignation()!=null) emp.setDesignation(req.getDesignation());
        if(req.getBasicSalary()!=null) emp.setBasicSalary(req.getBasicSalary());
        if(req.getHra()!=null) emp.setHra(req.getHra());
        if(req.getAllowances()!=null) emp.setAllowances(req.getAllowances());
        if(req.getDeductions()!=null) emp.setDeductions(req.getDeductions());
        if(req.getProfileImageUrl()!=null) emp.setProfileImageUrl(req.getProfileImageUrl());
        if(req.getPanNumber()!=null) emp.setPanNumber(req.getPanNumber());
        if(req.getAadharNumber()!=null) emp.setAadharNumber(req.getAadharNumber());
        if(req.getBankAccount()!=null) emp.setBankAccount(req.getBankAccount());
        if(req.getBankName()!=null) emp.setBankName(req.getBankName());
        if(req.getIfscCode()!=null) emp.setIfscCode(req.getIfscCode());
        if(req.getGender()!=null) emp.setGender(req.getGender());
        if(req.getEmploymentType()!=null) emp.setEmploymentType(req.getEmploymentType());
        if(req.getStatus()!=null) emp.setStatus(req.getStatus());
        if(req.getDepartmentId()!=null) {
            Department dept = deptRepo.findById(req.getDepartmentId()).orElseThrow(()->new ResourceNotFoundException("Department",req.getDepartmentId()));
            emp.setDepartment(dept);
        }
        return map(empRepo.save(emp));
    }
    @Transactional
    public void delete(Long id) {
        Employee emp = empRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee",id));
        emp.setStatus(Employee.Status.TERMINATED);
        empRepo.save(emp);
    }
    private String generateEmployeeId(Department dept) {
        String prefix = dept.getName().substring(0,Math.min(3,dept.getName().length())).toUpperCase();
        long count = empRepo.countByDepartmentId(dept.getId()) + 1;
        return prefix + String.format("%04d", count);
    }
    public EmployeeDto.Response map(Employee e) {
        return EmployeeDto.Response.builder()
            .id(e.getId()).employeeId(e.getEmployeeId()).firstName(e.getFirstName()).lastName(e.getLastName())
            .fullName(e.getFullName()).email(e.getEmail()).phone(e.getPhone()).address(e.getAddress())
            .city(e.getCity()).state(e.getState()).pincode(e.getPincode())
            .dateOfBirth(e.getDateOfBirth()).joiningDate(e.getJoiningDate())
            .designation(e.getDesignation()).basicSalary(e.getBasicSalary()).hra(e.getHra())
            .allowances(e.getAllowances()).deductions(e.getDeductions())
            .grossSalary(e.getGrossSalary()).netSalary(e.getNetSalary())
            .profileImageUrl(e.getProfileImageUrl()).panNumber(e.getPanNumber())
            .aadharNumber(e.getAadharNumber()).bankAccount(e.getBankAccount())
            .bankName(e.getBankName()).ifscCode(e.getIfscCode())
            .gender(e.getGender()).employmentType(e.getEmploymentType()).status(e.getStatus())
            .departmentId(e.getDepartment()!=null?e.getDepartment().getId():null)
            .departmentName(e.getDepartment()!=null?e.getDepartment().getName():null)
            .createdAt(e.getCreatedAt()).build();
    }
}