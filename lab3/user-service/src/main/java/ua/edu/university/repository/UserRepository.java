package ua.edu.university.repository;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.university.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class UserRepository {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserRepository() {
        // Фейкові дані для тестування
        createUser(new User(null, "Іван", "Петренко", "ivan.petrenko@example.com", "+380501234567"));
        createUser(new User(null, "Марія", "Коваленко", "maria.kovalenko@example.com", "+380502345678"));
        createUser(new User(null, "Олександр", "Шевченко", "oleksandr.shevchenko@example.com", "+380503456789"));
    }

    public User createUser(User user) {
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        if (!users.containsKey(id)) {
            return Optional.empty();
        }
        updatedUser.setId(id);
        users.put(id, updatedUser);
        return Optional.of(updatedUser);
    }

    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}
