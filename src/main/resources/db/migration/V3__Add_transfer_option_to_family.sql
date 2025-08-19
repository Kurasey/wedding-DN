-- Удаляем старую колонку
ALTER TABLE family DROP COLUMN is_transfer_required;

-- Добавляем новую колонку для хранения опции трансфера как строки
-- NOT_REQUIRED будет значением по умолчанию для всех существующих семей
ALTER TABLE family ADD COLUMN transfer_option VARCHAR(255) NOT NULL DEFAULT 'NOT_REQUIRED';