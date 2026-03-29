-- Создание таблицы product с учётом встраиваемого объекта Dimension
CREATE TABLE IF NOT EXISTS product (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DECIMAL(10, 2),
    height DECIMAL(10, 2),
    depth DECIMAL(10, 2),
    quantity INTEGER,
    weight DECIMAL(10, 2)
);

-- Индексы для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_product_fragile ON product(fragile);
CREATE INDEX IF NOT EXISTS idx_product_weight ON product(weight);
CREATE INDEX IF NOT EXISTS idx_product_quantity ON product(quantity);

-- Комментарий к таблице
COMMENT ON TABLE product IS 'Таблица товаров на складе. Содержит основные характеристики товара, включая габариты (width, height, depth) как часть составного поля Dimension';

-- Комментарии к колонкам
COMMENT ON COLUMN product.product_id IS 'Уникальный идентификатор товара (UUID)';
COMMENT ON COLUMN product.fragile IS 'Признак хрупкости товара';
COMMENT ON COLUMN product.width IS 'Ширина товара (часть встраиваемого объекта Dimension)';
COMMENT ON COLUMN product.height IS 'Высота товара (часть встраиваемого объекта Dimension)';
COMMENT ON COLUMN product.depth IS 'Глубина товара (часть встраиваемого объекта Dimension)';
COMMENT ON COLUMN product.quantity IS 'Количество товара на складе';
COMMENT ON COLUMN product.weight IS 'Вес товара в килограммах';