-- Таблица адресов (адреса теперь независимы)
CREATE TABLE IF NOT EXISTS address (
    address_id UUID PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(50) NOT NULL,
    flat VARCHAR(50)
);

-- Таблица доставок
CREATE TABLE IF NOT EXISTS delivery (
    delivery_id UUID PRIMARY KEY,
    from_address_id UUID NOT NULL,
    to_address_id UUID NOT NULL,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(20) NOT NULL,
    -- Внешние ключи для связи с таблицей адресов
    CONSTRAINT fk_delivery_from_address FOREIGN KEY (from_address_id) REFERENCES address(address_id),
    CONSTRAINT fk_delivery_to_address FOREIGN KEY (to_address_id) REFERENCES address(address_id)
);