package com.example.parking.repository;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.Vehicle;
import com.example.parking.entity.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    boolean existsByPlateNumber(String plateNumber);

    List<Vehicle> findByOwner(AppUser owner);

    @Query("""
            select v from Vehicle v
            where (:type is null or v.type = :type)
              and (:keyword is null or lower(v.plateNumber) like lower(concat('%', :keyword, '%'))
                   or lower(v.brand) like lower(concat('%', :keyword, '%'))
                   or lower(v.model) like lower(concat('%', :keyword, '%')))
            """)
    Page<Vehicle> search(@Param("keyword") String keyword,
                         @Param("type") VehicleType type,
                         Pageable pageable);
}
