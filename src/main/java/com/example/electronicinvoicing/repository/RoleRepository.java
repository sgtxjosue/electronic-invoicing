package com.example.electronicinvoicing.repository;

import com.example.electronicinvoicing.domain.Role;
import com.example.electronicinvoicing.domain.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}

