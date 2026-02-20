package com.ikon.dacexampleapp.service;

import com.ikon.app.core.properties.IkonApplicationProperties;
import com.ikon.appaccessmanagement.entity.group.IkonGroup;
import com.ikon.appaccessmanagement.enums.GroupType;
import com.ikon.appaccessmanagement.service.IkonGroupService;
import com.ikon.dac.core.AccessCriteria;
import com.ikon.dac.core.DataAccessFilter;
import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.entity.TaskEntity;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;
import com.ikon.dacexampleapp.repository.TaskRepository;
import com.ikon.webservice.WebService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service("jpaService")
public class JPAService extends WebService implements TaskService {

    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final DataAccessFilter dataAccessFilter;
    private final IkonGroupService ikonGroupService;
    private final IkonApplicationProperties applicationProperties;

    public JPAService(TaskRepository taskRepository, ModelMapper modelMapper,
            @Qualifier("jpaAccessFilter") DataAccessFilter dataAccessFilter,
            IkonGroupService ikonGroupService, IkonApplicationProperties applicationProperties) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.dataAccessFilter = dataAccessFilter;
        this.ikonGroupService = ikonGroupService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        TaskEntity task = modelMapper.map(request, TaskEntity.class);

        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setAccountId(getActiveAccountId());
        task.setCreatedBy(getCurrentUserId());

        TaskEntity savedTask = taskRepository.save(task);

        String dynamicGroupName = "Task-" + savedTask.getId() + "-Group";

        savedTask.setDynamicGroups(new HashSet<>(Set.of(dynamicGroupName)));
        taskRepository.save(savedTask);

        IkonGroup group = IkonGroup.builder()
                .groupName(dynamicGroupName)
                .groupType(GroupType.DYNAMIC)
                .groupDescription("Test")
                .accountId(getActiveAccountId())
                .softwareId(applicationProperties.getSoftwareId())
                .build();
        IkonGroup createdGroup = ikonGroupService.createGroup(group);

        ikonGroupService.saveMembershipToGroup(createdGroup.getGroupId(), List.of(getCurrentUserId()),
                getActiveAccountId());

        return modelMapper.map(savedTask, TaskResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(String id) {
        TaskEntity task = dataAccessFilter.findByIdOrThrow(TaskEntity.class, UUID.fromString(id));
        return modelMapper.map(task, TaskResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksWithFilters(TaskStatus status, TaskPriority priority, String search) {
        Map<String, Object> filters = new HashMap<>();
        if (status != null) {
            filters.put("status", status);
        }
        if (priority != null) {
            filters.put("priority", priority);
        }
        return dataAccessFilter
                .findAll(TaskEntity.class, filters, search,
                        Sort.by(Sort.Direction.DESC, "createdAt"),
                        AccessCriteria.builder().allowedRoles(Set.of("Basic Access"))
                                .build())
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }

    @Override
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
        return dataAccessFilter
                .findAll(TaskEntity.class, filters, search, AccessCriteria.accountOnly(), pageable)
                .map(task -> modelMapper.map(task, TaskResponse.class));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String id, TaskRequest request) {
        TaskEntity existingTask = dataAccessFilter.findByIdOrThrow(TaskEntity.class, UUID.fromString(id),
                AccessCriteria.builder().ownerField("createdBy").skipDynamicGroupCheck(true)
                        .allowedRoles(Set.of("Task Admin")).build());

        // Update fields
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            existingTask.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            existingTask.setPriority(request.getPriority());
        }

        TaskEntity updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    @Transactional
    public void deleteTask(String id) {
        UUID uuid = UUID.fromString(id);
        if (!dataAccessFilter.isAccessible(TaskEntity.class, uuid,
                AccessCriteria.builder().ownerField("createdBy").skipDynamicGroupCheck(true)
                        .allowedRoles(Set.of("Task Admin")).build())) {
            throw new RuntimeException("Access Denied");
        }
        taskRepository.deleteById(uuid);
    }
}
