-- Создание таблицы корзин
CREATE TABLE carts (
    shopping_cart_id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    cart_state VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'  -- соответствует enum CartState
);

-- Создание таблицы элементов корзины для хранения Map<String, Integer>
CREATE TABLE cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,  -- ключ из Map (ID товара)
    quantity INTEGER NOT NULL DEFAULT 1,   -- значение из Map (количество)
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (shopping_cart_id)
        REFERENCES carts(shopping_cart_id) ON DELETE CASCADE,
    -- Уникальность: один товар может быть только один раз в одной корзине
    CONSTRAINT uk_cart_items_product UNIQUE (shopping_cart_id, product_id)
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_cart_items_cart_id ON cart_items(shopping_cart_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX idx_carts_cart_state ON carts(cart_state);