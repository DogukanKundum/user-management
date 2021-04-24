package user.management.dtos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import user.management.model.User;

@Repository
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByToken(String token);
}