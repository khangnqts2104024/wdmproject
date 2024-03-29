package com.springboot.wmproject.services.impl;

import com.springboot.wmproject.DTO.OrderDTO;
import com.springboot.wmproject.DTO.VenueDTO;
import com.springboot.wmproject.entities.Orders;
import com.springboot.wmproject.entities.Venues;
import com.springboot.wmproject.exceptions.ResourceNotFoundException;
import com.springboot.wmproject.repositories.CustomerRepository;
import com.springboot.wmproject.repositories.OrderRepository;
import com.springboot.wmproject.repositories.VenueRepository;
import com.springboot.wmproject.services.VenueService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements VenueService {
    private VenueRepository venueRepository;
    private ModelMapper modelMapper;

    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;

    @Autowired
    public VenueServiceImpl(VenueRepository venueRepository, ModelMapper modelMapper, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.venueRepository = venueRepository;
        this.modelMapper = modelMapper;
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<VenueDTO> getAllVenue() throws ResourceNotFoundException {
        return venueRepository.findAll().stream().map(venue -> mapToDTO(venue)).collect(Collectors.toList());
    }

    @Override
    public List<VenueDTO> getAllVenueActive() {
        List<Venues> list = venueRepository.findAll();
        List<VenueDTO> venues = list.stream().map(venue -> mapToDTO(venue)).collect(Collectors.toList());
        List<VenueDTO> newList = new ArrayList<>();
        for (VenueDTO venue : venues) {
            if (venue.isActive()) {
                newList.add(venue);
            }
        }
        return newList;
    }

    @Override
    public VenueDTO getOneVenueById(int id) {
        Venues venues = venueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue", "id", String.valueOf(id)));
        return mapToDTO(venues);
    }

    @Override
    public List<VenueDTO> getAllVenueByOrderId(Integer orderId) throws ResourceNotFoundException {
        return venueRepository.getAllByOrderId(orderId).stream().map(venue -> mapToDTO(venue)).collect(Collectors.toList());

    }

    @Override
    public List<VenueDTO> getVenueByName(String name) throws ResourceNotFoundException {
        return venueRepository.getAllVenuesByName(name).stream().map(venues -> mapToDTO(venues)).collect(Collectors.toList());
    }

    @Override
    public VenueDTO createVenue(VenueDTO venueDTO) throws ResourceNotFoundException {
        String venueName = venueDTO.getVenueName();
        if (venueName != null) {
            List<Venues> checkVenues = venueRepository.validVenueByName(venueName);
            if (checkVenues.size() == 0) {
                Venues newVenues = venueRepository.save(mapToEntity(venueDTO));
                return mapToDTO(newVenues);
            }
        }
        return null;
    }

    @Override
    public VenueDTO updateVenue(VenueDTO venueDTO) throws ResourceNotFoundException {
        String venueName = venueDTO.getVenueName();
        if (venueName != null) {
            Venues checkVenue = venueRepository.findById(venueDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Venues", "id", String.valueOf(venueDTO.getId())));
            if (checkVenue.getVenueName().equals( venueDTO.getVenueName())) {
                checkVenue.setVenueName(venueDTO.getVenueName());
                checkVenue.setMinPeople(venueDTO.getMinPeople());
                checkVenue.setMaxPeople(venueDTO.getMaxPeople());
                checkVenue.setPrice(venueDTO.getPrice());
                checkVenue.setActive(venueDTO.isActive());
                venueRepository.save(checkVenue);
                return mapToDTO(checkVenue);
            } else {
                List<Venues> checkValid = venueRepository.validVenueByName(venueName);
                if (!checkValid.isEmpty()) {
                    return null;
                }else {
                    checkVenue.setVenueName(venueDTO.getVenueName());
                    checkVenue.setMinPeople(venueDTO.getMinPeople());
                    checkVenue.setMaxPeople(venueDTO.getMaxPeople());
                    checkVenue.setPrice(venueDTO.getPrice());
                    checkVenue.setActive(venueDTO.isActive());
                    venueRepository.save(checkVenue);
                    return mapToDTO(checkVenue);
                }
            }
        }
        return null;
    }

    @Override
    public void deleteVenue(int venueId) throws ResourceNotFoundException {
        Venues venues = venueRepository.findById(venueId).orElseThrow(() -> new ResourceNotFoundException("Venue", "Id", String.valueOf(venueId)));
        venueRepository.delete(venues);
    }

    public VenueDTO mapToDTO(Venues venue) {
        VenueDTO venueDTO = modelMapper.map(venue, VenueDTO.class);
        venueDTO.setActive(venue.isActive());
        return venueDTO;
    }

    public Venues mapToEntity(VenueDTO venueDTO) {

        Venues venue = modelMapper.map(venueDTO, Venues.class);
        venue.setActive(venueDTO.isActive());
        return venue;
    }

    public OrderDTO mapToDTO(Orders orders) {
        OrderDTO orderDTO = modelMapper.map(orders, OrderDTO.class);
        return orderDTO;
    }

    public Orders mapToEntity(OrderDTO orderDTO) {
        Orders orders = modelMapper.map(orderDTO, Orders.class);
        return orders;
    }
}
