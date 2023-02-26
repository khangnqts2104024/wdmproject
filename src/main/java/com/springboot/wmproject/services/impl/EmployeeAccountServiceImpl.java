package com.springboot.wmproject.services.impl;

import com.springboot.wmproject.DTO.EmployeeAccountDTO;
import com.springboot.wmproject.entities.EmployeeAccounts;
import com.springboot.wmproject.entities.Employees;
import com.springboot.wmproject.exceptions.ResourceNotFoundException;
import com.springboot.wmproject.repositories.EmployeeAccountRepository;
import com.springboot.wmproject.repositories.EmployeeRepository;
import com.springboot.wmproject.services.EmployeeAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class EmployeeAccountServiceImpl implements EmployeeAccountService {
    private EmployeeRepository empRepo;
    private EmployeeAccountRepository empAccRepo;
    private ModelMapper modelMapper;

    @Autowired
    public EmployeeAccountServiceImpl(EmployeeRepository empRepo, EmployeeAccountRepository empAccRepo, ModelMapper modelMapper) {
        this.empRepo = empRepo;
        this.empAccRepo = empAccRepo;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<EmployeeAccountDTO> getAllEmployeeAccounts(){
        List<EmployeeAccounts> employeeAccountsList = empAccRepo.findAll();
        if(employeeAccountsList.isEmpty()){
            return null;
        }
        List<EmployeeAccountDTO> employeeAccountDTOList = employeeAccountsList.stream().map(booking -> mapToDto(booking)).collect(Collectors.toList());
        return employeeAccountDTOList;
    }

    @Override
    public EmployeeAccountDTO getEmployeeAccountByEmployeeAccountId(int id) {
        EmployeeAccounts employeeAccount= empAccRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee Account","Id",String.valueOf(id)));
        return mapToDto(employeeAccount);
    }

//    @Override
//    public List<EmployeeAccountDTO> findByPhone(String phone) {
//        List<EmployeeAccounts> employeeAccounts = empAccRepo.findByPhone(phone);
//        return employeeAccounts.stream().map(emp -> mapToDto(emp)).collect(Collectors.toList());
//    }

    @Override
    public List<EmployeeAccountDTO> findByName(String name) {
        List<EmployeeAccounts> employeeAccounts = empAccRepo.findByName(name);
        return employeeAccounts.stream().map(emp -> mapToDto(emp)).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeAccountDTO> filterByEmpType(String empType) {
        List<EmployeeAccounts> employeeAccounts = empAccRepo.filterByEmpType(empType);
        return employeeAccounts.stream().map(emp -> mapToDto(emp)).collect(Collectors.toList());
    }

    @Override
    public EmployeeAccountDTO createEmployeeAccount(EmployeeAccountDTO employeeAccountDTO) {
        int employeeId = employeeAccountDTO.getEmployeeId();
        //check employee exist
        if (employeeId != 0) {
            Employees checkEmployees = empRepo.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(employeeId)));
            List<EmployeeAccounts> checkEmployeeAccount= (List <EmployeeAccounts>) empAccRepo.getEmployeeAccountByEmployeeId(employeeId);
            //if exist create account for employee
            if (checkEmployees != null && checkEmployeeAccount.isEmpty()) {
                EmployeeAccounts newEmployeeAccount = empAccRepo.save(mapToEntity(employeeAccountDTO));
                EmployeeAccountDTO employeeAccountResponse = mapToDto(newEmployeeAccount);
                return employeeAccountResponse;
            }
        }
        return null;
    }

    @Override
    public EmployeeAccountDTO updateEmployeeAccount(EmployeeAccountDTO employeeAccountDTO){
        int employeeAccountId = employeeAccountDTO.getEmployeeId();
        //check employee account exist
        EmployeeAccounts checkEmployeeAccount = empAccRepo.findById(employeeAccountId).orElseThrow(() -> new ResourceNotFoundException("Employee Account", "id", String.valueOf(employeeAccountId)));
        //if exist update
        if (checkEmployeeAccount != null) {
            EmployeeAccounts updateEmployeeAccount = new EmployeeAccounts();
            updateEmployeeAccount.setId(employeeAccountDTO.getId());
            updateEmployeeAccount.setUsername(employeeAccountDTO.getUsername());
            updateEmployeeAccount.setPassword(employeeAccountDTO.getPassword());
            updateEmployeeAccount.setRole(employeeAccountDTO.getRole());
            updateEmployeeAccount.setEmployeeId(employeeAccountDTO.getEmployeeId());
            empAccRepo.save(updateEmployeeAccount);
            return mapToDto(updateEmployeeAccount);
        }
        return null;
    }

    @Override
    public void deleteEmployeeAccount(int employeeAccountId) {
        EmployeeAccounts checkEmployeeAccount = empAccRepo.findById(employeeAccountId).orElseThrow(() -> new ResourceNotFoundException("Employee Account", "id", String.valueOf(employeeAccountId)));
        empAccRepo.delete(checkEmployeeAccount);
    }

    public EmployeeAccountDTO mapToDto(EmployeeAccounts employeeAccounts) {
        EmployeeAccountDTO postDto = modelMapper.map(employeeAccounts, EmployeeAccountDTO.class);
        return postDto;
    }

    public EmployeeAccounts mapToEntity(EmployeeAccountDTO employeeAccountDTO) {
        EmployeeAccounts employeeAccounts = modelMapper.map(employeeAccountDTO, EmployeeAccounts.class);
        return employeeAccounts;
    }



}
