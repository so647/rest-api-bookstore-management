package edu.leicester.co2103.part1s2.controller;


import edu.leicester.co2103.part1s2.ErrorMessage;
import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import edu.leicester.co2103.part1s2.domain.Order;
import edu.leicester.co2103.part1s2.repo.AuthorRepository;
import edu.leicester.co2103.part1s2.repo.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@RestController
@RequestMapping("/api")
public class BookRestController {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    //endpoint #7 List all books
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.size() == 0) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
    @PostMapping("/books")
    public ResponseEntity<?> addBook(@RequestBody Book book, @RequestParam("id") long id,UriComponentsBuilder ucBuilder) {
        if (bookRepository.existsById(book.getISBN())) {
            return new ResponseEntity<>(new ErrorMessage("The book named " + book.getTitle() + " already exists."), HttpStatus.CONFLICT);
        }
        bookRepository.save(book);
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return new ResponseEntity<>(new ErrorMessage("Author not found"), HttpStatus.NOT_FOUND);
        }
        author.getBooks().add(book);
        authorRepository.save(author);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/book/{ISBN}").buildAndExpand(book.getISBN()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    //endpoint #9 retrieve(GET)
    @GetMapping("/books/{ISBN}")
    public ResponseEntity<?> getBook(@PathVariable String ISBN) {
        Book book = bookRepository.findById(ISBN).orElse(null);
        if (book == null) {
            return new ResponseEntity(new ErrorMessage("This book does not exist"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
    //endpoint #10 update (PUT)
    @PutMapping("/books/{ISBN}")
    public ResponseEntity<?> updateBook(@PathVariable String ISBN, @RequestBody Book book){
        Book savedBook = bookRepository.findById(ISBN).orElse(null);
        if (savedBook == null) {
            return new ResponseEntity<>(new ErrorMessage("This book does not exist"), HttpStatus.NOT_FOUND);
        }
        savedBook.setTitle(book.getTitle());
        savedBook.setPublicationYear(book.getPublicationYear());
        savedBook.setPrice(book.getPrice());

        if (book.getOrders() != null) {
            savedBook.getOrders().clear();
            savedBook.getOrders().addAll(book.getOrders());}
        bookRepository.save(savedBook);
        return new ResponseEntity<>(savedBook, HttpStatus.OK);
    }


    //endpoint #11 delete (DELETE) a specific book
    @DeleteMapping("/books/{ISBN}")
    public ResponseEntity<?> deleteBook(@PathVariable String ISBN) {
        Book savedBook = bookRepository.findById(ISBN).orElse(null);
        if (savedBook == null){
            return new ResponseEntity<>(new ErrorMessage("This book does not exist"), HttpStatus.NOT_FOUND);
        }
        List<Author> authors = authorRepository.findAuthorsByBookISBN(ISBN);
        for (Author author : authors) {
            author.getBooks().remove(savedBook);
            authorRepository.save(author);
        }
        if (savedBook.getOrders() != null) {
            savedBook.getOrders().clear();
            bookRepository.save(savedBook);
        }

        bookRepository.delete(savedBook);
        return new ResponseEntity<>("This book has been deleted", HttpStatus.OK);
    }
    //endpoint #12 List all authors of a book
    @GetMapping("/books/{ISBN}/authors")
    public ResponseEntity<?> getBooksAuthor(@PathVariable String ISBN) {
        Book savedBook = bookRepository.findById(ISBN).orElse(null);

        if (savedBook == null) {
            return new ResponseEntity(new ErrorMessage("This book does not exist"), HttpStatus.NOT_FOUND);
        }
        List<Author> allAuthors=authorRepository.findAuthorsByBookISBN(ISBN);

        if (allAuthors.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(allAuthors, HttpStatus.OK);

    }
    //endpoint #13 List all orders containing a specific book
    @GetMapping("/books/{ISBN}/orders")
    public ResponseEntity<?> getBooksOrders(@PathVariable String ISBN) {
        Book savedBook = bookRepository.findById(ISBN).orElse(null);
        if (savedBook==null || savedBook.getOrders().size()==0) {
            return new ResponseEntity(new ErrorMessage("This book is not in any orders"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedBook.getOrders(), HttpStatus.OK);
    }
}