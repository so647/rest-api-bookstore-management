package edu.leicester.co2103.part1s2.controller;

import edu.leicester.co2103.part1s2.ErrorMessage;
import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import edu.leicester.co2103.part1s2.repo.AuthorRepository;
import edu.leicester.co2103.part1s2.repo.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;



@RestController
@RequestMapping("/api")
public class AuthorRestController {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    //endpoint #1
    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    //#endpoint #2 create (POST)
    @PostMapping("/authors")
    public ResponseEntity<?> addAuthor(@RequestBody Author author,UriComponentsBuilder ucBuilder) {
        Author savedAuthor = authorRepository.findById(author.getId()).orElse(null);
        if (savedAuthor != null) {
            return new ResponseEntity<>(new ErrorMessage("This author already exists"), HttpStatus.CONFLICT);
        }
        Author newAuthor = authorRepository.save(author);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/authors/{id}").buildAndExpand(author.getId()).toUri());

        return new ResponseEntity<>(newAuthor, headers, HttpStatus.CREATED);
    }


    //endpoint #3 retrieve(GET)
    @GetMapping("/authors/{id}")
    public ResponseEntity<?> getAuthor(@PathVariable long id) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return new ResponseEntity(new ErrorMessage("This author does not exist"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    //endpoint #4 update (only PUT)
    @PutMapping("/authors/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable long id, @RequestBody Author author){
        Author savedAuthor = authorRepository.findById(id).orElse(null);
        if (savedAuthor == null) {
            return new ResponseEntity<>(new ErrorMessage("This author does not exist"), HttpStatus.NOT_FOUND);
        }
        savedAuthor.setName(author.getName());
        savedAuthor.setBirthYear(author.getBirthYear());
        savedAuthor.setNationality(author.getNationality());
        if (author.getBooks() != null) {
            savedAuthor.getBooks().clear();
            savedAuthor.getBooks().addAll(author.getBooks());
        }
        authorRepository.save(savedAuthor);
        return new ResponseEntity<>(savedAuthor, HttpStatus.OK);
    }


    //endpoint #5 delete (DELETE)
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable long id, Author author ) {
        Author savedAuthor = authorRepository.findById(id).orElse(null);
        if (savedAuthor == null){
            return new ResponseEntity<>(new ErrorMessage("This author does not exist"), HttpStatus.NOT_FOUND);
        }/*
        for (Book b:savedAuthor.getBooks()) {
            if (b.getAuthors().size()>1) {
                b.getAuthors().remove(author);
            }else{
        authorRepository.delete(savedAuthor);/*}}*/
        return new ResponseEntity<>("This author was deleted", HttpStatus.OK);
    }

    //endpoint #6 List all books written by a specific author
    @GetMapping("/authors/{id}/books")
    public ResponseEntity<?> getAuthorsBooks(@PathVariable long id) {
        Author savedAuthor = authorRepository.findById(id).orElse(null);
        if (savedAuthor.getBooks().size()==0) {
            return new ResponseEntity(new ErrorMessage("This author does not have any books"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(savedAuthor.getBooks(), HttpStatus.OK);
    }


}


