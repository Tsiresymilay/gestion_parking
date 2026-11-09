package com.example.parking.repository;

import com.example.parking.entity.ParkingSpace;
import com.example.parking.entity.SpaceStatus;
import com.example.parking.entity.SpaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    Optional<ParkingSpace> findByNumber(String number);

    boolean existsByNumber(String number);

    List<ParkingSpace> findByStatus(SpaceStatus status);

    List<ParkingSpace> findByStatusAndType(SpaceStatus status, SpaceType type);

    long countByStatus(SpaceStatus status);

    long countByType(SpaceType type);

    @Query("""
            select s from ParkingSpace s
            where (:status is null or s.status = :status)
              and (:type is null or s.type = :type)
              and (:zone is null or lower(s.zone) = lower(:zone))
              and (:keyword is null or lower(s.number) like lower(concat('%', :keyword, '%'))
                   or lower(s.zone) like lower(concat('%', :keyword, '%')))
            """)
    Page<ParkingSpace> search(@Param("keyword") String keyword,
                              @Param("status") SpaceStatus status,
                              @Param("type") SpaceType type,
                              @Param("zone") String zone,
                              Pageable pageable);
}
