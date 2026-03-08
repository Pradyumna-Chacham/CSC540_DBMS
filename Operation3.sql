USE CSC540;

/* =========================================================
   3. DISTRIBUTION
   ========================================================= */


/* --------------------------------------------------
3A. Enter new distributor
-------------------------------------------------- */

/* Before insert */
SELECT * 
FROM Distributors
WHERE DistributorID = 'DST006';

/* Insert new distributor */
INSERT INTO Distributors
(DistributorID, Name, Address, Type, PhoneNumber, ContactName, Balance, City, Country)
VALUES
('DST006', 'Scholars Point', '101 College Ave', 'Library', '9196666666', 'Nina Patel', 0.00, 'Raleigh', 'USA');

/* After insert */
SELECT * 
FROM Distributors
WHERE DistributorID = 'DST006';



/* --------------------------------------------------
3B. Update distributor information
-------------------------------------------------- */

/* Before update */
SELECT DistributorID, Name, PhoneNumber, ContactName
FROM Distributors
WHERE DistributorID = 'DST006';

/* Update distributor information */
UPDATE Distributors
SET PhoneNumber = '9197777777',
    ContactName = 'Nina Shah'
WHERE DistributorID = 'DST006';

/* After update */
SELECT DistributorID, Name, PhoneNumber, ContactName
FROM Distributors
WHERE DistributorID = 'DST006';



/* --------------------------------------------------
3C. Delete a distributor
-------------------------------------------------- */

/* Before delete */
SELECT * 
FROM Distributors
WHERE DistributorID = 'DST006';

/* Delete distributor */
DELETE FROM Distributors
WHERE DistributorID = 'DST006';

/* After delete */
SELECT * 
FROM Distributors
WHERE DistributorID = 'DST006';



/* --------------------------------------------------
3D. Input an order from a distributor for a publication
-------------------------------------------------- */

/* Before insert */
SELECT * 
FROM Orders
WHERE OrderID = 'ORD006';

/* Insert new order */
INSERT INTO Orders
(OrderID, Quantity, UnitPrice, ShipCost, OrderDate, RequiredByDate, BilledDate, TotalBilledAmount, DistributorID, PublicationID)
VALUES
('ORD006', 60, 20.00, 25.00, '2024-04-06', '2024-04-15', NULL, NULL, 'DST001', 'PUB006');

/* After insert */
SELECT * 
FROM Orders
WHERE OrderID = 'ORD006';



/* --------------------------------------------------
3E. Bill distributor for an order
   Assumption: TotalBilledAmount = Quantity * UnitPrice + ShipCost
-------------------------------------------------- */

/* Before billing: order state */
SELECT OrderID, Quantity, UnitPrice, ShipCost, BilledDate, TotalBilledAmount, DistributorID
FROM Orders
WHERE OrderID = 'ORD006';

/* Before billing: distributor balance */
SELECT DistributorID, Name, Balance
FROM Distributors
WHERE DistributorID = 'DST001';

/* Step 1: set billed date and billed amount */
UPDATE Orders
SET BilledDate = '2024-04-07',
    TotalBilledAmount = (Quantity * UnitPrice) + ShipCost
WHERE OrderID = 'ORD006';

/* Check order after billing update */
SELECT OrderID, Quantity, UnitPrice, ShipCost, BilledDate, TotalBilledAmount, DistributorID
FROM Orders
WHERE OrderID = 'ORD006';

/* Step 2: add billed amount to distributor balance */
UPDATE Distributors d
JOIN Orders o ON d.DistributorID = o.DistributorID
SET d.Balance = d.Balance + o.TotalBilledAmount
WHERE o.OrderID = 'ORD006';

/* After billing: distributor balance */
SELECT DistributorID, Name, Balance
FROM Distributors
WHERE DistributorID = 'DST001';



/* --------------------------------------------------
3F. Receive a payment and change outstanding balance of a distributor
-------------------------------------------------- */

/* Before payment: distributor balance */
SELECT DistributorID, Name, Balance
FROM Distributors
WHERE DistributorID = 'DST001';

/* Before payment: payment record */
SELECT *
FROM DistPayment
WHERE DistPayID = 'DPY006';

/* Step 1: record distributor payment */
INSERT INTO DistPayment (DistPayID, Amount, PayDate, DistributorID)
VALUES ('DPY006', 500.00, '2024-04-08', 'DST001');

/* Check payment record after insert */
SELECT *
FROM DistPayment
WHERE DistPayID = 'DPY006';

/* Step 2: reduce distributor balance */
UPDATE Distributors
SET Balance = Balance - 500.00
WHERE DistributorID = 'DST001';

/* After payment: distributor balance */
SELECT DistributorID, Name, Balance
FROM Distributors
WHERE DistributorID = 'DST001';



/* --------------------------------------------------
3G. Identify distributors whose total billed amount
    does not match the sum of recorded payments
-------------------------------------------------- */

SELECT d.DistributorID,
       d.Name,
       COALESCE(SUM(o.TotalBilledAmount), 0) AS TotalBilled,
       COALESCE(SUM(dp.Amount), 0) AS TotalPaid
FROM Distributors d
LEFT JOIN Orders o
    ON d.DistributorID = o.DistributorID
LEFT JOIN DistPayment dp
    ON d.DistributorID = dp.DistributorID
GROUP BY d.DistributorID, d.Name
HAVING COALESCE(SUM(o.TotalBilledAmount), 0) <> COALESCE(SUM(dp.Amount), 0);



/* --------------------------------------------------
3H. List all distributors of a specific type located in a given city
-------------------------------------------------- */

SELECT DistributorID, Name, Address, PhoneNumber, ContactName, Balance
FROM Distributors
WHERE Type = 'Bookstore'
  AND City = 'Raleigh' and Country='USA';