package com.example.parking.repository;

import com.example.parking.entity.AppUser;
import com.example.parking.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
            select u from AppUser u
            where (:role is null or u.role = :role)
              and (:keyword is null or lower(u.firstName) like lower(concat('%', :keyword, '%'))
                   or lower(u.lastName) like lower(concat('%', :keyword, '%'))
                   or lower(u.username) like lower(concat('%', :keyword, '%')))
            """)
    Page<AppUser> search(@Param("keyword") String keyword,
                         @Param("role") Role role,
                         Pageable pageable);
}
