package com.springboot.wmproject.repositories;

import com.springboot.wmproject.entities.ServiceDetails;
import com.springboot.wmproject.entities.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetails,Integer> {
    @Query("select c from ServiceDetails c where c.orderId =:orderId")
    List<ServiceDetails> findAllByOrder(Integer orderId);
    @Query("select c from ServiceDetails c where c.serviceId =:serviceId")
    List<ServiceDetails> findAllByService(Integer serviceId);
    @Modifying
    @Query("delete from ServiceDetails c where c.orderId =:orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);
}
