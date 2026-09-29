# Smart Library Borrowing and Tracking System

A desktop library system with a simple Swing GUI, backed by a MySQL database.
It was built as a 3-person group project for BER2053 (Java), May to September 2026, at UCSI University.

## What it does

- Add, update, delete, search and sort books
- Add, update, delete, search and sort members (Student or Staff)
- Borrow and return books, with a 14-day loan period
- View all borrow records and list overdue records
- Input validation and clear error messages

## Tech

- Java (Swing GUI)
- MySQL and SQL through JDBC
- DAO pattern for database access

## My contribution

This was a group project of 3 people. My parts:

- Database connection: connected the Java application to MySQL with JDBC (`DBConnection`)
- Data access layer: `MemberDAO` (7 public methods) and `BookDAO` (8 public methods), using SQL with prepared statements
- Team work: assigned tasks to the team members, merged everyone's code into one working system, then tested and debugged it

## Project structure

```
library/          Java source files (DAO classes, GUI panels, services)
library.sql       Database script: creates the user, tables and sample data
```

## How to run

1. Install MySQL Server and Java (JDK).
2. Run `library.sql` in MySQL to create the `library` database and sample data.
3. Open `library/SharedSpec.java` and set `DB_PASSWORD` to the password you use for the database user in `library.sql` (the file contains the placeholder `your_password`; use the same value in both files).
4. Download the MySQL Connector/J jar from https://dev.mysql.com/downloads/connector/j/ and put it in a `lib` folder.
5. Compile and run (on Windows use `;` instead of `:` in the classpath):

```
javac -cp ".:lib/*" library/*.java
java -cp ".:lib/*" library.MainFrame
```

The sample members and books in `library.sql` are made-up data.
