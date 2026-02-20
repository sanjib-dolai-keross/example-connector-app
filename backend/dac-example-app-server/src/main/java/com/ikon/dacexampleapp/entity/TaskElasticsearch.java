package com.ikon.dacexampleapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.ikon.dacexampleapp.enums.TaskPriority;
import com.ikon.dacexampleapp.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document(indexName = "taskss")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskElasticsearch {

    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;

    @Field(type = FieldType.Keyword)
    private UUID accountId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private TaskStatus status;

    @Field(type = FieldType.Keyword)
    private TaskPriority priority;

    @Field(type = FieldType.Keyword)
    private Set<String> dynamicGroups;

    @Field(type = FieldType.Date)
    @CreatedDate
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date)
    @LastModifiedDate
    private LocalDateTime updatedAt;

}