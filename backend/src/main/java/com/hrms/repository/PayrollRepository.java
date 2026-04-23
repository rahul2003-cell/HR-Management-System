package com.hrms.repository;
import com.hrms.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface PayrollRepository extends JpaRepository<Payroll,Long> {
    Optional<Payroll> findByEmployeeIdAndMonthAndYear(Long empId, int month, int year);
    List<Payroll> findByEmployeeIdOrderByYearDescMonthDesc(Long empId);
    List<Payroll> findByMonthAndYearOrderByEmployeeFirstNameAsc(int month, int year);
    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.status='PAID'")
    BigDecimal getTotalPaidSalary();
    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.month=:month AND p.year=:year")
    BigDecimal getMonthlyPayroll(int month, int year);
}
