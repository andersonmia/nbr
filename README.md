# Banking System Management

This is a Spring Boot application that provides a RESTful API for managing a banking system. It includes features such as customer registration, account management, and transaction processing.

## Features

- **Customer Registration**: Register new customers.
- **Account Management**: Manage customer accounts and balances.
- **Transaction Processing**: Perform saving, withdrawal, and transfer transactions.
- **Messaging**: Send transaction confirmation messages to customers.

## Technologies Used

- **Spring Boot**: Framework for building Java applications.
- **Spring Data JPA**: For database interactions.
- **MariaDB**: Database for storing application data.
- **Swagger**: For API documentation.

## Setup and Installation

1. **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/banking-system.git
    cd banking-system
    ```

2. **Database Configuration**
    - Ensure MariaDB is installed and running.
    - Create a database named `bankingsystem`.
    - Update the database configuration in `application.properties` if necessary:
      ```properties
      spring.datasource.url=jdbc:mariadb://localhost:3306/bankingsystem
      spring.datasource.username=root
      spring.datasource.password=yourpassword
      spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      ```

3. **Run the application**
    ```bash
    ./mvnw spring-boot:run
    ```

4. **Access the API documentation**
    - The Swagger UI is available at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## API Endpoints

### Customer Endpoints

- **Register a new customer**
    - `POST /customers/add`
    - Request Body:
      ```json
      {
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "balance": 1000.0,
        "mobile": "1234567890",
        "dob": "1990-01-01"
      }
      ```

- **Get all customers**
    - `GET /customers/`
    - Response:
      ```json
      [
        {
          "id": 1,
          "firstName": "John",
          "lastName": "Doe",
          "email": "john@example.com",
          "balance": 1000.0,
          "mobile": "1234567890",
          "dob": "1990-01-01",
          "account": "1234567890",
          "lastUpdateTime": "2023-01-01T12:00:00"
        }
      ]
      ```

- **Get customer details by ID**
    - `GET /customers/{id}`
    - Response:
      ```json
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john@example.com",
        "balance": 1000.0,
        "mobile": "1234567890",
        "dob": "1990-01-01",
        "account": "1234567890",
        "lastUpdateTime": "2023-01-01T12:00:00"
      }
      ```

- **Update a customer**
    - `PUT /customers/{id}`
    - Request Body:
      ```json
      {
        "firstName": "Jane",
        "lastName": "Doe",
        "email": "jane@example.com",
        "balance": 2000.0,
        "mobile": "0987654321",
        "dob": "1992-02-02"
      }
      ```

- **Delete a customer**
    - `DELETE /customers/{id}`
    - Response: `204 No Content`

### Transaction Endpoints

- **Add a new transaction**
    - `POST /transactions/add`
    - Request Body:
      ```json
      {
        "customerId": 1,
        "amount": 500.0,
        "type": "SAVING"
      }
      ```

- **Get all transactions**
    - `GET /transactions/`
    - Response:
      ```json
      [
        {
          "id": 1,
          "customer": {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe"
          },
          "account": "1234567890",
          "amount": 500.0,
          "type": "SAVING",
          "bankingDateTime": "2023-01-01T12:00:00"
        }
      ]
      ```

- **Get transaction details by ID**
    - `GET /transactions/{id}`
    - Response:
      ```json
      {
        "id": 1,
        "customer": {
          "id": 1,
          "firstName": "John",
          "lastName": "Doe"
        },
        "account": "1234567890",
        "amount": 500.0,
        "type": "SAVING",
        "bankingDateTime": "2023-01-01T12:00:00"
      }
      ```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a pull request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
