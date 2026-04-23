package com.hrms.repository;
import com.hrms.entity.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LeaveRepository extends JpaRepository<Leave,Long> {
    Page<Leave> findByEmployeeIdOrderByCreatedAtDesc(Long empId, Pageable pageable);
    Page<Leave> findByStatusOrderByCreatedAtDesc(Leave.LeaveStatus status, Pageable pageable);
    List<Leave> findByEmployeeIdAndStatus(Long empId, Leave.LeaveStatus status);
    long countByStatus(Leave.LeaveStatus status);
}
