-- Универсальные последовательности
CREATE SEQUENCE family_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE guest_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE visit_history_record_seq START WITH 1 INCREMENT BY 50;

-- Таблица family с инлайн-констрейнтами
CREATE TABLE family (
    id BIGINT NOT NULL PRIMARY KEY,
    is_active BOOLEAN,
    appeal VARCHAR(500) NOT NULL,
    confirmation_deadline DATE NOT NULL,
    max_available_guest_count INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    personal_link VARCHAR(255) UNIQUE,
    phone VARCHAR(255),
    is_placement_required BOOLEAN,
    is_transfer_required BOOLEAN,
    version BIGINT,
    CONSTRAINT family_max_guest_check CHECK (max_available_guest_count >= 1 AND max_available_guest_count <= 10)
);

-- Таблица guest с инлайн-констрейнтами
CREATE TABLE guest (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    will_attend BOOLEAN NOT NULL,
    family_id BIGINT NOT NULL,
    CONSTRAINT fkkhtm2gvdddxktx0eudchw8s8t FOREIGN KEY (family_id) REFERENCES family (id)
);

-- Таблица guest_beverages с универсальным CHECK
CREATE TABLE guest_beverages (
    guest_id BIGINT NOT NULL,
    beverage VARCHAR(255),
    CONSTRAINT guest_beverages_beverage_check CHECK (beverage IN ('DRY_WINE', 'SEMI_SWEET_WINE', 'COGNAC', 'VODKA', 'CHAMPAGNE', 'NON_ALCOHOLIC')),
    CONSTRAINT fk3ba073l8bifwxndcwbw9rhqr7 FOREIGN KEY (guest_id) REFERENCES guest (id)
);

-- Таблица visit_history_record
CREATE TABLE visit_history_record (
    id BIGINT NOT NULL PRIMARY KEY,
    uri VARCHAR(255) NOT NULL,
    personal_link VARCHAR(255) NOT NULL,
    remote_ip_address VARCHAR(255) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    visited_at TIMESTAMP NOT NULL,
    family_id BIGINT,
    CONSTRAINT fkfmkijt9phylfdqv0h68c1s4na FOREIGN KEY (family_id) REFERENCES family (id)
);