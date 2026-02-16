package com.ikon.dacexampleapp.api;

import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Management", description = "APIs for managing tasks with CRUD operations")
@RequestMapping("/tasks")
public interface TaskApi {
        @Operation(summary = "Create a new task")
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Task created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping
        ResponseEntity<TaskResponse> createTask(
                @Parameter(description = "Access token for authentication", required = true) @RequestHeader("Authorization") String accessToken,
                @Valid @RequestBody TaskRequest request);
        
        @Operation(summary = "Get a task by ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
                @ApiResponse(responseCode = "404", description = "Task not found"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @GetMapping("/{id}")
        ResponseEntity<TaskResponse> getTaskById(
                @Parameter(description = "Access token for authentication", required = true) @RequestHeader("Authorization") String accessToken,
                @Parameter(description = "ID of the task to retrieve", required = true) @PathVariable String id);
        
        @Operation(summary = "Get all tasks with optional filters")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @GetMapping
        ResponseEntity<List<TaskResponse>> getAllTasks(
                @Parameter(description = "Access token for authentication", required = true) @RequestHeader("Authorization") String accessToken,
                @Parameter(description = "Filter by task status") @RequestParam(required = false) TaskStatus status,
                @Parameter(description = "Filter by task priority") @RequestParam(required = false) TaskPriority priority,
                @Parameter(description = "Search term for title or description") @RequestParam(required = false) String search);

        @Operation(summary = "Get paginated list of tasks with optional filters")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @GetMapping("/paginated")
        ResponseEntity<Page<TaskResponse>> getTasksPaginated(
                @Parameter(description = "Access token for authentication", required = true) @RequestHeader("Authorization") String accessToken,
                @Parameter(description = "Pagination information") Pageable pageable,
                @Parameter(description = "Filter by task status") @RequestParam(required = false) TaskStatus status,
                @Parameter(description = "Filter by task priority") @RequestParam(required = false) TaskPriority priority,
                @Parameter(description = "Search term for title or description") @RequestParam(required = false) String search);

        @Operation(summary = "Update an existing task")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Task updated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "404", description = "Task not found"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PutMapping("/{id}")
        ResponseEntity<TaskResponse> updateTask(
                @Parameter(description = "Access token for authentication", required = true) @RequestHeader("Authorization") String accessToken,
                @Parameter(description = "ID of the task to update", required = true) @PathVariable String id,
                @Valid @RequestBody TaskRequest request);

        @Operation(summary = "Delete a task by ID")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Task not found"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @DeleteMapping("/{id}")
        ResponseEntity<Void> deleteTask(
                @Parameter(description = "Access token for authentication", required = true) @RequestHeader("Authorization") String accessToken,
                @Parameter(description = "ID of the task to delete", required = true) @PathVariable String id);
}