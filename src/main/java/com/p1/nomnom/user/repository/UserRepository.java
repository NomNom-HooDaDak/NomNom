package com.p1.nomnom.user.repository;

import com.p1.nomnom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
