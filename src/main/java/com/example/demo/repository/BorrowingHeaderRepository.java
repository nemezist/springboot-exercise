package com.example.demo.repository;

import com.example.demo.database.Book;
import com.example.demo.database.BorrowingHeader;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BorrowingHeaderRepository extends CrudRepository<BorrowingHeader, Integer> {

    @Query(value = "SELECT b FROM Book b WHERE b.id IN (SELECT bookId FROM BorrowingDetail WHERE borrowingId IN " +
            "(SELECT id FROM BorrowingHeader WHERE borrowingStatus = com.example.demo.enums.BorrowingStatus.BORROWING))")
    List<Book> getBorrowedBooks();
}
