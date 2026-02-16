package com.ikon.dacexampleapp.repository;

import com.ikon.dacexampleapp.entity.TaskEntity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
        
}
