package com.springboot.wmproject.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.wmproject.DTO.*;
import com.springboot.wmproject.securities.AuthenticationToken.CustomerUsernamePasswordAuthenticationToken;
import com.springboot.wmproject.services.AuthServices.AuthService;
import com.springboot.wmproject.services.AuthServices.CustomerAccountService;
import com.springboot.wmproject.services.AuthServices.PasswordResetTokenService;
import com.springboot.wmproject.services.AuthServices.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;
    private CustomerAccountService accountService;

    private PasswordResetTokenService passwordResetTokenService;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(PasswordResetTokenService passwordResetTokenService, AuthService authService, CustomerAccountService accountService,RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.accountService = accountService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    //    Login API
    @PostMapping(value = {"/employees/login"})
    public ResponseEntity<JWTAuthResponse> staffLogin(@RequestBody LoginDTO loginDTO){
        HashMap<String,String> map = authService.employeeLogin(loginDTO);

        if(map.containsKey("accessToken") && map.containsKey("refreshToken")){
            JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
            jwtAuthResponse.setAccessToken(map.get("accessToken"));
            jwtAuthResponse.setRefreshToken(map.get("refreshToken"));
            return ResponseEntity.ok(jwtAuthResponse);
        }
        return null;
    }

    @PostMapping(value = {"/refreshToken/{refreshToken}"})
    public ResponseEntity<JWTAuthResponse> refreshToken(@PathVariable("refreshToken") String refreshToken){
       String newAccessToken = authService.refreshToken(refreshToken);
            JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
            jwtAuthResponse.setAccessToken(newAccessToken);
            return ResponseEntity.ok(jwtAuthResponse);
    }


    @PostMapping(value = {"/customers/login"})
    public ResponseEntity<JWTAuthResponse> customerLogin(@RequestBody LoginDTO loginDTO){
        HashMap<String,String> map = authService.customerLogin(loginDTO);

        if(map.containsKey("accessToken") && map.containsKey("refreshToken")){
            JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
            jwtAuthResponse.setAccessToken(map.get("accessToken"));
            jwtAuthResponse.setRefreshToken(map.get("refreshToken"));
            return ResponseEntity.ok(jwtAuthResponse);
        }
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = {"/employees/create"})
    public ResponseEntity<RegisterDTO> staffRegister(@RequestBody RegisterDTO registerDTO) throws JsonProcessingException {
        RegisterDTO response = authService.employeeRegister(registerDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = {"/customers/register"})
    public ResponseEntity<RegisterCustomerDTO> customerRegister(@RequestBody RegisterCustomerDTO registerDTO,@RequestHeader("User-Agent") String userAgent) throws JsonProcessingException {
        RegisterCustomerDTO response = authService.customerRegister(registerDTO, userAgent);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = {"/customers/register/validPhoneEmail"})
    public ResponseEntity<RegisterCustomerDTO> customerValidPhoneEmail(@RequestBody RegisterCustomerDTO registerDTO) throws JsonProcessingException {
        RegisterCustomerDTO response = authService.customerPersonalValid(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALE','ORGANIZE')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = {"/employees/update"})
    public ResponseEntity<RegisterDTO> staffUpdate(@RequestBody RegisterDTO registerDTO) throws JsonProcessingException {
        RegisterDTO response = authService.employeeUpdate(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = {"/employees/delete/{id}"})
    public ResponseEntity<String> staffDelete(@PathVariable int id) throws JsonProcessingException {
        String response = authService.staffDelete(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALE','CUSTOMER')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = {"/customers/update"})
    public ResponseEntity<RegisterCustomerDTO> customerUpdate(@RequestBody RegisterCustomerDTO registerDTO,@RequestHeader("User-Agent") String userAgent) throws JsonProcessingException {
        RegisterCustomerDTO response = authService.customerUpdate(registerDTO,userAgent);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALE','ORGANIZE')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = {"/employees/findRoleByEmployeeID/{id}"})
    public ResponseEntity<String> findRoleByEmployeeID(@PathVariable int id) throws JsonProcessingException {
        String response = authService.findRoleByEmployeeID(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALE','CUSTOMER')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = {"/customers/getOne/RegisterCustomer/{id}"})
    public ResponseEntity<RegisterCustomerDTO> getOneRegisterCustomer(@PathVariable int id) throws JsonProcessingException {
        RegisterCustomerDTO response = authService.getOneRegisterCustomer(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALE','ORGANIZE')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = {"/employees/getOne/RegisterEmployee/{id}"})
    public ResponseEntity<RegisterDTO> getOneRegisterEmp(@PathVariable int id) throws JsonProcessingException {
        RegisterDTO response = authService.getOneRegisterEmp(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/processForgotPassword")
    public ResponseEntity<String> processForgotPassword(@RequestBody String email,@RequestHeader("User-Agent") String userAgent){
        String response = accountService.processForgotPassword(email,userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/processChangePassword")
    public ResponseEntity<String> processChangePassword(@RequestBody PasswordDTO passwordDTO) throws ParseException {
        String response = accountService.updatePassword(passwordDTO.getNewPassword(), passwordDTO.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/verifyEmailRegister/{token}")
    public ResponseEntity<String> verifyEmailRegister(@PathVariable("token") String token) throws ParseException {
        String response = accountService.verifyEmailRegister(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/updatePasswordMobile")
    public ResponseEntity<String> updatePasswordMobile(@RequestBody PasswordDTO passwordDTO) throws ParseException {
        String response = accountService.updatePasswordMobile(passwordDTO.getNewPassword(),passwordDTO.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/validToken")
    public ResponseEntity<String> validToken(@RequestBody String token) throws ParseException {
        String response = accountService.validToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customers/createPWToken")
    public ResponseEntity<String> createPWToken(){
        String response = passwordResetTokenService.create(1);
        return ResponseEntity.ok(response);
    }



}
