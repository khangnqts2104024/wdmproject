package com.springboot.wmproject.controllers;

import com.springboot.wmproject.DTO.JWTAuthResponse;
import com.springboot.wmproject.DTO.LoginDTO;
import com.springboot.wmproject.DTO.RegisterDTO;
import com.springboot.wmproject.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

//    Login API
    @PostMapping(value = {"employee/login"})
    public ResponseEntity<JWTAuthResponse> staffLogin(@RequestBody LoginDTO loginDTO){
        String token = authService.employeeLogin(loginDTO);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping(value = {"customer/login"})
    public ResponseEntity<JWTAuthResponse> customerLogin(@RequestBody LoginDTO loginDTO){
        String token = authService.customerLogin(loginDTO);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = {"employee/register"})
    public ResponseEntity<RegisterDTO> staffRegister(@RequestBody RegisterDTO registerDTO){
        RegisterDTO response = authService.employeeRegister(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = {"customer/register"})
    public ResponseEntity<RegisterDTO> customerRegister(@RequestBody RegisterDTO registerDTO){
        RegisterDTO response = authService.customerRegister(registerDTO);
        return ResponseEntity.ok(response);
    }
}
