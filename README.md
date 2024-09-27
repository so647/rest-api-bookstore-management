# RESTful API Bookstore Management

A simple RESTful API that allows for the management of books, authors, and orders in an online bookstore.

### Endpoints

The API includes **20 endpoints** for managing the books, authors and book orders.

### Capabilities

- **Authors**: Create, retrieve, update, and delete authors. Retrieve all books from a specific author.
- **Books**: Create, retrieve, update, delete, and list books. Retrieve all authors of a book. Retrieve all orders containing a specific book.
- **Orders**: Create, retrieve, update and delete an order. Retrieve all orders. Retrieve all books in an order. Add book to an existing order. Delete book from an existing author.


### Testing

For each endpoint, there are **two tests**: one for a successful response and another for an error response.


### Technologies Used

- **Spring MVC**: Backend framework
- **MySQL**: Database management
- **Swagger**: API documentation
- **Postman**: API testing
- **IDE**: IntelliJ


**Postman Collection**: The Postman collection for testing the API is located at: src/main/resources/part1.postman_collection.json
