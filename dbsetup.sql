

use proj2;


CREATE TABLE Publications (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Title VARCHAR(255) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    Periodicity VARCHAR(50) NULL,
    PrimaryTopic VARCHAR(100) NOT NULL,
    CHECK (Type IN ('BOOK', 'JOURNAL', 'MAGAZINE')),
    CHECK (
        (Type = 'BOOK' AND Periodicity IS NULL) OR
        (Type IN ('JOURNAL', 'MAGAZINE') AND Periodicity IS NOT NULL)
    )
);

CREATE TABLE Distributors (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    Address VARCHAR(255) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    PhoneNumber VARCHAR(20) NOT NULL,
    ContactName VARCHAR(255) NOT NULL,
    Balance DECIMAL(12,2) NOT NULL,
    City VARCHAR(100) NOT NULL,
    Country VARCHAR(100) NOT NULL,
    CHECK (Type IN ('BOOKSTORE', 'WHOLESALE', 'LIBRARY')),
    CHECK (Balance >= 0)
);

CREATE TABLE Person (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    Role VARCHAR(20) NOT NULL,
    Affiliation VARCHAR(100) NOT NULL,
    CHECK (Role IN ('AUTHOR', 'EDITOR', 'BOTH')),
    CHECK (Affiliation IN ('STAFF', 'INVITED'))
);

CREATE TABLE EditionIssue (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    EditionNum INT NULL,
    PubDate DATE NULL,
    Status VARCHAR(50) NOT NULL,
    ISBN VARCHAR(20) NULL UNIQUE,
    PublicationID CHAR(36) NOT NULL,
    CHECK (Status IN ('IN_PROGRESS', 'FINISHED', 'PUBLISHED', 'UNPUBLISHED')),
    FOREIGN KEY (PublicationID) REFERENCES Publications(ID)
);

CREATE TABLE Content (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ContentTitle VARCHAR(255) NOT NULL,
    Topic VARCHAR(100) NOT NULL,
    DateWritten DATE NOT NULL,
    ContentType VARCHAR(50) NOT NULL,
    ContentText TEXT NOT NULL,
    EditionIssueID CHAR(36) NOT NULL,
    CHECK (ContentType IN ('ARTICLE', 'CHAPTER', 'SECTION')),
    FOREIGN KEY (EditionIssueID) REFERENCES EditionIssue(ID)
);

CREATE TABLE UserPayments (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    PaymentType VARCHAR(100) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    IssueDate DATE NOT NULL,
    ClaimedDate DATE NULL,
    PersonID CHAR(36) NOT NULL,
    CHECK (PaymentType IN ('BOOK_AUTHORSHIP', 'ARTICLE_AUTHORSHIP', 'EDITORIAL_WORK')),
    CHECK (Amount >= 0),
    CHECK (ClaimedDate IS NULL OR ClaimedDate >= IssueDate),
    FOREIGN KEY (PersonID) REFERENCES Person(ID)
);

CREATE TABLE Orders (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    ShipCost DECIMAL(10,2) NOT NULL,
    OrderDate DATE NOT NULL,
    RequiredByDate DATE NOT NULL,
    BilledDate DATE NULL,
    TotalBilledAmount DECIMAL(12,2) NULL,
    DistributorID CHAR(36) NOT NULL,
    EditionIssueID CHAR(36) NOT NULL,
    CHECK (Quantity > 0),
    CHECK (UnitPrice >= 0),
    CHECK (ShipCost >= 0),
    CHECK (TotalBilledAmount IS NULL OR TotalBilledAmount >= 0),
    CHECK (RequiredByDate >= OrderDate),
    CHECK (BilledDate IS NULL OR BilledDate >= OrderDate),
    FOREIGN KEY (DistributorID) REFERENCES Distributors(ID),
    FOREIGN KEY (EditionIssueID) REFERENCES EditionIssue(ID)
);

CREATE TABLE DistPayment (
    ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Amount DECIMAL(10,2) NOT NULL,
    PayDate DATE NOT NULL,
    DistributorID CHAR(36) NOT NULL,
    CHECK (Amount >= 0),
    FOREIGN KEY (DistributorID) REFERENCES Distributors(ID)
);

CREATE TABLE Edits (
    PersonID CHAR(36) NOT NULL,
    PublicationID CHAR(36) NOT NULL,
    PRIMARY KEY (PersonID, PublicationID),
    FOREIGN KEY (PersonID) REFERENCES Person(ID),
    FOREIGN KEY (PublicationID) REFERENCES Publications(ID)
);

CREATE TABLE Writes (
    PersonID CHAR(36) NOT NULL,
    ContentID CHAR(36) NOT NULL,
    PRIMARY KEY (PersonID, ContentID),
    FOREIGN KEY (PersonID) REFERENCES Person(ID),
    FOREIGN KEY (ContentID) REFERENCES Content(ID)
);