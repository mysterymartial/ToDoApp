package africa.pk.data.repository;

import africa.pk.data.model.ToDo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToDoRepository extends MongoRepository<ToDo, String> {
    ToDo findByUserName(String userName);
    Optional<ToDo> findUserByUserName(String userName);
//    ToDo findByUserNameinTodoRepository(String userName);
}
