package com.springboot.wmproject.services.impl;

import com.springboot.wmproject.DTO.OrderDTO;
import com.springboot.wmproject.entities.Orders;
import com.springboot.wmproject.repositories.OrderRepository;
import com.springboot.wmproject.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.springboot.wmproject.utils.SD.*;

@Component
public class OrderSchedule {

    private OrderRepository orderRepository;
    private OrderService orderService;
@Autowired
    public OrderSchedule( OrderRepository orderRepository, OrderService orderService) {

        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }




    @Scheduled(fixedRate = 720000) // Thực hiện kiểm tra sau mỗi 12 h
    public void updateOrderStatusWarning() {
        // Lấy danh sách các Order từ database
        List<Orders> orders=orderRepository.findAll();
        // Kiểm tra xem có Order nào gần tới timeHappen không
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Orders order : orders) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timeHappen = LocalDateTime.parse(order.getTimeHappen(),formatter);
            Duration duration = Duration.between(now, timeHappen);
            long seconds = duration.getSeconds();

            // Nếu còn 14 ngày nữa tới timeHappen, thực hiện việc cập nhật trạng thái của Order->warning
            //neu6 con 7 ngay thi2 cap65 nhat status warning->canceled
            if (seconds > 7 * 24 * 60 * 60 && seconds <= 14 * 24 * 60 * 60) {
                updateDepositedWarningAuto(order);
            }
            else if (seconds<7*24*60*60) {
               updateWarningCanceledAuto(order);
            }

        }
    }

    @Scheduled(fixedRate = 720000) // Thực hiện kiểm tra sau mỗi 12 h
    public void updateOrderStatusCanceled() {
        // Lấy danh sách các Order từ database
        List<Orders> orders=orderRepository.findAll();
        // Kiểm tra xem có Order nào gần tới timeHappen không
        //neu sửa format ở nao thì them formatter vào
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Orders order : orders) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timeOrder = LocalDateTime.parse(order.getOrderDate(),formatter);
            Duration duration = Duration.between(timeOrder, now);
            long seconds = duration.getSeconds();

            // Nếu còn 3 ngày sau order, thực hiện việc cập nhật trạng thái của Order
            if (seconds > 3 * 24 * 60 * 60) {
                updateOrderedCanceledAuto(order);
            }
        }
    }
    public void updateDepositedWarningAuto(Orders order) {

        if(order.getOrderStatus().equalsIgnoreCase(orderStatusDeposited)){

            order.setOrderStatus(orderStatusWarning);

            orderRepository.save(order);

        }

    }

    public void updateOrderedCanceledAuto(Orders order) {

        if(order.getOrderStatus().equalsIgnoreCase(orderStatusOrdered)){

            order.setOrderStatus(orderStatusCanceled);

            orderRepository.save(order);

        }

    }
    public void updateWarningCanceledAuto(Orders order) {

        if(order.getOrderStatus().equalsIgnoreCase(orderStatusWarning)){

            order.setOrderStatus(orderStatusCanceled);

            orderRepository.save(order);

        }

    }


}
