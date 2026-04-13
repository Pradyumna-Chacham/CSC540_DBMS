use pchacha2;

/* =========================================================
   4. REPORTS
   Treating each “per ...” as an independent operation
   ========================================================= */

/* 4A. Number and total price of copies of each publication bought per distributor */
SELECT d.id AS distributor_id,
       d.name AS distributor_name,
       ei.publication_id,
       SUM(o.quantity) AS total_copies,
       SUM(o.quantity * o.unit_price) AS total_price
FROM Orders o
JOIN Distributors d ON o.distributor_id = d.id
JOIN EditionIssue ei ON o.edition_issue_id = ei.id
GROUP BY d.id, d.name, ei.publication_id
ORDER BY d.id, ei.publication_id;

/* 4B. Number and total price of copies of each publication bought per week */
SELECT YEAR(o.order_date) AS report_year,
       WEEK(o.order_date) AS report_week,
       ei.publication_id,
       SUM(o.quantity) AS total_copies,
       SUM(o.quantity * o.unit_price) AS total_price
FROM Orders o
JOIN EditionIssue ei ON o.edition_issue_id = ei.id
GROUP BY YEAR(o.order_date), WEEK(o.order_date), ei.publication_id
ORDER BY report_year, report_week, ei.publication_id;

/* 4C. Number and total price of copies of each publication bought per month */
SELECT YEAR(o.order_date) AS report_year,
       MONTH(o.order_date) AS report_month,
       ei.publication_id,
       SUM(o.quantity) AS total_copies,
       SUM(o.quantity * o.unit_price) AS total_price
FROM Orders o
JOIN EditionIssue ei ON o.edition_issue_id = ei.id
GROUP BY YEAR(o.order_date), MONTH(o.order_date), ei.publication_id
ORDER BY report_year, report_month, ei.publication_id;

/* 4D. Total revenue of the publishing house
   Assumption: revenue is based on billed amounts only */
SELECT COALESCE(SUM(total_billed_amount), 0) AS total_revenue
FROM Orders;

/* 4E. Total expenses of the publishing house
   Assumption: expenses = shipping costs + user payments */
SELECT
    (SELECT COALESCE(SUM(ship_cost), 0) FROM Orders) +
    (SELECT COALESCE(SUM(amount), 0) FROM UserPayments) AS total_expenses;

/* 4F. Total current number of distributors */
SELECT COUNT(*) AS total_current_distributors
FROM Distributors;

/* 4G. Total revenue since inception per city(of distributors) */
SELECT d.city,
       COALESCE(SUM(o.total_billed_amount), 0) AS revenue_per_city
FROM Distributors d
LEFT JOIN Orders o ON d.id = o.distributor_id
GROUP BY d.city
ORDER BY revenue_per_city DESC;

/* 4H. Total revenue since inception per distributor */
SELECT d.id AS distributor_id,
       d.name,
       COALESCE(SUM(o.total_billed_amount), 0) AS revenue_per_distributor
FROM Distributors d
LEFT JOIN Orders o ON d.id = o.distributor_id
GROUP BY d.id, d.name
ORDER BY revenue_per_distributor DESC;

/* 4I. Total revenue since inception per location
   Assumption: location = country */
SELECT d.country,
       COALESCE(SUM(o.total_billed_amount), 0) AS revenue_per_location
FROM Distributors d
LEFT JOIN Orders o ON d.id = o.distributor_id
GROUP BY d.country
ORDER BY revenue_per_location DESC;

/* 4J. Total payments to editors and authors per month */
SELECT YEAR(issue_date) AS pay_year,
       MONTH(issue_date) AS pay_month,
       SUM(amount) AS total_payments
FROM UserPayments
GROUP BY YEAR(issue_date), MONTH(issue_date)
ORDER BY pay_year, pay_month;

/* 4K. Total payments by work type */
SELECT payment_type,
       SUM(amount) AS total_payments_by_work_type
FROM UserPayments
GROUP BY payment_type
ORDER BY payment_type;