package com.ikon.dacexampleapp.service;

import com.ikon.appaccessmanagement.entity.group.IkonGroup;
import com.ikon.appaccessmanagement.enums.GroupType;
import com.ikon.appaccessmanagement.service.IkonGroupService;
import com.ikon.dac.annotation.RequireRole;
import com.ikon.dac.core.AccessChecker;
import com.ikon.dac.core.AccessCriteria;
import com.ikon.dac.core.DataAccessFilter;
import com.ikon.dac.filter.jpa.JpaAccessFilter;
import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.entity.Task;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;
import com.ikon.dacexampleapp.repository.TaskRepository;
import com.ikon.webservice.WebService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService extends WebService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final JpaAccessFilter dataAccessFilter;
    private final IkonGroupService ikonGroupService;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        Task task = modelMapper.map(request, Task.class);

        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setAccountId(getActiveAccountId());

        Task savedTask = taskRepository.save(task);

        String dynamicGroupName = "Task-" + savedTask.getId() + "-Group";

        savedTask.setDynamicGroups(new HashSet<>(Set.of(dynamicGroupName)));
        taskRepository.save(savedTask);

        IkonGroup group = IkonGroup.builder()
                .groupName(dynamicGroupName)
                .groupType(GroupType.DYNAMIC)
                .groupDescription("Test")
                .accountId(getActiveAccountId())
                .softwareId(UUID.fromString("865798ff-47a9-4911-b82e-d50d6fd7ff87"))
                .build();
        IkonGroup createdGroup = ikonGroupService.createGroup(group);

        ikonGroupService.saveMembershipToGroup(createdGroup.getGroupId(), List.of(getCurrentUserId()),
                getActiveAccountId());

        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return modelMapper.map(task, TaskResponse.class);
    }

    
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksWithFilters(TaskStatus status, TaskPriority priority, String search) {
        Map<String, Object> filters = new HashMap<>();
        if (status != null) {
            filters.put("status", status);
        }
        if (priority != null) {
            filters.put("priority", priority);
        }
        // if (search != null && !search.trim().isEmpty()) {
        // filters.put("search", search);
        // }
        return dataAccessFilter
                .findAll(Task.class, filters,
                        AccessCriteria.builder().skipDynamicGroupCheck(false).allowedRoles(Set.of("Basic Access"))
                                .build())
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksPaginated(Pageable pageable, TaskStatus status,
            TaskPriority priority, String search) {
        Map<String, Object> filters = new HashMap<>();
        if (status != null) {
            filters.put("status", status);
        }
        if (priority != null) {
            filters.put("priority", priority);
        }
        // if (search != null && !search.trim().isEmpty()) {
        // filters.put("search", search);
        // }
        return dataAccessFilter
                .findAll(Task.class, filters, AccessCriteria.builder().skipDynamicGroupCheck(false).allowedRoles(Set.of("Basic Access")).build(),
                        pageable)
                .map(task -> modelMapper.map(task, TaskResponse.class));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // Update fields
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            existingTask.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            existingTask.setPriority(request.getPriority());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
