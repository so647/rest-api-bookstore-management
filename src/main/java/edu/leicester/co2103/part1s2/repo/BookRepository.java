package edu.leicester.co2103.part1s2.repo;

import edu.leicester.co2103.part1s2.domain.Author;
import edu.leicester.co2103.part1s2.domain.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends CrudRepository<Book,String> {
    List<Book> findAll();

    @Query("SELECT DISTINCT b FROM Book b JOIN b.orders o WHERE o.id = :orderId")
    List<Book> findBooksByOrderId(@Param("orderId") long orderId);
}
