

use pchacha2;


CREATE TABLE Publications (
    PublicationID VARCHAR(10) PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    Periodicity VARCHAR(50) NULL,
    PrimaryTopic VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    CHECK (PublicationID REGEXP '^PUB[0-9]{3}$'),
    CHECK (Type IN ('Book', 'Journal', 'Magazine')),
    CHECK (Price >= 0)
);

CREATE TABLE Distributors (
    DistributorID VARCHAR(10) PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Address VARCHAR(255) NOT NULL,
    Type VARCHAR(50) NOT NULL,
    PhoneNumber VARCHAR(20) NOT NULL,
    ContactName VARCHAR(255) NOT NULL,
    Balance DECIMAL(12,2) NOT NULL,
    City VARCHAR(100) NOT NULL,
    Country VARCHAR(100) NOT NULL,
    CHECK (DistributorID REGEXP '^DST[0-9]{3}$'),
    CHECK (Balance >= 0)
);

CREATE TABLE Person (
    PersonID VARCHAR(10) PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Role VARCHAR(20) NOT NULL,
    CHECK (PersonID REGEXP '^PER[0-9]{3}$'),
    CHECK (Role IN ('author', 'editor', 'both'))
);

CREATE TABLE EditionIssue (
    ID VARCHAR(10) PRIMARY KEY,
    EditionNum INT NULL,
    PubDate DATE NOT NULL,
    Status VARCHAR(50) NOT NULL,
    ISBN VARCHAR(20) NULL UNIQUE,
    PublicationID VARCHAR(10) NOT NULL,
    CHECK (ID REGEXP '^EDI[0-9]{3}$'),
    CHECK (Status IN ('finished', 'published', 'unpublished')),
    FOREIGN KEY (PublicationID) REFERENCES Publications(PublicationID)
);

CREATE TABLE Content (
    ContentID VARCHAR(10) PRIMARY KEY,
    ContentTitle VARCHAR(255) NOT NULL,
    Topic VARCHAR(100) NOT NULL,
    DateWritten DATE NOT NULL,
    ContentType VARCHAR(50) NOT NULL,
    ContentText TEXT NOT NULL,
    ID VARCHAR(10) NOT NULL,
    CHECK (ContentID REGEXP '^CON[0-9]{3}$'),
    CHECK (ContentType IN ('article', 'chapter')),
    FOREIGN KEY (ID) REFERENCES EditionIssue(ID)
);

CREATE TABLE StaffPayments (
    PaymentID VARCHAR(10) PRIMARY KEY,
    PaymentType VARCHAR(100) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    IssueDate DATE NOT NULL,
    ClaimedDate DATE NULL,
    PersonID VARCHAR(10) NOT NULL,
    CHECK (PaymentID REGEXP '^SPY[0-9]{3}$'),
    CHECK (Amount >= 0),
    CHECK (ClaimedDate IS NULL OR ClaimedDate >= IssueDate),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID)
);

CREATE TABLE Orders (
    OrderID VARCHAR(10) PRIMARY KEY,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    ShipCost DECIMAL(10,2) NOT NULL,
    OrderDate DATE NOT NULL,
    RequiredByDate DATE NOT NULL,
    BilledDate DATE NULL,
    TotalBilledAmount DECIMAL(12,2) NULL,
    DistributorID VARCHAR(10) NOT NULL,
    PublicationID VARCHAR(10) NOT NULL,
    CHECK (OrderID REGEXP '^ORD[0-9]{3}$'),
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
    DistPayID VARCHAR(10) PRIMARY KEY,
    Amount DECIMAL(10,2) NOT NULL,
    PayDate DATE NOT NULL,
    DistributorID VARCHAR(10) NOT NULL,
    CHECK (DistPayID REGEXP '^DPY[0-9]{3}$'),
    CHECK (Amount >= 0),
    FOREIGN KEY (DistributorID) REFERENCES Distributors(DistributorID)
);

CREATE TABLE Edits (
    PersonID VARCHAR(10) NOT NULL,
    PublicationID VARCHAR(10) NOT NULL,
    PRIMARY KEY (PersonID, PublicationID),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID),
    FOREIGN KEY (PublicationID) REFERENCES Publications(PublicationID)
);

CREATE TABLE Writes (
    PersonID VARCHAR(10) NOT NULL,
    ContentID VARCHAR(10) NOT NULL,
    PRIMARY KEY (PersonID, ContentID),
    FOREIGN KEY (PersonID) REFERENCES Person(PersonID),
    FOREIGN KEY (ContentID) REFERENCES Content(ContentID)
);