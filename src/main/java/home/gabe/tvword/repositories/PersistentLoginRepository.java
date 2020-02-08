package home.gabe.tvword.repositories;

import home.gabe.tvword.model.PersistentLogin;
import org.springframework.data.repository.CrudRepository;

public interface PersistentLoginRepository extends CrudRepository<PersistentLogin, String> {
    PersistentLogin findByUsername(String username);

    void deleteByUsername(String username);
}
