package com.ikon.dacexampleapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse getTaskById(String id);

    List<TaskResponse> getAllTasksWithFilters(TaskStatus status, TaskPriority priority, String search);

    Page<TaskResponse> getTasksPaginated(Pageable pageable, TaskStatus status, TaskPriority priority, String search);

    TaskResponse updateTask(String id, TaskRequest request);

    void deleteTask(String id);

}
