package com.crypto.users.repository;

import com.crypto.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(""
            + "SELECT u FROM User u "
            + "WHERE (:includeInactive = TRUE OR u.active = TRUE) "
            + "AND (LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%',:filter,'%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%',:filter,'%'))) "
    )
    Page<User> findAll(@Param("includeInactive") Boolean includeInactive, @Param("filter") String filter, Pageable pageable);
}
