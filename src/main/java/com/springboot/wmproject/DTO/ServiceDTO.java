package com.springboot.wmproject.DTO;

import com.springboot.wmproject.entities.ServiceDetails;
import lombok.Data;


import java.util.Set;

@Data
public class ServiceDTO {
    private int id;
    private String serviceName;
    private Double price;
    private String description;
    private boolean active;
    private Double cost;

//    private Set<ServiceDetailDTO> serviceDetailsById;
}
