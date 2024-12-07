package africa.pk.data.repository;

import africa.pk.data.model.ToDo;
import africa.pk.data.model.ToDoEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ToDoEntryRepository extends MongoRepository<ToDoEntry,String> {
    ToDoEntry findByTitle(String title);
    ToDoEntry findByid(String id);
    Optional<ToDo> findUserByUserName(String userName);
}
