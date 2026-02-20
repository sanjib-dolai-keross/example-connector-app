package com.ikon.dacexampleapp.repository;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.ikon.dacexampleapp.entity.TaskElasticsearch;

@Repository
public interface TaskElasticsearchRepository extends ElasticsearchRepository<TaskElasticsearch, UUID> {
}