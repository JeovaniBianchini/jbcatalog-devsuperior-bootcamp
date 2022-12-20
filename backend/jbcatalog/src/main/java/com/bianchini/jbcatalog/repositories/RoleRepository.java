package com.bianchini.jbcatalog.repositories;

import com.bianchini.jbcatalog.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
