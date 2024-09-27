package edu.leicester.co2103.part1s2.repo;

import edu.leicester.co2103.part1s2.domain.Author;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorRepository extends CrudRepository<Author,Long> {
    List<Author> findAll();

    @Query("SELECT a FROM Author a JOIN a.books b WHERE b.ISBN = :isbn")
    List <Author> findAuthorsByBookISBN(@Param("isbn") String isbn);
}
