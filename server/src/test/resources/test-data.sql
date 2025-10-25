-- Очистка перед тестами
DELETE FROM comments;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requests;
DELETE FROM users;

-- Сброс sequence
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items ALTER COLUMN id RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;

-- Тестовые пользователи
INSERT INTO users (id, name, email) VALUES
(1, 'User1', 'user1@example.com'),
(2, 'User2', 'user2@example.com'),
(3, 'User3', 'user3@example.com');

-- Тестовые предметы
INSERT INTO items (id, name, description, is_available, owner_id, request_id) VALUES
(1, 'Item1', 'Description1', true, 1, null),
(2, 'Item2', 'Description2', true, 2, null),
(3, 'Item3', 'Description3', false, 1, null);

-- Тестовые бронирования
INSERT INTO bookings (id, start_date, end_date, item_id, booker_id, status) VALUES
(1, '2023-01-01 10:00:00', '2023-01-02 10:00:00', 1, 2, 'APPROVED'),
(2, '2023-01-03 10:00:00', '2023-01-04 10:00:00', 2, 1, 'WAITING');

-- Тестовые комментарии
INSERT INTO comments (id, text, item_id, author_id, created) VALUES
(1, 'Great item!', 1, 2, '2023-01-05 10:00:00');