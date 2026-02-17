package com.ikon.dacexampleapp.repository;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.ikon.dacexampleapp.entity.TaskCassandra;

@Repository
public interface TaskCassandraRepository extends CassandraRepository<TaskCassandra, UUID> {

}
