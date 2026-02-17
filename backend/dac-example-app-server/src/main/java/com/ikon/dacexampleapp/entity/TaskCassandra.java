package com.ikon.dacexampleapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;

import java.time.LocalDateTime; // Or use Instant for better UTC handling
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCassandra {

    @PrimaryKey
    private UUID id;

    // Standard column (Note: Querying by this will be slow without an index)
    private UUID accountId;

    private String title;

    private String description;

    // Enums are stored as Strings by default in Spring Data Cassandra
    private TaskStatus status = TaskStatus.PENDING;

    private TaskPriority priority = TaskPriority.MEDIUM;

    // Cassandra stores sets natively. No @ElementCollection needed.
    private Set<String> dynamicGroups = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}