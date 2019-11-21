package home.gabe.tvword.repositories;

import home.gabe.tvword.model.User;
import home.gabe.tvword.model.UserRole;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String name);

    Iterable<User> findByRole(UserRole role);
}
