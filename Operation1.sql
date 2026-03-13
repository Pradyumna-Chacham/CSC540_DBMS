USE proj2;
/* =========================================================
   1. EDITING AND PUBLISHING
   ========================================================= */

/* --------------------------------------------------
1A. Enter basic information on a new publication
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Publications WHERE id = '00000000-0000-0000-0000-000000000106';

/* Insert new publication */
INSERT INTO Publications (id, title, type, periodicity, primary_topic)
VALUES ('00000000-0000-0000-0000-000000000106', 'Data Engineering Today', 'JOURNAL', 'Monthly', 'Technology');

/* After insert */
SELECT * FROM Publications WHERE id = '00000000-0000-0000-0000-000000000106';

/* --------------------------------------------------
1B. Update information on an existing publication
-------------------------------------------------- */

/* Before update */
SELECT id, primary_topic
FROM Publications
WHERE id = '00000000-0000-0000-0000-000000000101';

/* Update publication */
UPDATE Publications
SET primary_topic = 'Database Systems'
WHERE id = '00000000-0000-0000-0000-000000000101';

/* After update */
SELECT id, primary_topic
FROM Publications
WHERE id = '00000000-0000-0000-0000-000000000101';


/* --------------------------------------------------
1C. Assign editor to a publication
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Edits
WHERE person_id = '00000000-0000-0000-0000-000000000305' AND publication_id = '00000000-0000-0000-0000-000000000101';

/* Insert assignment */
INSERT INTO Edits (person_id, publication_id)
VALUES ('00000000-0000-0000-0000-000000000305', '00000000-0000-0000-0000-000000000101');

/* After insert */
SELECT * FROM Edits
WHERE person_id = '00000000-0000-0000-0000-000000000305' AND publication_id = '00000000-0000-0000-0000-000000000101';


/* --------------------------------------------------
1D. Remove editor from a publication
-------------------------------------------------- */

/* Before delete */
SELECT * FROM Edits
WHERE person_id = '00000000-0000-0000-0000-000000000305' AND publication_id = '00000000-0000-0000-0000-000000000101';

/* Delete assignment */
DELETE FROM Edits
WHERE person_id = '00000000-0000-0000-0000-000000000305'
AND publication_id = '00000000-0000-0000-0000-000000000101';

/* After delete */
SELECT * FROM Edits
WHERE person_id = '00000000-0000-0000-0000-000000000305' AND publication_id = '00000000-0000-0000-0000-000000000101';

/* 1E. View all publications assigned to a specific editor */
SELECT e.person_id, p.id AS publication_id, p.title, p.type, p.periodicity, p.primary_topic
FROM Publications p
JOIN Edits e ON p.id = e.publication_id
WHERE e.person_id = '00000000-0000-0000-0000-000000000302';

/* --------------------------------------------------
1F. Add article/chapter to table of contents
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Content
WHERE id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');

/* Insert content */
/* In application code, check if the corresponding publcation has the same topic as the content*/
INSERT INTO Content (id, content_title, topic, date_written, content_type, content_text, edition_issue_id)
VALUES ('00000000-0000-0000-0000-000000000506', 'Advanced SQL Optimization', 'Technology', '2024-03-25',
'CHAPTER', 'This chapter discusses advanced SQL optimization techniques.', '00000000-0000-0000-0000-000000000401');

/* Insert article example */
/* In application code, check if the corresponding publcation has the same topic as the content*/
INSERT INTO Content (id, content_title, topic, date_written, content_type, content_text, edition_issue_id)
VALUES ('00000000-0000-0000-0000-000000000508', 'Cloud Database Trends', 'Science', '2024-03-26',
'ARTICLE', 'This article highlights recent cloud database trends and tradeoffs.', '00000000-0000-0000-0000-000000000402');

/* After insert */
SELECT * FROM Content
WHERE id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');


/* --------------------------------------------------
Assign author to the newly created article/chapter
-------------------------------------------------- */

/* Before insert */
SELECT * FROM Writes
WHERE content_id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');

/* Link author to the article */
INSERT INTO Writes (person_id, content_id)
VALUES ('00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000506');

/* Link author to the new article example */
INSERT INTO Writes (person_id, content_id)
VALUES ('00000000-0000-0000-0000-000000000303', '00000000-0000-0000-0000-000000000508');

/* After insert */
SELECT * FROM Writes
WHERE content_id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');

/* --------------------------------------------------
1G. Delete article/chapter from table of contents
-------------------------------------------------- */

/* Before delete */
SELECT * FROM Content
WHERE id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');

/* Remove author mapping */
DELETE FROM Writes
WHERE content_id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');

/* Delete content */
DELETE FROM Content
WHERE id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');

/* After delete */
SELECT * FROM Content
WHERE id IN ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000508');