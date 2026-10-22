# 🛍️ E-Commerce REST API

A RESTful backend application built with **Java 21, Spring Boot, Spring Security, Spring Data JPA, Hibernate, and PostgreSQL**.

This project provides the backend infrastructure for a full-stack e-commerce application, including user authentication, product management, shopping cart integration, customer addresses, payment card information, and order processing.

The backend is containerized with Docker, deployed on **Render**, and connected to a cloud-hosted **Neon PostgreSQL** database.

## 🌐 Live Application

- **Frontend:** https://e-commerce-project-two-ashen.vercel.app/
- **Backend API:** https://ecommerce-api-7on3.onrender.com
- **Products Endpoint:** https://ecommerce-api-7on3.onrender.com/products

> The backend runs on Render's free instance, so the first request may take longer after a period of inactivity.

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| Security | Spring Security, JWT, BCrypt |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Cloud Database | Neon |
| Deployment | Docker, Render |
| Build Tool | Maven |
| API Architecture | RESTful API |

## ✨ Features

### Authentication and Authorization
- User registration and login
- JWT-based authentication
- Password hashing with BCrypt
- Stateless security configuration
- Protected endpoints for authenticated users

### Product Catalog
- Retrieve available products
- View product details
- Product images and categories
- Product filtering, sorting, and pagination

### Customer Management
- Customer account information
- Address management
- Saved payment card information for demonstration purposes

### Orders
- Shopping cart integration through the frontend
- Order creation
- Order item management
- PostgreSQL persistence

## 🏗️ Architecture

The application follows a layered backend architecture:

**Controller → Service → Repository → PostgreSQL**

- **Controller:** Handles incoming HTTP requests and returns API responses.
- **Service:** Implements business logic.
- **Repository:** Manages database operations using Spring Data JPA.
- **Entity:** Represents relational database models.
- **DTO:** Defines request and response data structures.
- **Security:** Handles JWT validation and authentication.

## 🔗 API Endpoints

The following are examples of the application's API routes:

| Method | Endpoint | Description |
|---|---|---|
| GET | `/products` | Retrieve products |
| GET | `/products/{productId}` | Retrieve product details |
| GET | `/categories` | Retrieve categories |
| GET | `/roles` | Retrieve available roles |
| POST | `/signup` | Register a user |
| POST | `/login` | Authenticate a user |

The product listing endpoint supports optional query parameters:

- `limit`
- `offset`
- `category`
- `filter`
- `sort`

Additional authenticated API operations support customer addresses, saved cards, and orders.

## 🚀 Local Development

### Prerequisites

- Java 21
- Maven
- PostgreSQL

### 1. Clone the repository

```bash
git clone https://github.com/sedaahi/ecommerce-api.git
cd ecommerce-api
```

### 2. Configure environment variables

Set the following environment variables before starting the application:

| Variable | Description |
|---|---|
| `DATABASE_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key used to sign JWT tokens |
| `PORT` | Application port (optional, defaults to 8080) |

Example database URL:

```text
jdbc:postgresql://localhost:5432/ecommerce_db
```

Do not commit passwords, JWT secrets, or production database credentials to the repository.

### 3. Build the application

```bash
mvn clean package -DskipTests
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The application will be available at:

```text
http://localhost:8080
```

## 🐳 Docker Deployment

The repository includes a multi-stage `Dockerfile` that builds the Spring Boot application with Maven and runs it using Java 21.

The backend is deployed on Render using Docker.

Production configuration is managed through environment variables, while PostgreSQL is hosted on Neon.

## 🗄️ Database

The application uses PostgreSQL with Spring Data JPA and Hibernate.

Its main entities include:

- User
- Role
- Product
- ProductImage
- Category
- Store
- Address
- CreditCard
- Order
- OrderItem

Hibernate manages entity mappings and database schema updates during development.

## 🔐 Security

The application uses JWT-based authentication with Spring Security.

Passwords are hashed using BCrypt, and authenticated requests use the following HTTP header:

```text
Authorization: Bearer <JWT_TOKEN>
```

Payment card functionality is intended for educational demonstration only. Do not enter real payment card information.

## 🧪 Testing

The deployed application has been manually tested through its connected React frontend, including:

- Product listing and retrieval
- User registration
- User login
- Shopping and order creation

Automated test coverage is not documented here.

## 🔗 Related Project

**Frontend Repository:** https://github.com/sedaahi/e-commerce-project

The frontend is built with React and communicates with this API.

## 📌 Project Purpose

This project was developed as a full-stack e-commerce portfolio application to practice backend architecture, REST API development, JWT authentication, relational database design, Docker containerization, and cloud deployment.

## 👩‍💻 Developer

**Seda Ahi**

GitHub: https://github.com/sedaahi
