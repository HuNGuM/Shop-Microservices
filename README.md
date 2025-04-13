
# Shop-Microservices

## Description

Shop-Microservices is a microservices-based architecture for an e-commerce platform. This project includes multiple services such as Product Service, User Service, Cart Service, Authentication Service, and API Gateway. The application is designed with Spring Boot and Docker, providing a scalable and flexible system for handling e-commerce operations.

### Tech Stack:
- **Backend**: Spring Boot
- **Microservices**: Each microservice is built independently with its own responsibilities.
- **Database**: MongoDB for storing product and user data.
- **Messaging**: Kafka for communication between microservices.
- **Authentication**: JWT-based authentication with OAuth2.
- **Frontend**: Angular for the user interface (not included in this repository).
- **API Gateway**: Spring Cloud Gateway for routing.

## Prerequisites

- Docker
- Docker Compose
- Java 21 (for local builds, if not using Docker images)
- Maven (for building Java-based microservices)

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/HuNGuM/Shop-Microservices.git
git clone https://github.com/HuNGuM/Shop-frontend.git
cd Shop-Microservices
```

### 2. Build the project
For local builds, you can build each microservice using Maven.
```bash
cd user-service
mvn clean install
cd ../product-service
mvn clean install
# Repeat for other services: cart-service, auth-service, etc.
```

### 3. Docker Setup

Ensure that Docker is installed and running. You can build and run all services with Docker Compose.
```bash
docker-compose up --build
```

This will automatically build and start all the microservices, the database, and messaging components.

### 4. Accessing the services
- The API Gateway is exposed on `localhost:8080`.
- Other services are accessible through the API Gateway, with routes configured in the gateway.

## API Endpoints

- **Product Service**:
  - `GET /api/products` - Fetch all products.
  - `GET /api/products/{id}` - Fetch a product by its ID.
  
- **User Service**:
  - `POST /api/users/register` - Register a new user.
  - `POST /api/users/login` - Authenticate user and return a JWT.

- **Cart Service**:
  - `POST /api/cart/add` - Add an item to the cart.
  - `GET /api/cart` - View the user's cart.

- **Auth Service**:
  - `POST /api/auth/login` - Login to authenticate and get a JWT token.

## Troubleshooting

- If you face any issues with Docker Compose, ensure that Docker is running and no ports are being used by other services.
- To check the logs of any service, you can use the following command:
```bash
docker logs <container-name>
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
