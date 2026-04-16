USE pchacha2;

START TRANSACTION;

-- =========================================================
-- 1. PUBLICATIONS
-- =========================================================
INSERT INTO Publications (id, title, type, periodicity, primary_topic) VALUES
('00000000-0000-0000-0000-000000000101', 'Foundations of Distributed Databases', 'BOOK', NULL, 'Databases'),
('00000000-0000-0000-0000-000000000102', 'Tech Weekly', 'MAGAZINE', 'Weekly', 'Technology'),
('00000000-0000-0000-0000-000000000103', 'Global Affairs Review', 'JOURNAL', 'Monthly', 'News');

-- =========================================================
-- 2. PEOPLE
-- =========================================================
INSERT INTO Person (id, name, role, affiliation) VALUES
('00000000-0000-0000-0000-000000000301', 'Olivia Bennett', 'EDITOR', 'STAFF'),
('00000000-0000-0000-0000-000000000302', 'Helena Strauss', 'EDITOR', 'STAFF'),
('00000000-0000-0000-0000-000000000303', 'Daniel Whitmore', 'EDITOR', 'STAFF'),
('00000000-0000-0000-0000-000000000304', 'Alice Morgan', 'AUTHOR', 'INVITED'),
('00000000-0000-0000-0000-000000000305', 'Sarah Lee', 'AUTHOR', 'INVITED'),
('00000000-0000-0000-0000-000000000306', 'Michael Brown', 'AUTHOR', 'INVITED'),
('00000000-0000-0000-0000-000000000307', 'Emily Zhang', 'AUTHOR', 'INVITED'),
('00000000-0000-0000-0000-000000000308', 'Daniel Kim', 'AUTHOR', 'INVITED'),
('00000000-0000-0000-0000-000000000309', 'Laura Martinez', 'AUTHOR', 'INVITED'),
('00000000-0000-0000-0000-000000000310', 'Robert Singh', 'AUTHOR', 'INVITED');

-- =========================================================
-- 3. EDITOR-PUBLICATION RELATIONSHIPS
-- =========================================================
INSERT INTO Edits (person_id, publication_id) VALUES
('00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000101'),
('00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000102'),
('00000000-0000-0000-0000-000000000302', '00000000-0000-0000-0000-000000000102'),
('00000000-0000-0000-0000-000000000303', '00000000-0000-0000-0000-000000000103');

-- =========================================================
-- 4. EDITIONS / ISSUES
-- =========================================================
INSERT INTO EditionIssue (id, edition_num, pub_date, status, isbn, price, publication_id) VALUES
('00000000-0000-0000-0000-000000000401', 1, '2025-03-15', 'PUBLISHED', '9781458300001', 85.00, '00000000-0000-0000-0000-000000000101'),
('00000000-0000-0000-0000-000000000402', 2, '2026-02-01', 'PUBLISHED', '9781458300002', 85.00, '00000000-0000-0000-0000-000000000101'),
('00000000-0000-0000-0000-000000000403', NULL, '2026-02-01', 'PUBLISHED', NULL, 12.00, '00000000-0000-0000-0000-000000000102'),
('00000000-0000-0000-0000-000000000404', NULL, '2026-02-08', 'PUBLISHED', NULL, 12.00, '00000000-0000-0000-0000-000000000102'),
('00000000-0000-0000-0000-000000000405', NULL, '2026-02-15', 'PUBLISHED', NULL, 12.00, '00000000-0000-0000-0000-000000000102'),
('00000000-0000-0000-0000-000000000406', NULL, '2026-01-01', 'PUBLISHED', NULL, 15.00, '00000000-0000-0000-0000-000000000103'),
('00000000-0000-0000-0000-000000000407', NULL, '2026-02-01', 'PUBLISHED', NULL, 15.00, '00000000-0000-0000-0000-000000000103');

-- =========================================================
-- 5. CONTENT
-- =========================================================
INSERT INTO Content (id, content_title, topic, date_written, content_type, content_text, edition_issue_id) VALUES
('00000000-0000-0000-0000-000000000501', 'Ch1 Distributed Transactions', 'Distributed Systems', '2025-03-15', 'CHAPTER', 'Modern distributed database systems coordinate data across multiple sites, often separated by large geographic distances. Ensuring that a transaction commits consistently at every participating node is one of the central challenges of distributed computing.', '00000000-0000-0000-0000-000000000401'),
('00000000-0000-0000-0000-000000000502', 'Ch1 Distributed Transactions (Edition 2)', 'Distributed Systems', '2026-02-01', 'CHAPTER', 'As distributed infrastructures scale to cloud environments, transaction coordination becomes increasingly complex. Newer consensus-based approaches build upon traditional commit protocols to improve fault tolerance and availability.', '00000000-0000-0000-0000-000000000402'),
('00000000-0000-0000-0000-000000000503', 'Ch2 Data Replication', 'Data Management', '2026-02-01', 'CHAPTER', 'Data replication allows distributed systems to improve reliability and performance by maintaining multiple copies of data. Different replication strategies, however, introduce trade-offs between consistency, latency, and availability.', '00000000-0000-0000-0000-000000000402'),
('00000000-0000-0000-0000-000000000504', 'AI in 2026', 'Artificial Intelligence', '2026-01-20', 'ARTICLE', 'Artificial intelligence systems in 2026 operate at unprecedented scale, integrating multimodal inputs and real-time decision making. Rapid advances in model efficiency have made AI deployment feasible across industries.', '00000000-0000-0000-0000-000000000403'),
('00000000-0000-0000-0000-000000000505', 'Quantum Computing Basics', 'Quantum Computing', '2026-01-25', 'ARTICLE', 'Quantum computing departs fundamentally from classical computation by leveraging superposition and entanglement. Even small quantum systems demonstrate behaviors that have no classical equivalent.', '00000000-0000-0000-0000-000000000404'),
('00000000-0000-0000-0000-000000000506', 'Data Privacy in Practice', 'Cybersecurity', '2026-01-20', 'ARTICLE', 'Organizations increasingly face the challenge of protecting user data while maintaining usability and performance. Regulatory frameworks now require concrete safeguards and transparent data-handling practices.', '00000000-0000-0000-0000-000000000404'),
('00000000-0000-0000-0000-000000000507', 'Edge AI Applications', 'Artificial Intelligence', '2026-02-01', 'ARTICLE', 'Deploying AI models on edge devices reduces latency and enhances privacy by processing data locally. Advances in hardware acceleration have made on-device inference more practical than ever before.', '00000000-0000-0000-0000-000000000405'),
('00000000-0000-0000-0000-000000000508', 'Election Trends Worldwide', 'International Politics', '2025-12-15', 'ARTICLE', 'Recent elections across multiple regions reveal shifting voter priorities and evolving campaign strategies. Digital platforms now play a decisive role in shaping public discourse.', '00000000-0000-0000-0000-000000000406'),
('00000000-0000-0000-0000-000000000509', 'Energy Policy Updates', 'Public Policy', '2026-01-05', 'ARTICLE', 'Global energy markets continue to respond to geopolitical pressures and climate commitments. Policymakers are balancing economic growth with long-term sustainability goals.', '00000000-0000-0000-0000-000000000407');

-- =========================================================
-- 6. AUTHOR-CONTENT RELATIONSHIPS
-- =========================================================
INSERT INTO Writes (person_id, content_id) VALUES
('00000000-0000-0000-0000-000000000304', '00000000-0000-0000-0000-000000000501'),
('00000000-0000-0000-0000-000000000304', '00000000-0000-0000-0000-000000000502'),
('00000000-0000-0000-0000-000000000304', '00000000-0000-0000-0000-000000000503'),
('00000000-0000-0000-0000-000000000305', '00000000-0000-0000-0000-000000000504'),
('00000000-0000-0000-0000-000000000306', '00000000-0000-0000-0000-000000000505'),
('00000000-0000-0000-0000-000000000307', '00000000-0000-0000-0000-000000000506'),
('00000000-0000-0000-0000-000000000308', '00000000-0000-0000-0000-000000000507'),
('00000000-0000-0000-0000-000000000309', '00000000-0000-0000-0000-000000000508'),
('00000000-0000-0000-0000-000000000310', '00000000-0000-0000-0000-000000000509');

-- =========================================================
-- 7. DISTRIBUTORS
-- =========================================================
INSERT INTO Distributors (id, name, address, type, phone_number, contact_name, balance, balance_last_updated, city, country) VALUES
('00000000-0000-0000-0000-000000000201', 'Triangle Books', '142 Hillsborough Street, Raleigh, NC 27603', 'BOOKSTORE', '(919) 555-2101', 'Laura Thompson', 4850.00, '2025-12-31', 'Raleigh', 'USA'),
('00000000-0000-0000-0000-000000000202', 'Eastern Academic Wholesale', '850 Logistics Parkway, Charlotte, NC 28208', 'WHOLESALE', '(704) 555-7782', 'Mark Reynolds', 12300.00, '2025-12-31', 'Charlotte', 'USA'),
('00000000-0000-0000-0000-000000000203', 'Wake County Public Library', '336 Fayetteville Street, Raleigh, NC 27601', 'LIBRARY', '(919) 555-4433', 'Emily Carter', 0.00, '2025-12-31', 'Raleigh', 'USA'),
('00000000-0000-0000-0000-000000000204', 'Capitol City Books', '91 Market Square, Durham, NC 27701', 'BOOKSTORE', '(919) 555-6610', 'Daniel Wright', 2175.50, '2025-12-31', 'Durham', 'USA');

-- =========================================================
-- 8. ORDERS
-- =========================================================
INSERT INTO Orders (id, quantity, unit_price, ship_cost, order_date, required_by_date, billed_date, total_billed_amount, distributor_id, edition_issue_id) VALUES
('00000000-0000-0000-0000-000000000701', 90, 15.00, 160.00, '2026-01-03', '2026-01-17', NULL, NULL, '00000000-0000-0000-0000-000000000204', '00000000-0000-0000-0000-000000000406'),
('00000000-0000-0000-0000-000000000702', 75, 15.00, 140.00, '2026-02-03', '2026-02-17', NULL, NULL, '00000000-0000-0000-0000-000000000204', '00000000-0000-0000-0000-000000000407'),
('00000000-0000-0000-0000-000000000703', 650, 12.00, 750.00, '2026-02-03', '2026-02-17', NULL, NULL, '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000403'),
('00000000-0000-0000-0000-000000000704', 650, 12.00, 750.00, '2026-02-11', '2026-02-25', NULL, NULL, '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000404'),
('00000000-0000-0000-0000-000000000705', 500, 12.00, 600.00, '2026-02-17', '2026-03-03', NULL, NULL, '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000405'),
('00000000-0000-0000-0000-000000000706', 120, 85.00, 350.00, '2026-02-10', '2026-02-24', NULL, NULL, '00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000402'),
('00000000-0000-0000-0000-000000000707', 40, 85.00, 120.00, '2026-02-12', '2026-02-26', NULL, NULL, '00000000-0000-0000-0000-000000000203', '00000000-0000-0000-0000-000000000402');

-- =========================================================
-- 9. USER PAYMENTS
-- =========================================================
INSERT INTO UserPayments (id, payment_type, amount, issue_date, claimed_date, person_id, edition_issue_id) VALUES
('00000000-0000-0000-0000-000000000601', 'BOOK_AUTHORSHIP', 2500.00, '2025-03-20', '2025-04-05', '00000000-0000-0000-0000-000000000304', '00000000-0000-0000-0000-000000000401'),
('00000000-0000-0000-0000-000000000602', 'BOOK_AUTHORSHIP', 1000.00, '2026-02-05', NULL, '00000000-0000-0000-0000-000000000304', '00000000-0000-0000-0000-000000000402'),
('00000000-0000-0000-0000-000000000603', 'BOOK_AUTHORSHIP', 2000.00, '2026-02-10', NULL, '00000000-0000-0000-0000-000000000304', '00000000-0000-0000-0000-000000000402'),
('00000000-0000-0000-0000-000000000604', 'ARTICLE_AUTHORSHIP', 1200.00, '2026-01-28', '2026-02-03', '00000000-0000-0000-0000-000000000305', '00000000-0000-0000-0000-000000000403'),
('00000000-0000-0000-0000-000000000605', 'ARTICLE_AUTHORSHIP', 1100.00, '2026-01-30', NULL, '00000000-0000-0000-0000-000000000306', '00000000-0000-0000-0000-000000000404'),
('00000000-0000-0000-0000-000000000606', 'ARTICLE_AUTHORSHIP', 1150.00, '2026-01-25', '2026-02-02', '00000000-0000-0000-0000-000000000307', '00000000-0000-0000-0000-000000000404'),
('00000000-0000-0000-0000-000000000607', 'ARTICLE_AUTHORSHIP', 1300.00, '2026-02-05', NULL, '00000000-0000-0000-0000-000000000308', '00000000-0000-0000-0000-000000000405'),
('00000000-0000-0000-0000-000000000608', 'ARTICLE_AUTHORSHIP', 1250.00, '2025-12-20', '2026-01-05', '00000000-0000-0000-0000-000000000309', '00000000-0000-0000-0000-000000000406'),
('00000000-0000-0000-0000-000000000609', 'ARTICLE_AUTHORSHIP', 1180.00, '2026-01-10', NULL, '00000000-0000-0000-0000-000000000310', '00000000-0000-0000-0000-000000000407'),
('00000000-0000-0000-0000-000000000610', 'EDITORIAL_WORK', 4000.00, '2025-03-15', '2025-04-10', '00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000401'),
('00000000-0000-0000-0000-000000000611', 'EDITORIAL_WORK', 4000.00, '2026-02-01', '2026-02-10', '00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000402'),
-- Demo payment covers all three February 2026 Tech Weekly issues; schema allows only one edition_issue_id, so the final February 15, 2026 issue is used as the representative reference.
('00000000-0000-0000-0000-000000000612', 'EDITORIAL_WORK', 2500.00, '2026-02-28', NULL, '00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000405'),
-- Demo payment covers all three February 2026 Tech Weekly issues; schema allows only one edition_issue_id, so the final February 15, 2026 issue is used as the representative reference.
('00000000-0000-0000-0000-000000000613', 'EDITORIAL_WORK', 2000.00, '2026-02-28', '2026-03-05', '00000000-0000-0000-0000-000000000302', '00000000-0000-0000-0000-000000000405'),
('00000000-0000-0000-0000-000000000614', 'EDITORIAL_WORK', 3000.00, '2026-01-31', '2026-02-07', '00000000-0000-0000-0000-000000000303', '00000000-0000-0000-0000-000000000406'),
('00000000-0000-0000-0000-000000000615', 'EDITORIAL_WORK', 3000.00, '2026-02-28', NULL, '00000000-0000-0000-0000-000000000303', '00000000-0000-0000-0000-000000000407');

-- =========================================================
-- 10. DISTRIBUTOR PAYMENTS
-- =========================================================
INSERT INTO DistPayment (id, amount, pay_date, distributor_id) VALUES
('00000000-0000-0000-0000-000000000801', 15000.00, '2026-02-20', '00000000-0000-0000-0000-000000000201'),
('00000000-0000-0000-0000-000000000802', 26000.00, '2026-02-15', '00000000-0000-0000-0000-000000000202'),
('00000000-0000-0000-0000-000000000803', 5100.00, '2026-02-20', '00000000-0000-0000-0000-000000000202'),
('00000000-0000-0000-0000-000000000804', 3520.00, '2026-02-18', '00000000-0000-0000-0000-000000000203'),
('00000000-0000-0000-0000-000000000805', 3600.00, '2026-01-20', '00000000-0000-0000-0000-000000000204'),
('00000000-0000-0000-0000-000000000806', 1299.50, '2026-02-10', '00000000-0000-0000-0000-000000000204');

COMMIT;
