CREATE TABLE timeline_item (
    id BIGINT NOT NULL PRIMARY KEY,
    display_order INTEGER NOT NULL, -- Порядок сортировки
    event_time TIME NOT NULL,        -- Время события
    title VARCHAR(255) NOT NULL,     -- Название (Сбор гостей, Церемония)
    icon_name VARCHAR(255) NOT NULL   -- Имя файла иконки (например, 'rings.png')
);

CREATE SEQUENCE timeline_item_seq START WITH 1 INCREMENT BY 50;