DELETE FROM orders;
DELETE FROM customers;

INSERT INTO customers (id, name, city) VALUES
    (1, 'Rahul Sharma', 'Pune'),
    (2, 'Ananya Rao', 'Mumbai'),
    (3, 'Vikram Singh', 'Delhi');

INSERT INTO orders (customer_id, product_name, amount, order_date) VALUES
    (1, 'Laptop', 55000.00, '2026-08-01'),
    (1, 'Wireless Mouse', 800.00, '2026-08-10'),
    (2, 'Mechanical Keyboard', 4500.00, '2026-09-05'),
    (3, 'Monitor', 12000.00, '2026-09-15'),
    (2, 'Headphones', 2500.00, '2026-09-20');