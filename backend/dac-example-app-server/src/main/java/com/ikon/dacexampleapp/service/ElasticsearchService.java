package com.ikon.dacexampleapp.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ikon.app.core.properties.IkonApplicationProperties;
import com.ikon.appaccessmanagement.entity.group.IkonGroup;
import com.ikon.appaccessmanagement.enums.GroupType;
import com.ikon.appaccessmanagement.service.IkonGroupService;
import com.ikon.dac.core.AccessCriteria;
import com.ikon.dac.core.DataAccessFilter;
import com.ikon.dac.filter.elasticsearch.ElasticAccessFilter;
import com.ikon.dacexampleapp.dto.request.TaskRequest;
import com.ikon.dacexampleapp.dto.response.TaskResponse;
import com.ikon.dacexampleapp.entity.TaskElasticsearch;
import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;
import com.ikon.dacexampleapp.repository.TaskElasticsearchRepository;
import com.ikon.webservice.WebService;

@Service("elasticsearchService")
public class ElasticsearchService extends WebService implements TaskService {

    private final TaskElasticsearchRepository taskRepository;
    private final ModelMapper modelMapper;
    private final DataAccessFilter dataAccessFilter;
    private final IkonGroupService ikonGroupService;
    private final IkonApplicationProperties applicationProperties;

    public ElasticsearchService(TaskElasticsearchRepository taskMongoRepository, ModelMapper modelMapper,
            @Qualifier("elasticAccessFilter") DataAccessFilter dataAccessFilter,
            IkonGroupService ikonGroupService, IkonApplicationProperties applicationProperties) {
        this.taskRepository = taskMongoRepository;
        this.modelMapper = modelMapper;
        this.dataAccessFilter = dataAccessFilter;
        this.ikonGroupService = ikonGroupService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        TaskElasticsearch task = modelMapper.map(request, TaskElasticsearch.class);

        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setId(UUID.randomUUID());

        task.setAccountId(getActiveAccountId());

        TaskElasticsearch savedTask = taskRepository.save(task);

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
        TaskElasticsearch task = dataAccessFilter.findByIdOrThrow(TaskElasticsearch.class, new ObjectId(id));
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
                .findAll(TaskElasticsearch.class, filters,AccessCriteria.builder().build())
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
                .findAll(TaskElasticsearch.class, filters,
                        AccessCriteria.defaults(),
                        pageable)
                .map(task -> modelMapper.map(task, TaskResponse.class));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String id, TaskRequest request) {
        TaskElasticsearch existingTask = taskRepository.findById(UUID.fromString(id))
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

        TaskElasticsearch updatedTask = taskRepository.save(existingTask);
        return modelMapper.map(updatedTask, TaskResponse.class);
    }

    @Override
    @Transactional
    public void deleteTask(String id) {
        UUID objectId = UUID.fromString(id);
        if (!taskRepository.existsById(objectId)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(objectId);
    }

}
