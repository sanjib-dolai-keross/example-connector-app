package com.ikon.dacexampleapp.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ikon.app.core.properties.IkonApplicationProperties;
import com.ikon.appaccessmanagement.entity.group.IkonGroup;
import com.ikon.appaccessmanagement.enums.GroupType;
import com.ikon.appaccessmanagement.service.IkonGroupService;
import com.ikon.dac.core.AccessCriteria;
import com.ikon.dac.filter.mongo.MongoAccessFilter;
import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.entity.TaskDocument;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;
import com.ikon.dacexampleapp.repository.TaskMongoRepository;
import com.ikon.webservice.WebService;

import lombok.RequiredArgsConstructor;

@Service("mongoService")
@Primary
@RequiredArgsConstructor
public class MongoService extends WebService implements TaskService {

    private final TaskMongoRepository taskMongoRepository;
    private final ModelMapper modelMapper;
    private final MongoAccessFilter dataAccessFilter;
    private final IkonGroupService ikonGroupService;
    private final IkonApplicationProperties applicationProperties;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        TaskDocument task = modelMapper.map(request, TaskDocument.class);

        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setAccountId(getActiveAccountId());

        TaskDocument savedTask = taskMongoRepository.save(task);

        String dynamicGroupName = "Task-" + savedTask.getId() + "-Group";

        savedTask.setDynamicGroups(new HashSet<>(Set.of(dynamicGroupName)));
        taskMongoRepository.save(savedTask);

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
        TaskDocument task = taskMongoRepository.findById(new ObjectId(id))
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
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
                .findAll(TaskDocument.class, filters,
                        AccessCriteria.builder().allowedRoles(Set.of("Basic Access"))
                                .build())
                .stream()
                .map(task -> modelMapper.map(task, TaskResponse.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksPaginated(Pageable pageable, TaskStatus status, TaskPriority priority,
            String search) {
        Map<String, Object> filters = new HashMap<>();
        if (status != null) {
            filters.put("status", status);
        }
        if (priority != null) {
            filters.put("priority", priority);
        }

        return dataAccessFilter
                .findAll(TaskDocument.class, filters,
                        AccessCriteria.builder()
                                .build(),
                        pageable)
                .map(task -> modelMapper.map(task, TaskResponse.class));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String id, TaskRequest request) {
        TaskDocument existingTask = taskMongoRepository.findById(new ObjectId(id))
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

        TaskDocument updatedTask = taskMongoRepository.save(existingTask);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    @Transactional
    public void deleteTask(String id) {
        ObjectId objectId = new ObjectId(id);
        if (!taskMongoRepository.existsById(objectId)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskMongoRepository.deleteById(objectId);
    }

}
