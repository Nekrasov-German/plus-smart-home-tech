CREATE TABLE IF NOT EXISTS payment (
    payment_id UUID PRIMARY KEY,
    total_payment NUMERIC(15, 2),
    delivery_total NUMERIC(15, 2),
    fee_total NUMERIC(15, 2),
    payment_state VARCHAR(20) DEFAULT 'PENDING'
);

CREATE INDEX IF NOT EXISTS idx_payment_total_payment ON payment(total_payment);
CREATE INDEX IF NOT EXISTS idx_payment_delivery_total ON payment(delivery_total);
CREATE INDEX IF NOT EXISTS idx_payment_fee_total ON payment(fee_total);