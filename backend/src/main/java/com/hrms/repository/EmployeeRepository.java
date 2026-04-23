package com.hrms.repository;
import com.hrms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByEmployeeId(String empId);
    Optional<Employee> findByUserId(Long userId);
    Page<Employee> findByStatus(Employee.Status status, Pageable pageable);
    Page<Employee> findByDepartmentId(Long deptId, Pageable pageable);
    List<Employee> findByStatusAndDepartmentId(Employee.Status status, Long deptId);
    @Query("SELECT e FROM Employee e WHERE e.status='ACTIVE' AND (" +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Employee> search(@Param("q") String q, Pageable pageable);
    long countByStatus(Employee.Status status);
    long countByDepartmentId(Long deptId);
}