package com.example.parking.repository;

import com.example.parking.entity.ParkingSession;
import com.example.parking.entity.SessionStatus;
import com.example.parking.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    List<ParkingSession> findByStatus(SessionStatus status);

    Optional<ParkingSession> findFirstByVehicleAndStatus(Vehicle vehicle, SessionStatus status);

    @Query("""
            select s from ParkingSession s
            where (:status is null or s.status = :status)
              and (:keyword is null or lower(s.vehicle.plateNumber) like lower(concat('%', :keyword, '%')))
            """)
    Page<ParkingSession> search(@Param("keyword") String keyword,
                                @Param("status") SessionStatus status,
                                Pageable pageable);

    @Query("""
            select s from ParkingSession s
            where s.exitTime is not null
              and s.exitTime >= :from
              and s.exitTime < :to
            """)
    List<ParkingSession> findCompletedBetween(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    long countByStatus(SessionStatus status);
}
