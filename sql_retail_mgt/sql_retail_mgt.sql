USE retail_db;

CREATE TABLE IF NOT EXISTS Categories (
    category_id INT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL
);

INSERT INTO Categories (category_id, category_name) VALUES
(1, 'Electronics'),
(2, 'Apparel'),
(3, 'Home Goods'),
(4, 'Groceries');


CREATE TABLE IF NOT EXISTS Suppliers (
    supplier_id INT PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL
);

INSERT INTO Suppliers (supplier_id, supplier_name, contact_email) VALUES
(101, 'Tech Innovations Inc.', 'contact@techinnovations.com'),
(102, 'StyleCo', 'info@styleco.com'),
(103, 'Home Essentials', 'sales@homeessentials.com'),
(104, 'Gourmet Foods Ltd.', 'info@gourmetfoods.com');

CREATE TABLE IF NOT EXISTS Products (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category_id INT,
    supplier_id INT,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES Suppliers(supplier_id)
);

INSERT INTO Products (product_id, product_name, category_id, supplier_id, price) VALUES
(1, 'Laptop', 1, 101, 1200.00),
(2, 'T-Shirt', 2, 102, 25.50),
(3, 'Coffee Maker', 3, 103, 75.00),
(4, 'Bananas', 4, 104, 0.50),
(5, 'Mouse', 1, 101, 30.00),
(6, 'Jeans', 2, 102, 50.00),
(7, 'Toaster', 3, NULL, 40.00);

CREATE TABLE IF NOT EXISTS Inventory (
    inventory_id INT PRIMARY KEY,
    product_id INT,
    quantity INT NOT NULL,
    last_updated DATE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

INSERT INTO Inventory (inventory_id, product_id, quantity, last_updated) VALUES
(1, 1, 5, '2024-09-01'),
(2, 2, 12, '2024-09-02'),
(3, 3, 8, '2024-09-01'),
(4, 4, 50, '2024-09-03'),
(5, 5, 20, '2024-09-03'),
(6, 6, 15, '2024-09-02'),
(7, 7, 2, '2024-09-01');

CREATE TABLE IF NOT EXISTS Orders (
    order_id INT PRIMARY KEY,
    product_id INT,
    order_date DATE NOT NULL,
    quantity_ordered INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

INSERT INTO Orders (order_id, product_id, order_date, quantity_ordered, total_price) VALUES
(1, 1, '2024-09-01', 1, 1200.00),
(2, 2, '2024-09-01', 2, 51.00),
(3, 4, '2024-09-02', 5, 2.50),
(4, 3, '2024-09-03', 1, 75.00),
(5, 5, '2024-09-03', 2, 60.00),
(6, 1, '2024-09-04', 1, 1200.00),
(7, 4, '2024-09-04', 10, 5.00),
(8, 2, '2024-09-04', 1, 25.50);


-- Task 1 : Inner Join
SELECT
  P.product_name,
  C.category_name
FROM
  Products AS P
INNER JOIN
  Categories AS C
ON
  P.category_id = C.category_id;
  
-- Task 2 : Left Join
SELECT
  P.product_name,
  S.supplier_name
FROM
  Products AS P
LEFT JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id;

-- Task 3 : Right Join
SELECT
  P.product_name,
  S.supplier_name
FROM
  Products AS P
RIGHT JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id;

-- Task 4 : Full Outer Join (Emulated)
SELECT
  P.product_name,
  S.supplier_name
FROM
  Products AS P
LEFT JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id
UNION
SELECT
  P.product_name,
  S.supplier_name
FROM
  Products AS P
RIGHT JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id;

-- Task 5 : Join with Inventory
SELECT
  P.product_name,
  S.supplier_name,
  I.quantity
FROM
  Products AS P
JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id
JOIN
  Inventory AS I
ON
  P.product_id = I.product_id;

-- Task 6 : Join with Orders
SELECT
  P.product_name,
  SUM(O.quantity_ordered) AS total_quantity,
  SUM(O.total_price) AS total_revenue
FROM
  Products AS P
JOIN
  Orders AS O
ON
  P.product_id = O.product_id
GROUP BY
  P.product_name
ORDER BY
  total_revenue DESC;

-- Task 7 : Multi-Table Join
SELECT
  O.order_id,
  O.order_date,
  P.product_name,
  C.category_name,
  S.supplier_name,
  O.quantity_ordered,
  O.total_price
FROM
  Orders AS O
JOIN
  Products AS P
ON
  O.product_id = P.product_id
JOIN
  Categories AS C
ON
  P.category_id = C.category_id
JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id;

-- Task 8 : Conditional Join
SELECT 
	P.product_name,
    S.supplier_name,
    I.quantity
FROM
	Products as P
JOIN
	Suppliers as S
ON
	P.supplier_id = S.supplier_id
JOIN
	Inventory AS i
ON
	P.product_id = I.product_id
WHERE
	I.quantity < 10;

-- Task 9
-- (i) Find Suppliers with products in multiple categories
SELECT
  S.supplier_name,
  COUNT(DISTINCT C.category_id) AS count_of_categories
FROM
  Products AS P
INNER JOIN
  Suppliers AS S
ON
  P.supplier_id = S.supplier_id
INNER JOIN
  Categories AS C
ON
  P.category_id = C.category_id
GROUP BY
  S.supplier_name
HAVING
  count_of_categories > 1;

-- (ii) Find products that have never been ordered
SELECT
  P.product_id,
  P.product_name,
  P.price
FROM
  Products AS P
LEFT JOIN
  Orders AS O
ON
  P.product_id = O.product_id
WHERE
  O.order_id IS NULL;

-- (iii) Find the category with the highest total sales
SELECT
  C.category_name,
  SUM(O.total_price) AS total_sales
FROM
  Orders AS O
JOIN
  Products AS P
ON
  O.product_id = P.product_id
JOIN
  Categories AS C
ON
  P.category_id = C.category_id
GROUP BY
  C.category_name
ORDER BY
  total_sales DESC
LIMIT 1;