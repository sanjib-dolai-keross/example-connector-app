package com.ikon.dacexampleapp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ikon.dacexampleapp.entity.TaskDocument;

@Repository
public interface TaskMongoRepository extends MongoRepository<TaskDocument, ObjectId> {

}
