// file: src/main/java/com/example/book/repository/RoleRepository.java
package com.example.book.repository;

import com.example.book.entity.AppRole;
import com.example.book.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}