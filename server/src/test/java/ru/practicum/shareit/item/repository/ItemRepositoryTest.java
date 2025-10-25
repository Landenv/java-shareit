package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerIdOrderById_WithExistingOwner_ShouldReturnItems() {
        User owner = new User(null, "John", "john@mail.com");
        entityManager.persist(owner);

        Item item1 = new Item(null, "Drill", "Powerful", true, owner, null);
        Item item2 = new Item(null, "Hammer", "Heavy", true, owner, null);
        entityManager.persist(item1);
        entityManager.persist(item2);

        List<Item> items = itemRepository.findByOwnerIdOrderById(owner.getId());

        assertEquals(2, items.size());
        assertEquals("Drill", items.get(0).getName());
        assertEquals("Hammer", items.get(1).getName());
    }

    @Test
    void searchAvailableItems_WithMatchingText_ShouldReturnItems() {
        User owner = new User(null, "John", "john@mail.com");
        entityManager.persist(owner);

        Item item = new Item(null, "Power Drill", "Very powerful", true, owner, null);
        entityManager.persist(item);

        List<Item> items = itemRepository.searchAvailableItems("drill");

        assertEquals(1, items.size());
        assertEquals("Power Drill", items.get(0).getName());
    }

    @Test
    void searchAvailableItems_WithBlankText_ShouldReturnEmptyList() {
        List<Item> items = itemRepository.searchAvailableItems("");

        assertTrue(items.isEmpty());
    }
}