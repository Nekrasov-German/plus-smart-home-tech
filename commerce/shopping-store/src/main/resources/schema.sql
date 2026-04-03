CREATE TABLE IF NOT EXISTS product (
    product_id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    image_src VARCHAR(512),
    quantity_state VARCHAR(50) NOT NULL,
    product_state VARCHAR(50) NOT NULL,
    product_category VARCHAR(50) NOT NULL,
    price DECIMAL(15, 2) NOT NULL CHECK (price >= 0)
);

