package com.springboot.wmproject.repositories;

import com.springboot.wmproject.DTO.EmployeeDTO;
import com.springboot.wmproject.entities.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employees,Integer> {
    @Query("select e from Employees e join EmployeeAccounts ea on e.id = ea.employeeId where ea.role like %:role% ")
    List<Employees> findAllByRole(String role);
    @Query("select e from Employees e where e.name like %:name%")
    List<Employees> findAllByName(String name);
    @Query("select e from Employees e where e.team_id = :teamId")
    List<Employees> findAllTeamId(Integer teamId);

    @Query("select e from Employees e where e.phone = :phone")
    List<Employees> checkPhoneExists(String phone);

    @Query("select e from Employees e where e.email = :email")
    List<Employees> checkEmailExists(String email);

    @Query("SELECT e from Employees e where e.id = :id and e.is_deleted = false")
    Employees getEmployeeById(int id);

    @Query("select ea.role from Employees e join EmployeeAccounts ea on e.id = ea.employeeId where e.id = :empID ")
    String findRoleByEmployeeID(int empID);

    @Query("select e from Employees e join EmployeeAccounts ea on e.id = ea.employeeId where e.is_deleted = false and ea.role <> 'ROLE_ADMIN' ")

    List<Employees> findAllExceptAdmin();

    @Query("Select e from OrganizeTeams t join Employees e on t.id = e.team_id where e.team_id =:empId and e.is_deleted = false")
    List<Employees> getAllEmployeeByTeamId(Integer empId);

}
