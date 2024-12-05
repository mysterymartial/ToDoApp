package africa.pk.data.repository;

import africa.pk.data.model.ToDoEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ToDoEntryRepository extends MongoRepository<ToDoEntry,String> {
    ToDoEntry findByTitle(String title);
    ToDoEntry findByid(String id);
}
