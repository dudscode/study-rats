package com.example.studyrats.repository;

import com.example.studyrats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository  extends JpaRepository<User, String> {
    User findByEmail(String email);
}
