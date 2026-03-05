package com.example.users_manager.repository;

import com.example.users_manager.entity.User;
import com.example.users_manager.entity.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.example.users_manager.repository.UserListProjection;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u.id AS id, u.name AS name, u.surname AS surname
        FROM User u
        WHERE (:gender IS NULL OR u.gender = :gender)
        AND (
            :search IS NULL OR :search = '' OR
            LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<UserListProjection> searchUsers(
            @Param("gender") Gender gender,
            @Param("search") String search,
            Pageable pageable
    );
}