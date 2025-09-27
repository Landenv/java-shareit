package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public User save(User user) {
        for (User existingUser : users.values()) {
            boolean isDuplicate = user.getId() == null
                    ? existingUser.getEmail().equalsIgnoreCase(user.getEmail())
                    : !existingUser.getId().equals(user.getId()) &&
                    existingUser.getEmail().equalsIgnoreCase(user.getEmail());

            if (isDuplicate) {
                throw new EmailConflictException("User with email " + user.getEmail() + " already exists");
            }
        }

        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}