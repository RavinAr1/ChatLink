package com.chatlink.repository;

import com.chatlink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByUniqueCode(String uniqueCode);

    User findByVerificationToken(String token);

    User findByResetToken(String token);
}
