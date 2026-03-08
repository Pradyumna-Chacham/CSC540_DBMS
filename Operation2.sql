USE CSC540;


/* =========================================================
   2. PRODUCTION OF A BOOK EDITION OR ISSUE
   ========================================================= */


/* --------------------------------------------------
2A. Enter new book edition
-------------------------------------------------- */

/* Before insert */
SELECT * FROM EditionIssue WHERE ID = 'EDI006';

/* Insert new book edition */
INSERT INTO EditionIssue (ID, EditionNum, PubDate, Status, ISBN, PublicationID)
VALUES ('EDI006', 2, '2024-04-01', 'published', '9781111111111', 'PUB001');

/* After insert */
SELECT * FROM EditionIssue WHERE ID = 'EDI006';



/* --------------------------------------------------
2B. Enter a new issue of a periodic publication
-------------------------------------------------- */

/* Before insert */
SELECT * FROM EditionIssue WHERE ID = 'EDI007';

/* Insert issue */
INSERT INTO EditionIssue (ID, EditionNum, PubDate, Status, ISBN, PublicationID)
VALUES ('EDI007', NULL, '2024-04-05', 'published', NULL, 'PUB002');

/* After insert */
SELECT * FROM EditionIssue WHERE ID = 'EDI007';



/* --------------------------------------------------
2C. Update book edition / publication issue
-------------------------------------------------- */

/* Before update */
SELECT ID, Status
FROM EditionIssue
WHERE ID = 'EDI004';

/* Update edition status */
UPDATE EditionIssue
SET Status = 'published'
WHERE ID = 'EDI004';

/* After update */
SELECT ID, Status
FROM EditionIssue
WHERE ID = 'EDI004';



/* --------------------------------------------------
2D. Delete a book edition or publication issue
-------------------------------------------------- */

/* Insert temporary issue for deletion test */
INSERT INTO EditionIssue (ID, EditionNum, PubDate, Status, ISBN, PublicationID)
VALUES ('EDI008', NULL, '2024-04-10', 'finished', NULL, 'PUB003');

/* Before delete */
SELECT * FROM EditionIssue WHERE ID = 'EDI008';

/* Delete issue */
DELETE FROM EditionIssue
WHERE ID = 'EDI008';

/* After delete */
SELECT * FROM EditionIssue WHERE ID = 'EDI008';



/* --------------------------------------------------
2E. Enter a new article/chapter
-------------------------------------------------- */

/* Ensure author exists */
SELECT * FROM Person WHERE PersonID = 'PER006';

INSERT INTO Person (PersonID, Name, Role)
VALUES ('PER006', 'Linda Carter', 'author');

/* Verify author */
SELECT * FROM Person WHERE PersonID = 'PER006';


/* Before inserting article */
SELECT * FROM Content WHERE ContentID = 'CON007';

/* Insert article */
INSERT INTO Content
(ContentID, ContentTitle, Topic, DateWritten, ContentType, ContentText, ID)
VALUES
('CON007', 'AI in Healthcare', 'Technology', '2024-04-02', 'article',
'Initial article text for AI in healthcare.', 'EDI007');

/* After insert */
SELECT * FROM Content WHERE ContentID = 'CON007';


/* Link author to article */
SELECT * FROM Writes WHERE ContentID = 'CON007';

INSERT INTO Writes (PersonID, ContentID)
VALUES ('PER006', 'CON007');

/* Verify author linkage */
SELECT * FROM Writes WHERE ContentID = 'CON007';



/* --------------------------------------------------
2F. Update article/chapter metadata
-------------------------------------------------- */

/* Before update */
SELECT ContentID, ContentTitle, Topic, DateWritten
FROM Content
WHERE ContentID = 'CON007';

/* Update metadata */
UPDATE Content
SET ContentTitle = 'AI in Modern Healthcare',
    Topic = 'Health Technology',
    DateWritten = '2024-04-03'
WHERE ContentID = 'CON007';

/* After update */
SELECT ContentID, ContentTitle, Topic, DateWritten
FROM Content
WHERE ContentID = 'CON007';



/* --------------------------------------------------
2G. Update article text
-------------------------------------------------- */

/* Before update */
SELECT ContentID, ContentText
FROM Content
WHERE ContentID = 'CON007';

/* Update text */
UPDATE Content
SET ContentText = 'Updated full article text for AI in modern healthcare.'
WHERE ContentID = 'CON007';

/* After update */
SELECT ContentID, ContentText
FROM Content
WHERE ContentID = 'CON007';



/* --------------------------------------------------
2H. Find books and articles by topic
-------------------------------------------------- */

SELECT 'Publication' AS ResultType,
       PublicationID AS EntityID,
       Title AS Name,
       PrimaryTopic AS Topic
FROM Publications
WHERE PrimaryTopic = 'Technology'

UNION

SELECT 'Content' AS ResultType,
       ContentID AS EntityID,
       ContentTitle AS Name,
       Topic
FROM Content
WHERE Topic = 'Technology';



/* --------------------------------------------------
2I. Find books and articles by specific date range
-------------------------------------------------- */

SELECT 'EditionIssue' AS ResultType,
       ID AS EntityID,
       PubDate AS RelevantDate,
       PublicationID
FROM EditionIssue
WHERE PubDate BETWEEN '2024-02-01' AND '2024-03-31'

UNION

SELECT 'Content' AS ResultType,
       ContentID AS EntityID,
       DateWritten AS RelevantDate,
       ID
FROM Content
WHERE DateWritten BETWEEN '2024-02-01' AND '2024-03-31';



/* --------------------------------------------------
2J. Find books and articles by author name
-------------------------------------------------- */

SELECT
    p.Name AS AuthorName,
    c.ContentID,
    c.ContentTitle,
    c.Topic,
    c.DateWritten
FROM Person p
JOIN Writes w ON p.PersonID = w.PersonID
JOIN Content c ON w.ContentID = c.ContentID
WHERE p.Name = 'John Smith';



/* --------------------------------------------------
2K. Enter payment for author or editor
-------------------------------------------------- */

/* Before insert */
SELECT * FROM StaffPayments WHERE PaymentID = 'SPY006';

/* Insert payment */
INSERT INTO StaffPayments
(PaymentID, PaymentType, Amount, IssueDate, ClaimedDate, PersonID)
VALUES
('SPY006', 'article authorship', 275.00, '2024-04-05', NULL, 'PER006');

/* After insert */
SELECT * FROM StaffPayments WHERE PaymentID = 'SPY006';



/* --------------------------------------------------
2L. Update when payment was claimed
-------------------------------------------------- */

/* Before update */
SELECT PaymentID, ClaimedDate
FROM StaffPayments
WHERE PaymentID = 'SPY002';

/* Update claim date */
UPDATE StaffPayments
SET ClaimedDate = '2024-04-08'
WHERE PaymentID = 'SPY002';

/* After update */
SELECT PaymentID, ClaimedDate
FROM StaffPayments
WHERE PaymentID = 'SPY002';



/* --------------------------------------------------
2M. List payments issued but not claimed within a date range
-------------------------------------------------- */

SELECT PaymentID,
       PaymentType,
       Amount,
       IssueDate,
       ClaimedDate,
       PersonID
FROM StaffPayments
WHERE ClaimedDate IS NULL
AND IssueDate BETWEEN '2024-02-01' AND '2024-04-30';



/* --------------------------------------------------
2N. Compare two issues by listing their articles
-------------------------------------------------- */

SELECT
    ei.ID AS EditionIssueID,
    pub.Title AS PublicationTitle,
    c.ContentID,
    c.ContentTitle,
    c.Topic,
    c.DateWritten
FROM EditionIssue ei
JOIN Publications pub
ON ei.PublicationID = pub.PublicationID
JOIN Content c
ON ei.ID = c.ID
WHERE ei.ID IN ('EDI002', 'EDI003')
ORDER BY ei.ID, c.ContentID;