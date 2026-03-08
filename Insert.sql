use pchacha2;

INSERT INTO Publications (PublicationID, Title, Type, Periodicity, PrimaryTopic, Price) VALUES
('PUB001', 'Modern Databases', 'Book', NULL, 'Technology', 45.00),
('PUB002', 'Weekly Science', 'Magazine', 'Weekly', 'Science', 12.50),
('PUB003', 'History Monthly', 'Journal', 'Monthly', 'History', 15.00),
('PUB004', 'AI Fundamentals', 'Book', NULL, 'Technology', 55.00),
('PUB005', 'World Politics Review', 'Journal', 'Monthly', 'Politics', 18.00);


INSERT INTO Distributors (DistributorID, Name, Address, Type, PhoneNumber, ContactName, Balance, City, Country) VALUES
('DST001', 'BookWorld Distributors', '12 Main St', 'Bookstore', '9191111111', 'Alice Green', 1200.00, 'Raleigh', 'USA'),
('DST002', 'City Library Supply', '45 Elm St', 'Library', '9192222222', 'Bob White', 800.00, 'Durham', 'USA'),
('DST003', 'Global Books Hub', '78 Oak Ave', 'Wholesale', '9193333333', 'Carol Black', 2300.00, 'Charlotte', 'USA'),
('DST004', 'Triangle Readers', '90 Pine Rd', 'Bookstore', '9194444444', 'David Brown', 450.00, 'Raleigh', 'USA'),
('DST005', 'Academic Distribution Co', '33 Hill St', 'University', '9195555555', 'Emma Clark', 600.00, 'Chapel Hill', 'USA');

INSERT INTO Person (PersonID, Name, Role) VALUES
('PER001', 'John Smith', 'author'),
('PER002', 'Emily Davis', 'editor'),
('PER003', 'Michael Lee', 'both'),
('PER004', 'Sarah Wilson', 'author'),
('PER005', 'Robert Brown', 'editor');

INSERT INTO EditionIssue (ID, EditionNum, PubDate, Status, ISBN, PublicationID) VALUES
('EDI001', 1, '2024-01-15', 'published', '9781234567890', 'PUB001'),
('EDI002', NULL, '2024-02-01', 'published', NULL, 'PUB002'),
('EDI003', NULL, '2024-02-15', 'finished', NULL, 'PUB003'),
('EDI004', 1, '2024-03-10', 'unpublished', '9780987654321', 'PUB004'),
('EDI005', NULL, '2024-03-20', 'published', NULL, 'PUB005');


INSERT INTO Content (ContentID, ContentTitle, Topic, DateWritten, ContentType, ContentText, ID) VALUES
('CON001', 'Introduction to Databases', 'Technology', '2023-12-20', 'chapter', 'This chapter introduces database systems.', 'EDI001'),
('CON002', 'Recent Science Discoveries', 'Science', '2024-01-28', 'article', 'Overview of recent scientific breakthroughs.', 'EDI002'),
('CON003', 'World War II Overview', 'History', '2024-02-10', 'article', 'Historical analysis of World War II.', 'EDI003'),
('CON004', 'Machine Learning Basics', 'Technology', '2024-03-01', 'chapter', 'Introduction to machine learning concepts.', 'EDI004'),
('CON005', 'Global Elections Analysis', 'Politics', '2024-03-15', 'article', 'Analysis of recent global elections.', 'EDI005');


INSERT INTO StaffPayments (PaymentID, PaymentType, Amount, IssueDate, ClaimedDate, PersonID) VALUES
('SPY001', 'book authorship', 500.00, '2024-01-20', '2024-01-25', 'PER001'),
('SPY002', 'editorial work', 300.00, '2024-02-05', NULL, 'PER002'),
('SPY003', 'article authorship', 250.00, '2024-02-18', '2024-02-20', 'PER003'),
('SPY004', 'book authorship', 450.00, '2024-03-12', NULL, 'PER004'),
('SPY005', 'editorial work', 350.00, '2024-03-18', '2024-03-20', 'PER005');


INSERT INTO Orders (OrderID, Quantity, UnitPrice, ShipCost, OrderDate, RequiredByDate, BilledDate, TotalBilledAmount, DistributorID, PublicationID) VALUES
('ORD001', 100, 45.00, 50.00, '2024-01-18', '2024-01-30', '2024-01-19', 4550.00, 'DST001', 'PUB001'),
('ORD002', 200, 12.50, 75.00, '2024-02-02', '2024-02-10', NULL, NULL, 'DST002', 'PUB002'),
('ORD003', 150, 15.00, 60.00, '2024-02-20', '2024-03-01', '2024-02-21', 2310.00, 'DST003', 'PUB003'),
('ORD004', 80, 55.00, 40.00, '2024-03-15', '2024-03-25', NULL, NULL, 'DST004', 'PUB004'),
('ORD005', 120, 18.00, 45.00, '2024-03-22', '2024-04-01', '2024-03-23', 2205.00, 'DST005', 'PUB005');

INSERT INTO DistPayment (DistPayID, Amount, PayDate, DistributorID) VALUES
('DPY001', 1000.00, '2024-01-25', 'DST001'),
('DPY002', 500.00, '2024-02-12', 'DST002'),
('DPY003', 1200.00, '2024-02-25', 'DST003'),
('DPY004', 300.00, '2024-03-20', 'DST004'),
('DPY005', 450.00, '2024-03-28', 'DST005');


INSERT INTO Edits (PersonID, PublicationID) VALUES
('PER002', 'PUB001'),
('PER002', 'PUB002'),
('PER003', 'PUB003'),
('PER003', 'PUB004'),
('PER005', 'PUB005');

INSERT INTO Writes (PersonID, ContentID) VALUES
('PER001', 'CON001'),
('PER003', 'CON002'),
('PER004', 'CON003'),
('PER001', 'CON004'),
('PER003', 'CON005');


