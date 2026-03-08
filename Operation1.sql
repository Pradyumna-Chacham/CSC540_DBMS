USE pchacha2;
/* =========================================================
   1. EDITING AND PUBLISHING
   ========================================================= */

/* --------------------------------------------------
1A. Enter basic information on a new publication
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Publications WHERE PublicationID = 'PUB006';

/* Insert new publication */
INSERT INTO Publications (PublicationID, Title, Type, Periodicity, PrimaryTopic, Price)
VALUES ('PUB006', 'Data Engineering Today', 'Journal', 'Monthly', 'Technology', 20.00);

/* After insert */
SELECT * FROM Publications WHERE PublicationID = 'PUB006';

/* --------------------------------------------------
1B. Update information on an existing publication
-------------------------------------------------- */

/* Before update */
SELECT PublicationID, Price, PrimaryTopic
FROM Publications
WHERE PublicationID = 'PUB001';

/* Update publication */
UPDATE Publications
SET Price = 47.50,
    PrimaryTopic = 'Database Systems'
WHERE PublicationID = 'PUB001';

/* After update */
SELECT PublicationID, Price, PrimaryTopic
FROM Publications
WHERE PublicationID = 'PUB001';


/* --------------------------------------------------
1C. Assign editor to a publication
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Edits
WHERE PersonID = 'PER005' AND PublicationID = 'PUB001';

/* Insert assignment */
INSERT INTO Edits (PersonID, PublicationID)
VALUES ('PER005', 'PUB001');

/* After insert */
SELECT * FROM Edits
WHERE PersonID = 'PER005' AND PublicationID = 'PUB001';


/* --------------------------------------------------
1D. Remove editor from a publication
-------------------------------------------------- */

/* Before delete */
SELECT * FROM Edits
WHERE PersonID = 'PER005' AND PublicationID = 'PUB001';

/* Delete assignment */
DELETE FROM Edits
WHERE PersonID = 'PER005'
AND PublicationID = 'PUB001';

/* After delete */
SELECT * FROM Edits
WHERE PersonID = 'PER005' AND PublicationID = 'PUB001';

/* 1E. View all publications assigned to a specific editor */
SELECT e.PersonID,p.PublicationID, p.Title, p.Type, p.Periodicity, p.PrimaryTopic
FROM Publications p
JOIN Edits e ON p.PublicationID = e.PublicationID
WHERE e.PersonID = 'PER002';

/* --------------------------------------------------
1F. Add article/chapter to table of contents
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Content WHERE ContentID = 'CON006';

/* Insert content */
INSERT INTO Content (ContentID, ContentTitle, Topic, DateWritten, ContentType, ContentText, ID)
VALUES ('CON006', 'Advanced SQL Optimization', 'Technology', '2024-03-25',
'chapter', 'This chapter discusses advanced SQL optimization techniques.', 'EDI001');

/* After insert */
SELECT * FROM Content WHERE ContentID = 'CON006';


/* --------------------------------------------------
Assign author to the newly created article/chapter
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Writes WHERE ContentID = 'CON006';

/* Link author to the article */
INSERT INTO Writes (PersonID, ContentID)
VALUES ('PER001', 'CON006');

/* After insert */
SELECT * FROM Writes WHERE ContentID = 'CON006';

/* --------------------------------------------------
1G. Delete article/chapter from table of contents
-------------------------------------------------- */

/* Before delete */
SELECT * FROM Content WHERE ContentID = 'CON006';

/* Remove author mapping */
DELETE FROM Writes WHERE ContentID = 'CON006';

/* Delete content */
DELETE FROM Content WHERE ContentID = 'CON006';

/* After delete */
SELECT * FROM Content WHERE ContentID = 'CON006';