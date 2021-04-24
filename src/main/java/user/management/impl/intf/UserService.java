package user.management.impl.intf;

import user.management.dtos.UserDto;
import user.management.model.User;

import java.util.List;

public interface UserService {

    User findByUsername(String username);

    User getUserByToken(String token);

    User save(UserDto user);

    List<User> findAll();

    User findOne(String username);

    void delete(User user);
}
