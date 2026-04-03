USE proj2;


/* =========================================================
   2. PRODUCTION OF A BOOK EDITION OR ISSUE
   ========================================================= */


/* --------------------------------------------------
2A. Enter new book edition
-------------------------------------------------- */

/* Before insert */
SELECT * FROM EditionIssue WHERE id = '00000000-0000-0000-0000-000000000406';

/* Insert new book edition */
/* In application code, check if the corresponding publication is a book in case of non-null isbn or edition_num values*/
INSERT INTO EditionIssue (id, edition_num, pub_date, status, isbn, price, publication_id)
VALUES ('00000000-0000-0000-0000-000000000406', 2, '2024-04-01', 'IN_PROGRESS', '9781111111111', 47.50, '00000000-0000-0000-0000-000000000101');

/* After insert */
SELECT * FROM EditionIssue WHERE id = '00000000-0000-0000-0000-000000000406';



/* --------------------------------------------------
2B. Enter a new issue of a periodic publication
-------------------------------------------------- */

/* Before insert */
SELECT * FROM EditionIssue WHERE id = '00000000-0000-0000-0000-000000000407';

/* Insert issue */
/* In application code, check if the corresponding publication is a journal or magazine in case of null isbn or edition_num values*/
INSERT INTO EditionIssue (id, edition_num, pub_date, status, isbn, price, publication_id)
VALUES ('00000000-0000-0000-0000-000000000407', NULL, '2024-04-05', 'IN_PROGRESS', NULL, 12.50, '00000000-0000-0000-0000-000000000102');

/* After insert */
SELECT * FROM EditionIssue WHERE id = '00000000-0000-0000-0000-000000000407';



/* --------------------------------------------------
2C. Update book edition / publication issue
-------------------------------------------------- */

/* Before update */
SELECT id, status
FROM EditionIssue
WHERE id = '00000000-0000-0000-0000-000000000404';

/* Update edition status */
/* For updating edition_num or isbn values, check if the corresponding publication is a book*/
UPDATE EditionIssue
SET status = 'PUBLISHED'
WHERE id = '00000000-0000-0000-0000-000000000404';

/* After update */
SELECT id, status
FROM EditionIssue
WHERE id = '00000000-0000-0000-0000-000000000404';



/* --------------------------------------------------
2D. Delete a book edition or publication issue
-------------------------------------------------- */

/* Insert temporary issue for deletion test */
/* In application code, check if the corresponding publication is a journal or magazine in case of null isbn or edition_num values*/
INSERT INTO EditionIssue (id, edition_num, pub_date, status, isbn, price, publication_id)
VALUES ('00000000-0000-0000-0000-000000000408', NULL, '2024-04-10', 'IN_PROGRESS', NULL, 15.00, '00000000-0000-0000-0000-000000000103');

/* Before delete */
SELECT * FROM EditionIssue WHERE id = '00000000-0000-0000-0000-000000000408';

/* Delete issue */
DELETE FROM EditionIssue
WHERE id = '00000000-0000-0000-0000-000000000408';

/* After delete */
SELECT * FROM EditionIssue WHERE id = '00000000-0000-0000-0000-000000000408';



/* --------------------------------------------------
2E. Enter a new article/chapter
-------------------------------------------------- */

/* Ensure author exists */
SELECT * FROM Person WHERE id = '00000000-0000-0000-0000-000000000306';

INSERT INTO Person (id, name, role, affiliation)
VALUES ('00000000-0000-0000-0000-000000000306', 'Linda Carter', 'AUTHOR', 'INVITED');

/* Verify author */
SELECT * FROM Person WHERE id = '00000000-0000-0000-0000-000000000306';


/* Before inserting article */
SELECT * FROM Content WHERE id = '00000000-0000-0000-0000-000000000507';

/* Insert article */
INSERT INTO Content
(id, content_title, topic, date_written, content_type, content_text, edition_issue_id)
VALUES
('00000000-0000-0000-0000-000000000507', 'AI in Healthcare', 'Science', '2024-04-02', 'ARTICLE',
'Initial article text for AI in healthcare.', '00000000-0000-0000-0000-000000000407');

/* After insert */
SELECT * FROM Content WHERE id = '00000000-0000-0000-0000-000000000507';


/* Link author to article */
SELECT * FROM Writes WHERE content_id = '00000000-0000-0000-0000-000000000507';

INSERT INTO Writes (person_id, content_id)
VALUES ('00000000-0000-0000-0000-000000000306', '00000000-0000-0000-0000-000000000507');

/* Verify author linkage */
SELECT * FROM Writes WHERE content_id = '00000000-0000-0000-0000-000000000507';



/* --------------------------------------------------
2E2. Create a new author and update content author mapping
-------------------------------------------------- */

/* Before insert: verify new author does not exist */
SELECT * FROM Person WHERE id = '00000000-0000-0000-0000-000000000307';

/* Create new author */
INSERT INTO Person (id, name, role, affiliation)
VALUES ('00000000-0000-0000-0000-000000000307', 'Daniel Moore', 'AUTHOR', 'INVITED');

/* Verify new author */
SELECT * FROM Person WHERE id = '00000000-0000-0000-0000-000000000307';

/* Before update: current content-author mapping */
SELECT w.content_id, w.person_id, p.name AS author_name
FROM Writes w
JOIN Person p ON p.id = w.person_id
WHERE w.content_id = '00000000-0000-0000-0000-000000000507';

/* Update content author (relink content to the new author) */
UPDATE Writes
SET person_id = '00000000-0000-0000-0000-000000000307'
WHERE content_id = '00000000-0000-0000-0000-000000000507'
  AND person_id = '00000000-0000-0000-0000-000000000306';

/* After update: verify updated mapping */
SELECT w.content_id, w.person_id, p.name AS author_name
FROM Writes w
JOIN Person p ON p.id = w.person_id
WHERE w.content_id = '00000000-0000-0000-0000-000000000507';

/* --------------------------------------------------
2F. Update article/chapter metadata
-------------------------------------------------- */

/* Before update */
SELECT id, content_title, topic, date_written
FROM Content
WHERE id = '00000000-0000-0000-0000-000000000507';

/* Update metadata */
UPDATE Content
SET content_title = 'AI in Modern Healthcare',
    topic = 'Science',
    date_written = '2024-04-03'
WHERE id = '00000000-0000-0000-0000-000000000507';

/* After update */
SELECT id, content_title, topic, date_written
FROM Content
WHERE id = '00000000-0000-0000-0000-000000000507';



/* --------------------------------------------------
2G. Update article text
-------------------------------------------------- */

/* Before update */
SELECT id, content_text
FROM Content
WHERE id = '00000000-0000-0000-0000-000000000507';

/* Update text */
UPDATE Content
SET content_text = 'Updated full article text for AI in modern healthcare.'
WHERE id = '00000000-0000-0000-0000-000000000507';

/* After update */
SELECT id, content_text
FROM Content
WHERE id = '00000000-0000-0000-0000-000000000507';



/* --------------------------------------------------
2H. Find books and articles by topic
-------------------------------------------------- */

SELECT 'Book' AS ResultType,
       id AS entity_id,
       title AS name,
       primary_topic AS topic
FROM Publications
WHERE primary_topic = 'Technology'
  AND type = 'BOOK'

UNION

SELECT 'Article' AS ResultType,
       id AS entity_id,
       content_title AS name,
       topic
FROM Content
WHERE topic = 'Technology'
  AND content_type = 'ARTICLE';



/* --------------------------------------------------
2I. Find books and articles by specific date range
-------------------------------------------------- */

SELECT 'Book' AS ResultType,
       pub.id AS entity_id,
       pub.title AS title,
       MIN(ei.pub_date) AS relevant_date,
       pub.id AS parent_id
FROM Publications pub
JOIN EditionIssue ei ON ei.publication_id = pub.id
WHERE pub.type = 'BOOK'
  AND ei.pub_date BETWEEN '2024-02-01' AND '2024-03-31'
GROUP BY pub.id, pub.title

UNION

SELECT 'Article' AS ResultType,
       c.id AS entity_id,
       c.content_title AS title,
       c.date_written AS relevant_date,
       c.edition_issue_id AS parent_id
FROM Content c
WHERE date_written BETWEEN '2024-02-01' AND '2024-03-31'
  AND content_type = 'ARTICLE';



/* --------------------------------------------------
2J. Find books and articles by author name
-------------------------------------------------- */

/* Adding both chapter and book check for extra safety*/
SELECT
    p.name AS author_name,
    pub.title AS publication_title,
    pub.id
FROM Writes w
JOIN Person p ON w.person_id = p.id
JOIN Content c ON c.id = w.content_id
JOIN EditionIssue ei ON ei.id = c.edition_issue_id
JOIN Publications pub ON pub.id = ei.publication_id
WHERE p.name = 'John Smith'
    AND c.content_type = 'CHAPTER'
    AND pub.type = 'BOOK'
GROUP BY p.name, pub.title, pub.id;

SELECT
    p.name AS author_name,
    c.id AS content_id,
    c.content_title AS title,
    c.topic,
    c.date_written
FROM Writes w
JOIN Person p ON p.id = w.person_id
JOIN Content c ON c.id = w.content_id
WHERE p.name = 'John Smith'
    AND c.content_type = 'ARTICLE';


/* --------------------------------------------------
2K. Enter payment for author or editor
-------------------------------------------------- */

/* Before insert */
SELECT * FROM UserPayments WHERE id = '00000000-0000-0000-0000-000000000606';

/* Insert payment */
INSERT INTO UserPayments
(id, payment_type, amount, issue_date, claimed_date, person_id)
VALUES
('00000000-0000-0000-0000-000000000606', 'ARTICLE_AUTHORSHIP', 275.00, '2024-04-05', NULL, '00000000-0000-0000-0000-000000000306');

/* After insert */
SELECT * FROM UserPayments WHERE id = '00000000-0000-0000-0000-000000000606';



/* --------------------------------------------------
2L. Update when payment was claimed
-------------------------------------------------- */

/* Before update */
SELECT id, claimed_date
FROM UserPayments
WHERE id = '00000000-0000-0000-0000-000000000602';

/* Update claim date */
UPDATE UserPayments
SET claimed_date = '2024-04-08'
WHERE id = '00000000-0000-0000-0000-000000000602';

/* After update */
SELECT id, claimed_date
FROM UserPayments
WHERE id = '00000000-0000-0000-0000-000000000602';



/* --------------------------------------------------
2M. List payments issued but not claimed within a date range
-------------------------------------------------- */

SELECT id,
       payment_type,
       amount,
       issue_date,
       claimed_date,
       person_id
FROM UserPayments
WHERE claimed_date IS NULL
AND issue_date BETWEEN '2024-02-01' AND '2024-04-30';



/* --------------------------------------------------
2N. Compare two issues by listing their articles
-------------------------------------------------- */

SELECT
    ei.id AS edition_issue_id,
    pub.title AS publication_title,
    c.id,
    c.content_title AS title,
    c.topic,
    c.date_written
FROM Publications pub 
JOIN EditionIssue ei ON ei.publication_id = pub.id
JOIN Content c ON c.edition_issue_id = ei.id
WHERE ei.id IN ('00000000-0000-0000-0000-000000000407', '00000000-0000-0000-0000-000000000402')
  AND c.content_type = 'ARTICLE'
ORDER BY ei.id, c.id;