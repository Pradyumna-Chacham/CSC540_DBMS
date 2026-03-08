use csc540;

/* Query 1: Find content by topic */
SELECT ContentID, ContentTitle, Topic, DateWritten
FROM Content
WHERE Topic = 'Technology';


/* Explain before Index*/
EXPLAIN
SELECT ContentID, ContentTitle, Topic, DateWritten
FROM Content
WHERE Topic = 'Technology';

/*Create Index*/
CREATE INDEX idx_content_topic
ON Content(Topic);

/* Explain after Index*/
EXPLAIN
SELECT ContentID, ContentTitle, Topic, DateWritten
FROM Content
WHERE Topic = 'Technology';

/* Query 2: List distributors by type and city */
SELECT DistributorID, Name, Address, PhoneNumber, ContactName, Balance
FROM Distributors
WHERE Type = 'Bookstore'
  AND City = 'Raleigh';

/* Explain before Index*/
EXPLAIN
SELECT DistributorID, Name, Address, PhoneNumber, ContactName, Balance
FROM Distributors
WHERE Type = 'Bookstore'
  AND City = 'Raleigh';

/*Create Index*/
CREATE INDEX idx_distributors_type_city
ON Distributors(Type, City);

/* Explain after Index*/
EXPLAIN
SELECT DistributorID, Name, Address, PhoneNumber, ContactName, Balance
FROM Distributors
WHERE Type = 'Bookstore'
  AND City = 'Raleigh';