USE proj2;

/* =========================================================
   3. DISTRIBUTION
   ========================================================= */


/* --------------------------------------------------
3A. Enter new distributor
-------------------------------------------------- */

/* Before insert */
SELECT * 
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';

/* Insert new distributor */
INSERT INTO Distributors
(id, name, address, type, phone_number, contact_name, balance, city, country)
VALUES
('00000000-0000-0000-0000-000000000206', 'Scholars Point', '101 College Ave', 'LIBRARY', '9196666666', 'Nina Patel', 0.00, 'Raleigh', 'USA');

/* After insert */
SELECT * 
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';



/* --------------------------------------------------
3B. Update distributor information
-------------------------------------------------- */

/* Before update */
SELECT id, name, phone_number, contact_name
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';

/* Update distributor information */
UPDATE Distributors
SET phone_number = '9197777777',
    contact_name = 'Nina Shah'
WHERE id = '00000000-0000-0000-0000-000000000206';

/* After update */
SELECT id, name, phone_number, contact_name
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';



/* --------------------------------------------------
3C. Delete a distributor
-------------------------------------------------- */

/* Before delete */
SELECT * 
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';

/* Delete distributor */
DELETE FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';

/* After delete */
SELECT * 
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000206';



/* --------------------------------------------------
3D. Input an order from a distributor for a publication
-------------------------------------------------- */

/* Before insert */
SELECT * 
FROM Orders
WHERE id = '00000000-0000-0000-0000-000000000706';

/* Insert new order */
/* In application code, fetch the current price of the edition or issue as unit_price*/
/* In application code, check if the corresponding edition or issue is published*/
INSERT INTO Orders
(id, quantity, unit_price, ship_cost, order_date, required_by_date, billed_date, total_billed_amount, distributor_id, edition_issue_id)
VALUES
('00000000-0000-0000-0000-000000000706', 60, 20.00, 25.00, '2024-04-06', '2024-04-15', NULL, NULL, '00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000401');

/* After insert */
SELECT * 
FROM Orders
WHERE id = '00000000-0000-0000-0000-000000000706';



/* --------------------------------------------------
3E. Bill distributor for an order
   Assumption: total_billed_amount = quantity * unit_price + ship_cost
-------------------------------------------------- */

/* Before billing: order state */
SELECT id, quantity, unit_price, ship_cost, billed_date, total_billed_amount, distributor_id
FROM Orders
WHERE id = '00000000-0000-0000-0000-000000000706';

/* Before billing: distributor balance */
SELECT id, name, balance
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000201';

/* Step 1: set billed date and billed amount */
UPDATE Orders
SET billed_date = '2024-04-07',
    total_billed_amount = (quantity * unit_price) + ship_cost
WHERE id = '00000000-0000-0000-0000-000000000706';

/* Check order after billing update */
SELECT id, quantity, unit_price, ship_cost, billed_date, total_billed_amount, distributor_id
FROM Orders
WHERE id = '00000000-0000-0000-0000-000000000706';

/* Step 2: add billed amount to distributor balance */
UPDATE Distributors d
JOIN Orders o ON d.id = o.distributor_id
SET d.balance = d.balance + o.total_billed_amount
WHERE o.id = '00000000-0000-0000-0000-000000000706';

/* After billing: distributor balance */
SELECT id, name, balance
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000201';



/* --------------------------------------------------
3F. Receive a payment and change outstanding balance of a distributor
-------------------------------------------------- */

/* Before payment: distributor balance */
SELECT id, name, balance
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000201';

/* Before payment: payment record */
SELECT *
FROM DistPayment
WHERE id = '00000000-0000-0000-0000-000000000806';

/* Step 1: record distributor payment */
INSERT INTO DistPayment (id, amount, pay_date, distributor_id)
VALUES ('00000000-0000-0000-0000-000000000806', 500.00, '2024-04-08', '00000000-0000-0000-0000-000000000201');

/* Check payment record after insert */
SELECT *
FROM DistPayment
WHERE id = '00000000-0000-0000-0000-000000000806';

/* Step 2: reduce distributor balance */
UPDATE Distributors
SET balance = balance - 500.00
WHERE id = '00000000-0000-0000-0000-000000000201';

/* After payment: distributor balance */
SELECT id, name, balance
FROM Distributors
WHERE id = '00000000-0000-0000-0000-000000000201';



/* --------------------------------------------------
3G. Identify distributors whose total billed amount
    does not match the sum of recorded payments
-------------------------------------------------- */

SELECT d.id AS distributor_id,
       d.name,
       COALESCE(SUM(o.total_billed_amount), 0) AS total_billed,
       COALESCE(SUM(dp.amount), 0) AS total_paid
FROM Distributors d
LEFT JOIN Orders o
    ON d.id = o.distributor_id
LEFT JOIN DistPayment dp
    ON d.id = dp.distributor_id
GROUP BY d.id, d.name
HAVING COALESCE(SUM(o.total_billed_amount), 0) <> COALESCE(SUM(dp.amount), 0);



/* --------------------------------------------------
3H. List all distributors of a specific type located in a given city
-------------------------------------------------- */

SELECT id AS distributor_id, name, address, phone_number, contact_name, balance
FROM Distributors
WHERE type = 'BOOKSTORE'
  AND city = 'Raleigh' and country='USA';