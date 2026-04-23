package com.hrms.config;
import com.hrms.entity.*;
import com.hrms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Component @RequiredArgsConstructor @Slf4j
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepo; private final DepartmentRepository deptRepo;
    private final EmployeeRepository empRepo; private final AttendanceRepository attRepo;
    private final PayrollRepository payrollRepo; private final LeaveRepository leaveRepo;
    private final PasswordEncoder encoder;
    @Override public void run(String... args) {
        if(userRepo.count()>0){ log.info("Data already seeded."); return; }
        log.info("Seeding HRMS data...");
        // Departments
        Department eng = deptRepo.save(Department.builder().name("Engineering").description("Software development team").headName("Rajesh Kumar").build());
        Department hr = deptRepo.save(Department.builder().name("Human Resources").description("HR and talent management").headName("Priya Sharma").build());
        Department fin = deptRepo.save(Department.builder().name("Finance").description("Finance and accounts").headName("Amit Patel").build());
        Department mkt = deptRepo.save(Department.builder().name("Marketing").description("Marketing and growth").headName("Deepa Singh").build());
        Department ops = deptRepo.save(Department.builder().name("Operations").description("Operations and admin").headName("Suresh Rao").build());
        // Admin user
        User adminUser = userRepo.save(User.builder().email("admin@nexushr.com").password(encoder.encode("admin123")).role(User.Role.ADMIN).build());
        // Employees
        seedEmployee("Rajesh","Kumar","rajesh.kumar@nexushr.com","9876543210",eng,adminUser,"Senior Engineer","ENG0001",75000,25000,10000,0,LocalDate.of(2021,3,15),"MALE",true);
        User hrUser = createUser("priya.sharma@nexushr.com","admin123",User.Role.HR);
        seedEmployeeWithUser("Priya","Sharma","priya.sharma@nexushr.com","9876543211",hr,hrUser,"HR Manager","HUM0001",65000,20000,8000,0,LocalDate.of(2020,7,1),"FEMALE",false);
        createAndSeedEmp("Amit","Patel","amit.patel@nexushr.com","9876543212",fin,null,"Finance Manager","FIN0001",70000,22000,9000,0,LocalDate.of(2020,1,15),"MALE");
        createAndSeedEmp("Deepa","Singh","deepa.singh@nexushr.com","9876543213",mkt,null,"Marketing Head","MAR0001",68000,21000,8500,0,LocalDate.of(2021,6,1),"FEMALE");
        createAndSeedEmp("Suresh","Rao","suresh.rao@nexushr.com","9876543214",ops,null,"Operations Manager","OPS0001",60000,18000,7000,0,LocalDate.of(2020,9,1),"MALE");
        createAndSeedEmp("Ananya","Iyer","ananya.iyer@nexushr.com","9876543215",eng,null,"Software Engineer","ENG0002",55000,17000,6000,0,LocalDate.of(2022,4,1),"FEMALE");
        createAndSeedEmp("Vikram","Nair","vikram.nair@nexushr.com","9876543216",eng,null,"Tech Lead","ENG0003",80000,26000,12000,2000,LocalDate.of(2019,11,1),"MALE");
        createAndSeedEmp("Kavya","Reddy","kavya.reddy@nexushr.com","9876543217",mkt,null,"Digital Marketer","MAR0002",45000,14000,5000,0,LocalDate.of(2022,8,1),"FEMALE");
        createAndSeedEmp("Rahul","Gupta","rahul.gupta@nexushr.com","9876543218",fin,null,"Accountant","FIN0002",48000,15000,5500,0,LocalDate.of(2021,12,1),"MALE");
        createAndSeedEmp("Meera","Pillai","meera.pillai@nexushr.com","9876543219",hr,null,"HR Executive","HUM0002",42000,13000,4500,0,LocalDate.of(2022,2,1),"FEMALE");
        createAndSeedEmp("Arjun","Mehta","arjun.mehta@nexushr.com","9876543220",eng,null,"DevOps Engineer","ENG0004",70000,22000,9000,0,LocalDate.of(2021,5,1),"MALE");
        createAndSeedEmp("Sneha","Joshi","sneha.joshi@nexushr.com","9876543221",ops,null,"Operations Executive","OPS0002",38000,12000,4000,0,LocalDate.of(2023,1,1),"FEMALE");
        // Seed attendance for today
        empRepo.findAll().forEach(emp->{
            try { attRepo.save(Attendance.builder().employee(emp).attendanceDate(LocalDate.now()).checkIn(LocalTime.of(9,0)).status(Attendance.AttendanceStatus.PRESENT).build()); } catch(Exception ignored){}
        });
        // Seed a pending leave
        empRepo.findByEmployeeId("ENG0002").ifPresent(emp->{
            leaveRepo.save(Leave.builder().employee(emp).leaveType(Leave.LeaveType.SICK).fromDate(LocalDate.now().plusDays(3)).toDate(LocalDate.now().plusDays(4)).totalDays(2).reason("Not feeling well").status(Leave.LeaveStatus.PENDING).build());
        });
        log.info("HRMS seeded! Admin: admin@nexushr.com / admin123 | HR: priya.sharma@nexushr.com / admin123");
    }
    private User createUser(String email, String pass, User.Role role) {
        return userRepo.save(User.builder().email(email).password(encoder.encode(pass)).role(role).build());
    }
    private void seedEmployee(String fn,String ln,String email,String phone,Department dept,User user,String desig,String empId,double basic,double hra,double allow,double ded,LocalDate join,String gender,boolean isAdmin) {
        Employee emp = Employee.builder().employeeId(empId).firstName(fn).lastName(ln).email(email).phone(phone)
            .designation(desig).basicSalary(BigDecimal.valueOf(basic)).hra(BigDecimal.valueOf(hra))
            .allowances(BigDecimal.valueOf(allow)).deductions(BigDecimal.valueOf(ded))
            .joiningDate(join).department(dept).user(user).status(Employee.Status.ACTIVE)
            .gender(gender.equals("MALE")?Employee.Gender.MALE:Employee.Gender.FEMALE)
            .employmentType(Employee.EmploymentType.FULL_TIME).build();
        empRepo.save(emp);
    }
    private void seedEmployeeWithUser(String fn,String ln,String email,String phone,Department dept,User user,String desig,String empId,double basic,double hra,double allow,double ded,LocalDate join,String gender,boolean isAdmin) {
        seedEmployee(fn,ln,email,phone,dept,user,desig,empId,basic,hra,allow,ded,join,gender,isAdmin);
    }
    private void createAndSeedEmp(String fn,String ln,String email,String phone,Department dept,User user,String desig,String empId,double basic,double hra,double allow,double ded,LocalDate join,String gender) {
        User u = user!=null?user:createUser(email,"Hrms@123",User.Role.EMPLOYEE);
        seedEmployee(fn,ln,email,phone,dept,u,desig,empId,basic,hra,allow,ded,join,gender,false);
    }
}