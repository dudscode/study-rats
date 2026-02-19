package com.example.studyrats.repository;

import com.example.studyrats.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String > {
}
