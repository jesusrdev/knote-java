package dev.imyisus.knote_java;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotesRepository extends MongoRepository<Note, String> {
}
