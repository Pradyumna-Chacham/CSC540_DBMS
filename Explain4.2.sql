use proj2;

/* Query 1: Find articles by topic */
SELECT id, content_title, topic, date_written
FROM Content
WHERE topic = 'Science'
  AND content_type = 'ARTICLE';


/* Explain before Index*/
EXPLAIN
SELECT id, content_title, topic, date_written
FROM Content
WHERE topic = 'Science'
  AND content_type = 'ARTICLE';

/*Create Index*/
CREATE INDEX idx_content_type_topic
ON Content(content_type, topic);

/* Explain after Index*/
EXPLAIN
SELECT id, content_title, topic, date_written
FROM Content
WHERE topic = 'Science'
  AND content_type = 'ARTICLE';

/* Query 2: List distributors by type and city */
SELECT id, name, address, phone_number, contact_name, balance
FROM Distributors
WHERE type = 'BOOKSTORE'
  AND city = 'Raleigh';

/* Explain before Index*/
EXPLAIN
SELECT id, name, address, phone_number, contact_name, balance
FROM Distributors
WHERE type = 'BOOKSTORE'
  AND city = 'Raleigh';

/*Create Index*/
CREATE INDEX idx_distributors_type_city
ON Distributors(type, city);

/* Explain after Index*/
EXPLAIN
SELECT id, name, address, phone_number, contact_name, balance
FROM Distributors
WHERE type = 'BOOKSTORE'
  AND city = 'Raleigh';