package com.ikon.dacexampleapp.controller;

import com.ikon.dac.annotation.RequireRole;
import com.ikon.dacexampleapp.api.TaskApi;
import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;
import com.ikon.dacexampleapp.service.TaskService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController implements TaskApi {

    private final TaskService taskService;

    public TaskController(@Qualifier("mongoService") TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public ResponseEntity<TaskResponse> createTask(String accessToken, TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TaskResponse> getTaskById(String accessToken, String id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<TaskResponse>> getAllTasks(String accessToken, TaskStatus status, TaskPriority priority,
            String search) {
        List<TaskResponse> tasks = taskService.getAllTasksWithFilters(status, priority, search);

        return ResponseEntity.ok(tasks);
    }

    @Override
    public ResponseEntity<Page<TaskResponse>> getTasksPaginated(String accessToken, Pageable pageable,
            TaskStatus status, TaskPriority priority,
            String search) {
        Page<TaskResponse> taskPage = taskService.getTasksPaginated(pageable, status, priority, search);
        return ResponseEntity.ok(taskPage);
    }

    @Override
    public ResponseEntity<TaskResponse> updateTask(String accessToken, String id, TaskRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTask(String accessToken, String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
