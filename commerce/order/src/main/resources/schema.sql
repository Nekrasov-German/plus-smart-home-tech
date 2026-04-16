-- Основная таблица заказов
CREATE TABLE IF NOT EXISTS orders (
    order_id UUID PRIMARY KEY,
    shopping_card_id UUID NOT NULL,
    payment_id UUID NOT NULL,
    delivery_id UUID NOT NULL,
    state VARCHAR(255) NOT NULL,
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price DOUBLE PRECISION,
    delivery_price DOUBLE PRECISION,
    product_price DOUBLE PRECISION
);

-- Таблица элементов заказа (для Map<UUID, Integer>)
CREATE TABLE IF NOT EXISTS order_items (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_items_cart FOREIGN KEY (order_id)
        REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Индексы для ускорения запросов
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

-- Индексы для основной таблицы
CREATE INDEX IF NOT EXISTS idx_orders_shopping_card_id ON orders(shopping_card_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment_id ON orders(payment_id);
CREATE INDEX IF NOT EXISTS idx_orders_delivery_id ON orders(delivery_id);
CREATE INDEX IF NOT EXISTS idx_orders_state ON orders(state);