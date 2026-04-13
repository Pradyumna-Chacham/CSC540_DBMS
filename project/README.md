# 📘 GutenbergPubs Management System



# ⚙️ Setup Instructions

## 1️⃣ Database Setup

Before running the application, initialize the database.

### Step 1: Run SQL files (in order)

1. `dbsetup.sql` → Creates tables and constraints  
2. `Insert New.sql` → Inserts initial sample data  

#### Using MySQL/MariaDB CLI:
```sql
SOURCE path/to/dbsetup.sql;
SOURCE path/to/Insert New.sql;
````

---

## 2️⃣ Add JDBC Driver


Place the `.jar` file in the project folder:

```
Project Folder/
├── Main.java
├── DBConnection.java
├── Operation1.java
├── Operation2.java
├── Operation3.java
├── Operation4.java
├── mariadb-java-client-2.3.0.jar
```

---

## 3️⃣ Configure Database Connection

Open `DBConnection.java` and update:

```java
String url = "jdbc:mariadb://localhost:3306/your_database_name";
String user = "your_username";
String password = "your_password";
```

Make sure:

* Database is running
* Credentials are correct

---

# 🛠️ Compile and Run

## Compile

### Mac/Linux:

```bash
javac -cp ".:mariadb-java-client-3.x.x.jar" *.java
```

### Windows:

```bash
javac -cp ".;mariadb-java-client-3.x.x.jar" *.java
```

---

## Run

### Mac/Linux:

```bash
java -cp ".:mariadb-java-client-2.3.0.jar" Main
```


# ▶️ How to Use the Application

After running, you will see:

```
1. Editing and Publishing
2. Production of Editions/Issues
3. Distribution
4. Reports
5. Exit
```

Modules:

* **Operation1** → Publications & editors
* **Operation2** → Editions, content, payments
* **Operation3** → Distributors & orders
* **Operation4** → Reports


Common Issues
❌ Database connection failed
Check DB is running
Verify username/password
Verify JDBC URL
❌ "No suitable driver found"
Ensure .jar is included in compile AND run commands
❌ Tables not found
Run dbsetup.sql
Use correct database
❌ Empty reports
Insert data before running reports




# 🚀 Run the Application

```bash
java -cp ".:mariadb-java-client-2.3.0.jar" Main
```

