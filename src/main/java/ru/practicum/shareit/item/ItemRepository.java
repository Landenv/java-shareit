package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Repository;

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
        return List.copyOf(items.values());
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .toList();
    }

    public List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> containsText(item, searchText))
                .toList();
    }

    private boolean containsText(Item item, String searchText) {
        return item.getName().toLowerCase().contains(searchText) ||
                item.getDescription().toLowerCase().contains(searchText);
    }

    public void deleteById(Long id) {
        items.remove(id);
    }

    public boolean existsById(Long id) {
        return items.containsKey(id);
    }
}