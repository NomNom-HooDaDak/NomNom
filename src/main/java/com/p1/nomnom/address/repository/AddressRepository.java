package com.p1.nomnom.address.repository;

import com.p1.nomnom.address.entity.Address;
import com.p1.nomnom.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.isDeleted = false")
    List<Address> findByUser(@Param("user") User user);

    @Query("SELECT a FROM Address a WHERE a.id = :id AND a.isDeleted = false")
    Optional<Address> findByIdAndNotDeleted(@Param("id") UUID id);

    @Query("SELECT COUNT(a) > 0 FROM Address a WHERE a.user = :user AND a.isDeleted = false")
    boolean existsByUser(@Param("user") User user);

    @Query("SELECT COUNT(a) > 0 FROM Address a WHERE a.user = :user AND a.address = :address AND a.isDeleted = false")
    boolean existsByUserAndAddress(@Param("user") User user, @Param("address") String address);
}
