package com.ecom.repository;

import com.ecom.entity.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

   UserDtls findByEmail(String email);

    List<UserDtls> findByRole(String role);

    public UserDtls findByResetToken(String token);
}
