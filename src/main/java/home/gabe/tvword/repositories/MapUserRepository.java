package home.gabe.tvword.repositories;

import home.gabe.tvword.model.User;
import home.gabe.tvword.model.UserRole;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("map")
public class MapUserRepository implements UserRepository {

    private Map<Long, User> users = new HashMap<>();
    private Long autoId = Long.valueOf(1);

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        if (users.containsKey(id))
            return Optional.of(users.get(id));
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public User findByName(String name) {
        if (name == null)
            return null;

        return users.values().stream()
                .filter(user -> name.equals(user.getName()))
                .findFirst().get();
    }

    @Override
    public Iterable<User> findByRole(UserRole role) {
        Set<User> result = new HashSet<>();
        users.values().stream().filter(user -> user.getRole().equals(role)).forEach(user -> result.add(user));
        return result;
    }

    @Override
    public Long countByRole(UserRole role) {
        if (users.size() <= 0)
            return 0L;
        return users.values().stream().filter(user -> user.getRole().equals(role)).count();
    }

    @Override
    public Set<User> findAll() {
        return new HashSet<>(users.values());
    }

    @Override
    public Iterable<User> findAllById(Iterable<Long> iterable) {
        Set<User> result = new HashSet<>();
        iterable.forEach(id -> {
            Optional<User> user = findById(id);
            if (user.isPresent())
                result.add(user.get());
        });
        return result;
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public void deleteById(Long id) {
        Optional<User> user = findById(id);
        if (user.isPresent())
            delete(user.get());
    }

    @Override
    public User save(User user) {
        user.setId(autoId);
        users.put(autoId, user);

        autoId++;
        return user;
    }

    @Override
    public void delete(User user) {
        if (user == null)
            return;
        users.remove(user.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends User> iterable) {
        iterable.forEach(user -> delete(user));
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
