package com.springboot.wmproject.controllers;

import com.springboot.wmproject.DTO.EmployeeDTO;
import com.springboot.wmproject.DTO.OrganizeTeamDTO;
import com.springboot.wmproject.DTO.TeamSummaryDTO;
import com.springboot.wmproject.services.OrganizeTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teams")
public class OrganizeTeamController {
    private OrganizeTeamService organizeTeamService;
    @Autowired
    public OrganizeTeamController(OrganizeTeamService organizeTeamService) {
        this.organizeTeamService = organizeTeamService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZE','SALE')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/getSummaryTeamOrganization")
    public ResponseEntity<List<TeamSummaryDTO>> getSummaryTeamOrganization(){
        List<TeamSummaryDTO> list = organizeTeamService.getSummaryTeamOrganization();
        return ResponseEntity.ok(list);
    }



    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZE','SALE','ANONYMOUS','CUSTOMER')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/all")
    public ResponseEntity<List<OrganizeTeamDTO>> getAll(){
        return ResponseEntity.ok(organizeTeamService.getAllOrganizeTeam());
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZE','SALE')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/all/name/{name}")
    public ResponseEntity<List<OrganizeTeamDTO>> getAllByName(@Valid @PathVariable String name){
        return ResponseEntity.ok(organizeTeamService.searchOrganizeTeamByName(name));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ORGANIZE','SALE')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/one/id/{id}")
    public ResponseEntity<OrganizeTeamDTO> getAllById(@Valid @PathVariable int id){
        return ResponseEntity.ok(organizeTeamService.getOneOrganizeTeamById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/create")
    public ResponseEntity<OrganizeTeamDTO> createOrganizeTeam(@Valid @RequestBody OrganizeTeamDTO organizeTeamDTO){
        return new ResponseEntity<>(organizeTeamService.createOrganizeTeam(organizeTeamDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/update")
    public ResponseEntity<OrganizeTeamDTO> updateOrganizeTeam(@Valid @RequestBody OrganizeTeamDTO organizeTeamDTO){
        return ResponseEntity.ok(organizeTeamService.updateOrganizeTeam(organizeTeamDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> softDelete(@Valid @PathVariable int id){
        organizeTeamService.softDelete(id);
        return ResponseEntity.ok("Organize Team has been deleted");
    }
}
