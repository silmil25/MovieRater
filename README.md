# Spring boot Project MovieRater

## Overview

A Spring Boot REST API for managing users and a curated movie catalog. Users can register, browse movies, and have their roles updated or accounts deleted by admins. Admins can create, update, and delete movies, with movie ratings automatically fetched and updated asynchronously from the external OMDb API.
---

## Features

- User Authentication: Sign-up and authentication handling
- Find movies: Browse all movies, search by title keyword, or by movie ID
- Content Moderation: Admin controls for adding, updating and deleting movies
- Asynchronous enrichment: Movie ratings are fetched from an external API upon movie creation or update
- Swagger Integration: Auto-generated, interactive API documentation

---

## Technologies Used

- Java 17 – Primary programming language
- Spring Boot – Backend framework for building the application and REST endpoints
- Spring Security – Provides authentication and authorization for securing REST endpoints. Supports role-based access control and method-level security.
- BCrypt – Password hashing algorithm used with Spring Security to securely store user passwords with salted hashing.
- Hibernate ORM – JPA implementation used for object–relational mapping
- MariaDB – Relational database for storing movies and users
- springdoc-openapi / Swagger UI – Generates interactive API documentation
- Gradle – Build automation and dependency management tool 
- JUnit and Mockito - Used for unit testing and mocking dependencies to ensure code reliability.

---

## Installation

### Prerequisites

Before getting started, make sure you have the following installed:

- Java 17
- Gradle
- MariaDB
- Git

Follow these steps to set up and run the application:

1. Clone the repository

https://github.com/silmil25/MovieRater

2. Create the MariaDB database

Start MariaDB locally.

Create a new database and run the provided SQL scripts from db folder (schema + seed data):

3. Configure database connection

Update application.properties with your local DB credentials:

- spring.datasource.url=jdbc:mariadb://localhost:3306/movierater
- spring.datasource.username=your_username
- spring.datasource.password=your_password

4. Build the project

5.Run the application

Once the app is running, open: 👉 http://localhost:8080/swagger-ui.html

Or use Postman or similar to send API requests to localhost:8080

---

## Database Diagram

![Database](readme-images/database.png)

---

## Solution Structure

MovieRater <br>

├─ src/ <br>
│  ├─ main/<br>
│  │  ├─ java/<br>
│  │  │  └─ com/web/movierater/      <br>
│  │  │      ├─ config/                 <br>
│  │  │      ├─ controllers/            <br>
│  │  │      ├─ exceptions/             <br>
│  │  │      ├─ helpers/                <br>
│  │  │      ├─ models/                 <br>
│  │  │      └─ │ dtos/                 <br>
│  │  │      ├─ repositories/           <br>
│  │  │      ├─ services/               <br>
│  │  │      ├─ security/               <br>
│  │  │      └─ MovieRaterApplication.java<br>
│  │  └─ resources/<br>
│  │      └─  application.properties <br>
│  └─ test/<br>
│      └─ java/<br>
│          └─ com/web/movierater/<br>
│              └─ ...                   <br>
├─ db/                                  <br>
├─ build.gradle                         <br>
├─ gradlew / gradlew.bat                <br>
├─ README.md                            <br>
└─ settings.gradle                      <br>

---

## External Rating API

This application integrates with OMDb API (Open Movie Database) to fetch movie ratings.

- When a movie is created or updated, the application asynchronously fetches its rating from OMDb using exact title.
- The rating data is not immediately available during movie creation; it is updated in the background once the API call completes.

## Authentication & Authorization

Authentication and authorization are implemented using Spring Security:

- Registration: Users can register and access resources with a username and password. Passwords are securely hashed using BCrypt.
- Roles: Two roles are defined:
- - USER – Can browse movies.
- - ADMIN – Can create, update, and delete movies, and manage users.

Access Control: Method-level security ensures that only authorized roles can perform certain actions. For example:
- Only admins can create, delete or update movies and all users.
- Users can view movies but cannot modify them, can only view or delete their own account.


## Asynchronous Enrichment

- Movie ratings are fetched asynchronously to avoid blocking the main application flow:
- The application uses Spring’s @Async annotation along with a configured thread pool.
- WebClient is used to call the OMDb API.
- When a movie is created or updated, a background task is triggered to fetch the latest rating.
- This ensures that movie creation and update endpoints return quickly without waiting for external API responses.


## Architectural Decisions & Trade-offs


- Thread pool configuration: A fixed-size thread pool prevents excessive resource usage but limits the number of concurrent external API calls.
- Spring Security: Chose method-level security for fine-grained access control, balancing simplicity and flexibility.

---

## Contributors

| Authors           | Emails                | GitHub          |
|-------------------|-----------------------|-----------------|
| Silvia Mileva     | silmileva@gmail.com   | silmil25        |

---
