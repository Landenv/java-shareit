package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long idCounter = 1;

    public User save(User user) {
        String email = user.getEmail().toLowerCase();

        if (user.getId() == null) {
            if (emails.contains(email)) {
                throw new EmailConflictException("User with email " + user.getEmail() + " already exists");
            }
        } else {
            User existingUser = users.get(user.getId());
            if (existingUser != null && !existingUser.getEmail().equalsIgnoreCase(user.getEmail())) {
                if (emails.contains(email)) {
                    throw new EmailConflictException("User with email " + user.getEmail() + " already exists");
                }
                emails.remove(existingUser.getEmail().toLowerCase());
            }
        }

        if (user.getId() == null) {
            user.setId(idCounter++);
        }

        users.put(user.getId(), user);
        emails.add(email);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public void deleteById(Long id) {
        User user = users.get(id);
        if (user != null) {
            emails.remove(user.getEmail().toLowerCase());
            users.remove(id);
        }
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}