package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() != null && item.getOwner().getId().equals(ownerId)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase();
        List<Item> result = new ArrayList<>();

        for (Item item : items.values()) {
            if (Boolean.TRUE.equals(item.getAvailable()) &&
                    (item.getName().toLowerCase().contains(searchText) ||
                            item.getDescription().toLowerCase().contains(searchText))) {
                result.add(item);
            }
        }
        return result;
    }

    public void deleteById(Long id) {
        items.remove(id);
    }

    public boolean existsById(Long id) {
        return items.containsKey(id);
    }
}