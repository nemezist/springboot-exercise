package com.example.demo.controller;

import com.example.demo.database.Book;
import com.example.demo.database.BorrowingDetail;
import com.example.demo.database.BorrowingHeader;
import com.example.demo.enums.BorrowingStatus;
import com.example.demo.model.ResponseWrapper;
import com.example.demo.model.request.BorrowBookRequest;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BorrowingDetailRepository;
import com.example.demo.repository.BorrowingHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("borrowing")
public class BorrowingController {

    @Autowired
    BorrowingHeaderRepository borrowingHeaderRepository;

    @Autowired
    BorrowingDetailRepository borrowingDetailRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AccountRepository accountRepository;

    @PostMapping("/borrow")
    public ResponseWrapper<String> borrowBooks(@RequestBody BorrowBookRequest borrowBookRequest) {

        for(Integer bookId : borrowBookRequest.getBookIds()){
            if(!bookRepository.existsById(bookId)){
                return ResponseWrapper.<String>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("book not found!")
                        .success(false)
                        .content(null)
                        .build();
            }
        }

        if(!accountRepository.existsById(borrowBookRequest.getAccountId())){
            return ResponseWrapper.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("account not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        List<Book> borrowedBooks = borrowingHeaderRepository.getBorrowedBooks();
        boolean isBookBorrowed = false;

        for (Integer bookId : borrowBookRequest.getBookIds()) {
            if (borrowedBooks.stream()
                    .map(Book::getId)
                    .anyMatch(bookId::equals)) isBookBorrowed = true;
        }

        if (isBookBorrowed) {
            return ResponseWrapper.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("book already borrowed!")
                    .success(false)
                    .content(null)
                    .build();
        }

        long now = Instant.now().getEpochSecond();
        BorrowingHeader insertedHeader =
                borrowingHeaderRepository.save(BorrowingHeader.builder()
                        .accountId(borrowBookRequest.getAccountId())
                        .promisedReturnEpoch(now + borrowBookRequest.getBorrowingDuration() )
                        .borrowingEpoch(now)
                        .borrowingStatus(BorrowingStatus.BORROWING)
                                .build());

        for (int bookId: borrowBookRequest.getBookIds()) {
            borrowingDetailRepository.save(
                    BorrowingDetail.builder()
                            .bookId(bookId)
                            .borrowingId(insertedHeader.getId())
                            .build()
            );
        }

        return ResponseWrapper.<String>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(null)
                .build();
    }

    @PostMapping("/return/{id}")
    public ResponseWrapper<String> returnBooks(@PathVariable int id){

        if(!borrowingHeaderRepository.existsById(id)){
            return ResponseWrapper.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("borrowing Id not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        BorrowingHeader borrowingHeader = borrowingHeaderRepository.findById(id).get();

        if(borrowingHeader.getBorrowingStatus() != BorrowingStatus.BORROWING){
            return ResponseWrapper.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("invalid borrowing Id")
                    .success(false)
                    .content(null)
                    .build();
        }

        long now = Instant.now().getEpochSecond();
        borrowingHeader.setBorrowingStatus(now > borrowingHeader.getPromisedReturnEpoch() ?
                BorrowingStatus.OVERTIME : BorrowingStatus.RETURNED);
        borrowingHeader.setReturnEpoch(now);

        borrowingHeaderRepository.save(borrowingHeader);

        return ResponseWrapper.<String>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(null)
                .build();
    }
}
