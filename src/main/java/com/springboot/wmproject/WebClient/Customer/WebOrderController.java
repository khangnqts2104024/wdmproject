package com.springboot.wmproject.WebClient.Customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wmproject.DTO.*;

import com.springboot.wmproject.entities.Venues;
import com.springboot.wmproject.services.AuthService;
import com.springboot.wmproject.services.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller

public class WebOrderController {

    RestTemplate restTemplate = new RestTemplate();
    AuthService authService;

    String url = "http://localhost:8080/api/venues/all";
    String orderurl="http://localhost:8080/api/order";


    public WebOrderController(AuthService authService) {
        this.authService = authService;
    }

    @Autowired

//    String login="http://localhost:8080/api/auth/employee/login";



    @GetMapping("/order")
        public String order(){
            return "orderpage";
        }

        @RequestMapping("/getvenue")
        @ResponseBody
        public String about(@RequestBody String date) throws JsonProcessingException {
            Map<String, Object> map = new HashMap<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getToken());

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List<VenueDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<VenueDTO>>() {}
            );

            List<VenueDTO> venueList = response.getBody();

            ResponseEntity<List<OrderDTO>> orderResponse = restTemplate.exchange(
                    orderurl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<OrderDTO>>() {}
            );
            List<OrderDTO> orderList = orderResponse.getBody();
//        List find in date
            List<OrderDTO> bookedList=new ArrayList<>();
            if(orderList!=null){
            for (OrderDTO order: orderList) {
                if (order.getTimeHappen().contains(date)) {
                    bookedList.add(order);
                }
            }
            }
//        check venue available
            //fiter venue
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            List<VenueBooked> bookeds=new ArrayList<>();
            if(bookedList!=null){
//                int i=1;
            for (OrderDTO order:bookedList)
            {
                LocalTime timeHappen = LocalDateTime.parse(order.getTimeHappen(), formatter).toLocalTime();
                LocalTime compareTime1 = LocalTime.parse("13:00:00");
                LocalTime compareTime2 = LocalTime.parse("18:00:00");
                if(timeHappen.isBefore(compareTime1) && !order.getOrderStatus().equals("canceled")){
                VenueBooked newbooked=new VenueBooked();
//                newbooked.setId(i);
                newbooked.setVenueId(String.valueOf(order.getVenueId()));
                newbooked.setBooked("Afternoon");
                bookeds.add(newbooked);
//                i++;
                }
                else if(timeHappen.isAfter(compareTime1) && timeHappen.isBefore(compareTime2) && !order.getOrderStatus().equals("canceled")){
                    VenueBooked newbooked=new VenueBooked();
//                    newbooked.setId(i);
                    newbooked.setVenueId(String.valueOf(order.getVenueId()));
                    newbooked.setBooked("Evening");
                    bookeds.add(newbooked);
//                    i++;
                }
             }
            }
            String json = toJson(venueList,bookeds);
            return json;
        }

public String getToken()
{
    RestTemplate restTemplate = new RestTemplate();
    String loginUrl = "http://localhost:8080/api/auth/employee/login";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    LoginDTO loginRequest = new LoginDTO();
    loginRequest.setUsername("admin");
    loginRequest.setPassword("admin");

    HttpEntity<LoginDTO> request = new HttpEntity<>(loginRequest, headers);
    ResponseEntity<JWTAuthResponse> response = restTemplate.postForEntity(loginUrl, request, JWTAuthResponse.class);
    JWTAuthResponse jwtAuthResponse = response.getBody();
    String token = jwtAuthResponse.getAccessToken();
    return token;
}

    public String checkVenueBooked(List<VenueDTO> venueList,OrderDTO order) {

        for (VenueDTO venue : venueList) {
            if (venue.getId() == order.getVenueId()) {

                return "afternoon";
            }
        }
        return "none";
    }

    //toJson add Map and List of venue
    public String toJson(List<VenueDTO> venues, List<VenueBooked> bookeds) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> combinedMap = new HashMap<>();

        // Convert venues to JSON
        String venuesJson;
        try {
            venuesJson = objectMapper.writeValueAsString(venues);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        combinedMap.put("venues", venuesJson);

        String bookedJson;
        try {
            bookedJson = objectMapper.writeValueAsString(bookeds);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        combinedMap.put("bookeds", bookedJson);

        // Convert combinedMap to JSON
        try {
            return objectMapper.writeValueAsString(combinedMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    }


