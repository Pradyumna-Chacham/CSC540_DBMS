

use asreeku;


CREATE TABLE Publications (
    PublicationID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Title VARCHAR(255) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    Periodicity VARCHAR(50) NULL,
    PrimaryTopic VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    CHECK (Type IN ('Book', 'Journal', 'Magazine')),
    CHECK (Price >= 0)
);

CREATE TABLE Distributors (
    DistributorID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    Address VARCHAR(255) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    PhoneNumber VARCHAR(20) NOT NULL,
    ContactName VARCHAR(255) NOT NULL,
    Balance DECIMAL(12,2) NOT NULL,
    City VARCHAR(100) NOT NULL,
    Country VARCHAR(100) NOT NULL,
    CHECK (Balance >= 0)
);

CREATE TABLE Person (
    PersonID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    Role VARCHAR(20) NOT NULL,
    CHECK (Role IN ('author', 'editor', 'both'))
);

CREATE TABLE EditionIssue (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    EditionNum INT NULL,
    PubDate DATE NOT NULL,
    Status VARCHAR(50) NOT NULL,
    ISBN VARCHAR(20) NULL UNIQUE,
    PublicationID CHAR(36) NOT NULL,
    CHECK (Status IN ('finished', 'published', 'unpublished')),
    FOREIGN KEY (PublicationID) REFERENCES Publications(PublicationID)
);

CREATE TABLE Content (
    ContentID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ContentTitle VARCHAR(255) NOT NULL,
    Topic VARCHAR(100) NOT NULL,
    DateWritten DATE NOT NULL,
    ContentType VARCHAR(50) NOT NULL,
    ContentText TEXT NOT NULL,
    ID CHAR(36) NOT NULL,
    CHECK (ContentType IN ('article', 'chapter')),
    FOREIGN KEY (ID) REFERENCES EditionIssue(ID)
);

CREATE TABLE StaffPayments (
    PaymentID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    PaymentType VARCHAR(100) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    IssueDate DATE NOT NULL,
    ClaimedDate DATE NULL,
    PersonID CHAR(36) NOT NULL,
    CHECK (Amount >= 0),
    CHECK (ClaimedDate IS NULL OR ClaimedDate >= IssueDate),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID)
);

CREATE TABLE Orders (
    OrderID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    ShipCost DECIMAL(10,2) NOT NULL,
    OrderDate DATE NOT NULL,
    RequiredByDate DATE NOT NULL,
    BilledDate DATE NULL,
    TotalBilledAmount DECIMAL(12,2) NULL,
    DistributorID CHAR(36) NOT NULL,
    PublicationID CHAR(36) NOT NULL,
    CHECK (Quantity > 0),
    CHECK (UnitPrice >= 0),
    CHECK (ShipCost >= 0),
    CHECK (TotalBilledAmount IS NULL OR TotalBilledAmount >= 0),
    CHECK (RequiredByDate >= OrderDate),
    CHECK (BilledDate IS NULL OR BilledDate >= OrderDate),
    FOREIGN KEY (DistributorID) REFERENCES Distributors(DistributorID),
    FOREIGN KEY (PublicationID) REFERENCES Publications(PublicationID)
);

CREATE TABLE DistPayment (
    DistPayID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Amount DECIMAL(10,2) NOT NULL,
    PayDate DATE NOT NULL,
    DistributorID CHAR(36) NOT NULL,
    CHECK (Amount >= 0),
    FOREIGN KEY (DistributorID) REFERENCES Distributors(DistributorID)
);

CREATE TABLE Edits (
    PersonID CHAR(36) NOT NULL,
    PublicationID CHAR(36) NOT NULL,
    PRIMARY KEY (PersonID, PublicationID),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID),
    FOREIGN KEY (PublicationID) REFERENCES Publications(PublicationID)
);

CREATE TABLE Writes (
    PersonID CHAR(36) NOT NULL,
    ContentID CHAR(36) NOT NULL,
    PRIMARY KEY (PersonID, ContentID),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID),
    FOREIGN KEY (ContentID) REFERENCES Content(ContentID)
);