# CCRM



This guide will help you set up the environment to run the Main.java application.
1. Install MySQL Database

If you don't have MySQL installed, download and install it from the official website:
https://dev.mysql.com/downloads/installer/

During the installation, you will be asked to set a password for the root user. Remember this password.
2. Download MySQL JDBC Driver

You need the MySQL Connector/J driver to allow the Java application to communicate with your database.

    Go to the download page: https://dev.mysql.com/downloads/connector/j/

    Select "Platform Independent" as the operating system.

    Download the ZIP or TAR archive.

    Extract the archive, and you will find a .jar file (e.g., mysql-connector-j-8.x.x.jar). Keep this file handy.

3. Create the Database and Table

Next, you need to create the database and the users table that the application will use.

    Open the MySQL Command Line Client or any SQL tool like MySQL Workbench.

    Log in as the root user with the password you set.

    Run the following SQL commands one by one:

-- Create a new database named 'companydb'
CREATE DATABASE companydb;

-- Switch to the new database
USE companydb;

-- Create the 'users' table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- (Optional) Verify that the table was created
DESCRIBE users;

4. Configure the Java Code

Open the Main.java file and update the database connection details at the top of the class to match your setup:

private static final String DB_URL = "jdbc:mysql://localhost:3306/companydb";
private static final String USER = "root";
private static final String PASS = "password"; // <-- CHANGE THIS to your actual root password

5. Compile and Run the Application

You can now compile and run the code from your terminal.

    Place the mysql-connector-j-x.x.x.jar file in the same directory as your JdbcCrudExample.java file.

    Open a terminal or command prompt in that directory.

    Compile the Java file:

        On Windows:

        javac -cp ".;mysql-connector-j-8.x.x.jar" Main.java

        On macOS/Linux:

        javac -cp ".:mysql-connector-j-8.x.x.jar" Main.java

    (Remember to replace mysql-connector-j-8.x.x.jar with the actual name of your JAR file.)

    Run the compiled code:

        On Windows:

        java -cp ".;mysql-connector-j-8.x.x.jar" Main.java

        On macOS/Linux:

        java -cp ".:mysql-connector-j-8.x.x.jar" Main.java

You should see the output in your console demonstrating the creation, reading, updating, and deletion of user records.

Similarly, if you want to use the GUI version of code it is saved as "GUI.java".

Compile it:

        On Windows:

        javac -cp ".;mysql-connector-j-8.x.x.jar" Main.java

        On macOS/Linux:

        javac -cp ".:mysql-connector-j-8.x.x.jar" Main.java

Run it:

        On Windows:

        java -cp ".;mysql-connector-j-8.x.x.jar" Main.java

        On macOS/Linux:

        java -cp ".:mysql-connector-j-8.x.x.jar" Main.java


#Campus Course & Records Manager (CCRM)
1. Project Overview
CCRM is a console-based Java application for managing students, courses, and enrollments at an academic institution. It is built as a single-file project to demonstrate a wide range of Java SE features, from core OOP principles to advanced APIs like NIO.2 and Streams, within a self-contained structure.

How to Run:

Ensure you have JDK 11 or higher installed and configured.

Save the code as CCRMProject.java.

Compile the file:
javac CCRMProject.java

Run the main class:
java CCRMProject

Create a test-data directory in the same location as CCRMProject.java and place courses.csv inside it for the import functionality to work.

2. Java Architecture & Platform
Evolution of Java
1995: Java 1.0 released by Sun Microsystems.

2004: Java 5 (1.5) released, adding Generics, Enums, Annotations.

2014: Java 8 released, introducing Lambdas, Streams API, new Date/Time API.

2018: Java 11 released as the second Long-Term Support (LTS) version.

JDK vs JRE vs JVM
JVM (Java Virtual Machine): An abstract machine that provides the runtime environment in which Java bytecode can be executed. It interprets the compiled .class files.

JRE (Java Runtime Environment): A software package that contains the JVM, class libraries, and other files necessary to run Java applications.

JDK (Java Development Kit): A superset of the JRE. It contains everything in the JRE, plus development tools like the compiler (javac) and debugger (jdb) needed to create Java applications.

Java ME vs SE vs EE
Feature

Java ME (Micro Edition)

Java SE (Standard Edition)

Java EE (Enterprise Edition)

Target

Resource-constrained devices

Desktop, servers, general-purpose

Large-scale enterprise systems

Scope

Subset of SE APIs, smaller footprint

Core Java language and APIs

Superset of SE, adds enterprise APIs

Example APIs

javax.microedition.*

java.lang, java.util, java.io, NIO.2

Servlets, JPA, EJB, JMS

3. Syllabus Topic Mapping
Syllabus Topic

Class/Method Where Demonstrated

OOP - Abstraction

Person abstract class

OOP - Inheritance

Student class extends Person

OOP - Polymorphism

TranscriptService.printTranscript calls toString() on Enrollment

OOP - Encapsulation

All domain classes use private fields with public getters/setters.

Design Pattern - Singleton

AppConfig class with getInstance() method

Design Pattern - Builder

Course.Builder static nested class

Enums with Constructors

Grade enum with grade points

Recursion

RecursiveFileUtils.calculateDirectorySize(Path)

Streams API & Lambdas

Used extensively in services for filtering, mapping, and finding data.

NIO.2 File I/O

FileService class (uses Path, Files, Stream)

Date/Time API

Enrollment class and FileService for timestamps.

Custom Exceptions

DuplicateEnrollmentException, MaxCreditLimitExceededException

Nested Classes

Course.Builder and the entire project's single-file structure

switch, loops, break

The main method's CLI loop in CCRMProject

Assertions

Course.Builder.build() method includes assertions for validation.

4. Notes
Enabling Assertions: To run the application with assertions enabled (which validates course creation logic), use the -ea flag: java -ea CCRMProject.

File Paths: The application will create app-data and backups directories in the location where it is run. The data import expects a path relative to the runtime location (e.g., test-data/courses.csv).
