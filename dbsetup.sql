

use proj2;


CREATE TABLE Publications (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    periodicity VARCHAR(50) NULL,
    primary_topic VARCHAR(100) NOT NULL,
    CHECK (type IN ('BOOK', 'JOURNAL', 'MAGAZINE')),
    CHECK (
        (type = 'BOOK' AND periodicity IS NULL) OR
        (type IN ('JOURNAL', 'MAGAZINE') AND periodicity IS NOT NULL)
    )
);

CREATE TABLE Distributors (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    contact_name VARCHAR(255) NOT NULL,
    balance DECIMAL(12,2) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    CHECK (type IN ('BOOKSTORE', 'WHOLESALE', 'LIBRARY')),
    CHECK (balance >= 0)
);

CREATE TABLE Person (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    affiliation VARCHAR(100) NOT NULL,
    CHECK (role IN ('AUTHOR', 'EDITOR', 'BOTH')),
    CHECK (affiliation IN ('STAFF', 'INVITED'))
);

CREATE TABLE EditionIssue (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    edition_num INT NULL,
    pub_date DATE NULL,
    status VARCHAR(50) NOT NULL,
    isbn VARCHAR(20) NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    publication_id CHAR(36) NOT NULL,
    CHECK (status IN ('IN_PROGRESS', 'FINISHED', 'PUBLISHED', 'UNPUBLISHED')),
    CHECK (price >= 0),
    FOREIGN KEY (publication_id) REFERENCES Publications(id)
);

CREATE TABLE Content (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    content_title VARCHAR(255) NOT NULL,
    topic VARCHAR(100) NOT NULL,
    date_written DATE NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    content_text TEXT NOT NULL,
    edition_issue_id CHAR(36) NOT NULL,
    CHECK (content_type IN ('ARTICLE', 'CHAPTER', 'SECTION')),
    FOREIGN KEY (edition_issue_id) REFERENCES EditionIssue(id)
);

CREATE TABLE UserPayments (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    payment_type VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    issue_date DATE NOT NULL,
    claimed_date DATE NULL,
    person_id CHAR(36) NOT NULL,
    CHECK (payment_type IN ('BOOK_AUTHORSHIP', 'ARTICLE_AUTHORSHIP', 'EDITORIAL_WORK')),
    CHECK (amount >= 0),
    CHECK (claimed_date IS NULL OR claimed_date >= issue_date),
    FOREIGN KEY (person_id) REFERENCES Person(id)
);

CREATE TABLE Orders (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    ship_cost DECIMAL(10,2) NOT NULL,
    order_date DATE NOT NULL,
    required_by_date DATE NOT NULL,
    billed_date DATE NULL,
    total_billed_amount DECIMAL(12,2) NULL,
    distributor_id CHAR(36) NOT NULL,
    edition_issue_id CHAR(36) NOT NULL,
    CHECK (quantity > 0),
    CHECK (unit_price >= 0),
    CHECK (ship_cost >= 0),
    CHECK (total_billed_amount IS NULL OR total_billed_amount >= 0),
    CHECK (required_by_date >= order_date),
    CHECK (billed_date IS NULL OR billed_date >= order_date),
    FOREIGN KEY (distributor_id) REFERENCES Distributors(id),
    FOREIGN KEY (edition_issue_id) REFERENCES EditionIssue(id)
);

CREATE TABLE DistPayment (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    amount DECIMAL(10,2) NOT NULL,
    pay_date DATE NOT NULL,
    distributor_id CHAR(36) NOT NULL,
    CHECK (amount >= 0),
    FOREIGN KEY (distributor_id) REFERENCES Distributors(id)
);

CREATE TABLE Edits (
    person_id CHAR(36) NOT NULL,
    publication_id CHAR(36) NOT NULL,
    PRIMARY KEY (person_id, publication_id),
    FOREIGN KEY (person_id) REFERENCES Person(id),
    FOREIGN KEY (publication_id) REFERENCES Publications(id)
);

CREATE TABLE Writes (
    person_id CHAR(36) NOT NULL,
    content_id CHAR(36) NOT NULL,
    PRIMARY KEY (person_id, content_id),
    FOREIGN KEY (person_id) REFERENCES Person(id),
    FOREIGN KEY (content_id) REFERENCES Content(id)
);