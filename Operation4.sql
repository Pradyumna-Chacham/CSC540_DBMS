use csc540;

/* =========================================================
   4. REPORTS
   Treat each “per ...” as an independent operation
   ========================================================= */

/* 4A. Weekly report: number and total price of copies of each publication bought per distributor, per week */
SELECT d.DistributorID,
       d.Name AS DistributorName,
       o.PublicationID,
       YEAR(o.OrderDate) AS ReportYear,
       WEEK(o.OrderDate) AS ReportWeek,
       SUM(o.Quantity) AS TotalCopies,
       SUM(o.Quantity * o.UnitPrice) AS TotalPrice
FROM Orders o
JOIN Distributors d ON o.DistributorID = d.DistributorID
GROUP BY d.DistributorID, d.Name, o.PublicationID, YEAR(o.OrderDate), WEEK(o.OrderDate)
ORDER BY ReportYear, ReportWeek, d.DistributorID, o.PublicationID;

/* 4B. Monthly report: number and total price of copies of each publication bought per distributor, per month */
SELECT d.DistributorID,
       d.Name AS DistributorName,
       o.PublicationID,
       YEAR(o.OrderDate) AS ReportYear,
       MONTH(o.OrderDate) AS ReportMonth,
       SUM(o.Quantity) AS TotalCopies,
       SUM(o.Quantity * o.UnitPrice) AS TotalPrice
FROM Orders o
JOIN Distributors d ON o.DistributorID = d.DistributorID
GROUP BY d.DistributorID, d.Name, o.PublicationID, YEAR(o.OrderDate), MONTH(o.OrderDate)
ORDER BY ReportYear, ReportMonth, d.DistributorID, o.PublicationID;

/* 4C. Total revenue of the publishing house
   Assumption: revenue is based on billed amounts only */
SELECT COALESCE(SUM(TotalBilledAmount), 0) AS TotalRevenue
FROM Orders
WHERE TotalBilledAmount IS NOT NULL;

/* 4D. Total expenses of the publishing house
   Assumption: expenses = shipping costs + staff payments */
SELECT
    (SELECT COALESCE(SUM(ShipCost), 0) FROM Orders) +
    (SELECT COALESCE(SUM(Amount), 0) FROM StaffPayments) AS TotalExpenses;

/* 4E. Total current number of distributors */
SELECT COUNT(*) AS TotalCurrentDistributors
FROM Distributors;

/* 4F. Total revenue since inception per city */
SELECT d.City,
       COALESCE(SUM(o.TotalBilledAmount), 0) AS RevenuePerCity
FROM Distributors d
LEFT JOIN Orders o ON d.DistributorID = o.DistributorID
GROUP BY d.City
ORDER BY RevenuePerCity DESC;

/* 4G. Total revenue since inception per distributor */
SELECT d.DistributorID,
       d.Name,
       COALESCE(SUM(o.TotalBilledAmount), 0) AS RevenuePerDistributor
FROM Distributors d
LEFT JOIN Orders o ON d.DistributorID = o.DistributorID
GROUP BY d.DistributorID, d.Name
ORDER BY RevenuePerDistributor DESC;

/* 4H. Total revenue since inception per location
   Assumption: location = country */
SELECT d.Country,
       COALESCE(SUM(o.TotalBilledAmount), 0) AS RevenuePerLocation
FROM Distributors d
LEFT JOIN Orders o ON d.DistributorID = o.DistributorID
GROUP BY d.Country
ORDER BY RevenuePerLocation DESC;

/* 4I. Total payments to editors and authors per month */
SELECT YEAR(IssueDate) AS PayYear,
       MONTH(IssueDate) AS PayMonth,
       SUM(Amount) AS TotalPayments
FROM StaffPayments
GROUP BY YEAR(IssueDate), MONTH(IssueDate)
ORDER BY PayYear, PayMonth;

/* 4J. Total payments by work type */
SELECT PaymentType,
       SUM(Amount) AS TotalPaymentsByWorkType
FROM StaffPayments
GROUP BY PaymentType
ORDER BY PaymentType;