package com.ikon.dacexampleapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCassandra {

    @PrimaryKeyColumn(name = "accountid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID accountId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID id;

    private String title;

    private String description;

    private TaskStatus status = TaskStatus.PENDING;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private Set<String> dynamicGroups = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}