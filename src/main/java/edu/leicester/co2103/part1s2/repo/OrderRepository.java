package edu.leicester.co2103.part1s2.repo;

import edu.leicester.co2103.part1s2.domain.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order,Long> {
    List<Order> findAll();

}
