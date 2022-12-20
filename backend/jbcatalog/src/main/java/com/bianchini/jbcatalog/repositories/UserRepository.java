package com.bianchini.jbcatalog.repositories;

import com.bianchini.jbcatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
