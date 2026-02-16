package com.ikon.dacexampleapp.repository;

import com.ikon.dacexampleapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
        
}
