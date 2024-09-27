package edu.leicester.co2103.part1s2.controller;


import edu.leicester.co2103.part1s2.ErrorMessage;
import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import edu.leicester.co2103.part1s2.domain.Order;
import edu.leicester.co2103.part1s2.repo.AuthorRepository;
import edu.leicester.co2103.part1s2.repo.BookRepository;
import edu.leicester.co2103.part1s2.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderRestController {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    //endpoint #14 List all orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //#endpoint #15 create (POST)
    @PostMapping("/orders")
    public ResponseEntity<?> addOrder(@RequestBody Order order, @RequestParam("ISBN") String ISBN, UriComponentsBuilder ucBuilder) {

        Order savedOrder = orderRepository.findById(order.getId()).orElse(null);

        if (savedOrder != null) {
            return new ResponseEntity<>(new ErrorMessage("This order already exists"), HttpStatus.CONFLICT);
        }
        Order newOrder = orderRepository.save(order);

        Book savedBook = bookRepository.findById(ISBN).orElse(null);
        if (savedBook == null) {
            return new ResponseEntity<>(new ErrorMessage("Book not found"), HttpStatus.NOT_FOUND);
        }

        savedBook.getOrders().add(newOrder);
        bookRepository.save(savedBook);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/orders/{id}").buildAndExpand(order.getId()).toUri());

        return new ResponseEntity<>(newOrder, headers, HttpStatus.CREATED);
    }


    //endpoint #16 retrieve(GET)
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrder(@PathVariable long id) {
        Order savedOrder = orderRepository.findById(id).orElse(null);

        if (savedOrder == null) {
            return new ResponseEntity(new ErrorMessage("This book does not exist"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedOrder, HttpStatus.OK);
    }

    //endpoint #17 update (PUT)
    @PutMapping("/orders/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable long id, @RequestBody Order order) {
        Order savedOrder = orderRepository.findById(id).orElse(null);

        if (savedOrder == null) {
            return new ResponseEntity<>(new ErrorMessage("This order does not exist"), HttpStatus.NOT_FOUND);
        }
        savedOrder.setDatetime(order.getDatetime());
        savedOrder.setCustomerName(order.getCustomerName());
        orderRepository.save(savedOrder);
        return new ResponseEntity<>(savedOrder, HttpStatus.OK);
    }

    //endpoint #18 List all books in an order
    @GetMapping("/orders/{id}/books")
    public ResponseEntity<?> getOrdersBooks(@PathVariable long id) {
        Order savedOrder = orderRepository.findById(id).orElse(null);
        List<Book> ordersBook = bookRepository.findBooksByOrderId(id);

        if (savedOrder == null) {
            return new ResponseEntity(new ErrorMessage("This order does not exist."), HttpStatus.NOT_FOUND);
        }
        if (ordersBook.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(savedOrder, HttpStatus.OK);
    }

    //endpoint #19 add book to existing order (POST /orders/{id}/books)
    @PostMapping("/orders/{id}/books")
    public ResponseEntity<?> addBookToAnOrder(@RequestBody Book book, UriComponentsBuilder ucBuilder, @PathVariable long id) {
        Order savedOrder = orderRepository.findById(id).orElse(null);
        List<Book> ordersBook = bookRepository.findBooksByOrderId(id);

        if (savedOrder == null) {
            return new ResponseEntity<>(new ErrorMessage("This order does not exist."), HttpStatus.NOT_FOUND);
        }
        if (ordersBook != null) {
            ordersBook.add(book);

        }
        orderRepository.save(savedOrder);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/orders/{id}/books").buildAndExpand(id).toUri());
        return new ResponseEntity<>(savedOrder, headers, HttpStatus.OK);
    }


    //endpoint #20 Remove a book from an existing order (DELETE /orders/{id}/books/{ISBN})
    @DeleteMapping("/orders/{id}/books/{ISBN}")
    public ResponseEntity<?> deleteBookFromAnOrder(@PathVariable long id, @PathVariable String ISBN, Order order, Book book) {
        Order savedOrder = orderRepository.findById(id).orElse(null);
        List<Book> ordersBook = bookRepository.findBooksByOrderId(id);

        if (savedOrder == null) {
            return new ResponseEntity<>(new ErrorMessage("This order does not exist"), HttpStatus.NOT_FOUND);
        }
        Book bookToDelete = null;
        for (Book b : ordersBook) {
            if (b.getISBN().equals(ISBN)) {
                bookToDelete = b;
                break;
            }
        }
        if (bookToDelete == null) {
            return new ResponseEntity<>(new ErrorMessage("This book does not exist in this order"), HttpStatus.NOT_FOUND);
        } else {
            ordersBook.remove(bookToDelete);
            bookToDelete.getOrders().remove(savedOrder);
            orderRepository.save(savedOrder);
            bookRepository.save(bookToDelete);
            return new ResponseEntity<>("This book has been deleted from this order", HttpStatus.OK);


        }

    }
}