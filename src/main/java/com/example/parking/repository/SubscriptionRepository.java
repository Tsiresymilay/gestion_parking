package com.example.parking.repository;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.Subscription;
import com.example.parking.entity.SubscriptionStatus;
import com.example.parking.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser(AppUser user);

    List<Subscription> findByStatus(SubscriptionStatus status);

    @Query("""
            select s from Subscription s
            where s.vehicle = :vehicle
              and s.status = com.example.parking.entity.SubscriptionStatus.ACTIF
              and s.startDate <= :date
              and s.endDate >= :date
            """)
    List<Subscription> findValidByVehicle(@Param("vehicle") Vehicle vehicle,
                                          @Param("date") LocalDate date);

    @Query("""
            select s from Subscription s
            where (:status is null or s.status = :status)
              and (:keyword is null or lower(s.vehicle.plateNumber) like lower(concat('%', :keyword, '%')))
            """)
    Page<Subscription> search(@Param("keyword") String keyword,
                              @Param("status") SubscriptionStatus status,
                              Pageable pageable);
}
