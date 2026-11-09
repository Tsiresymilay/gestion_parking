package com.example.parking.repository;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.Reservation;
import com.example.parking.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(AppUser user);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("""
            select r from Reservation r
            where r.space.id = :spaceId
              and r.status in (com.example.parking.entity.ReservationStatus.EN_ATTENTE,
                               com.example.parking.entity.ReservationStatus.CONFIRMEE)
              and (:excludeId is null or r.id <> :excludeId)
              and r.expectedArrival < :departure
              and r.expectedDeparture > :arrival
            """)
    List<Reservation> findOverlapping(@Param("spaceId") Long spaceId,
                                      @Param("arrival") LocalDateTime arrival,
                                      @Param("departure") LocalDateTime departure,
                                      @Param("excludeId") Long excludeId);

    @Query("""
            select r from Reservation r
            where (:status is null or r.status = :status)
              and (:keyword is null or lower(r.vehicle.plateNumber) like lower(concat('%', :keyword, '%')))
            """)
    Page<Reservation> search(@Param("keyword") String keyword,
                             @Param("status") ReservationStatus status,
                             Pageable pageable);
}
