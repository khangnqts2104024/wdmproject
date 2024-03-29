package com.springboot.wmproject.services.AuthServices;

import com.springboot.wmproject.DTO.EmployeeDTO;
import com.springboot.wmproject.entities.Employees;

import java.util.List;

public interface EmployeeService {
    String findRoleByEmployeeID(int empID);
    List<EmployeeDTO> getAllEmployeesExceptAdmin();
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(int id);
    EmployeeDTO create(EmployeeDTO newEmployeeDTO);
    EmployeeDTO update(EmployeeDTO updateEmployeeDTO);
    List<EmployeeDTO> findAllByRole(String role);

    List<Employees> checkPhoneExists(String phone);
    List<Employees> checkEmailExists(String email);
    List<EmployeeDTO> findAllByName(String empType);
    List<EmployeeDTO> findAllByTeamId(Integer teamId);



    void softDelete(int employeeId);
     List<EmployeeDTO> getAllEmployeeByTeamId(Integer empId);

}
