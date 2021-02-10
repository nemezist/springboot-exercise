package com.example.demo;

import com.example.demo.controller.BorrowingController;
import com.example.demo.database.Account;
import com.example.demo.database.Book;
import com.example.demo.database.BorrowingHeader;
import com.example.demo.enums.BorrowingStatus;
import com.example.demo.model.ResponseWrapper;
import com.example.demo.model.request.BorrowBookRequest;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.BorrowingDetailRepository;
import com.example.demo.repository.BorrowingHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class BorrowingControllerTests {

    @Autowired
    BorrowingController borrowingController;

    @MockBean
    BorrowingHeaderRepository borrowingHeaderRepository;

    @MockBean
    BorrowingDetailRepository borrowingDetailRepository;


    BorrowingHeader borrowingHeaderOnBorrowingState,
            borrowingHeaderNotExist,
            borrowingHeaderOnReturnedState,
            borrowingHeaderOnOvertimeState;

    Book bookAvailable,
            bookNotExist,
            bookBorrowed;

    Account accountAvailable,
            accountNotExist;

    @MockBean
    BookRepository bookRepository;

    @MockBean
    AccountRepository accountRepository;


    @BeforeEach
    public void prepareMock(){
        borrowingHeaderNotExist = BorrowingHeader.builder()
                .id(0)
                .accountId(1)
                .borrowingStatus(BorrowingStatus.BORROWING)
                .borrowingEpoch(1000)
                .promisedReturnEpoch(1500)
                .build();

        doReturn(false).when(borrowingHeaderRepository)
                .existsById(borrowingHeaderNotExist.getId());
        doReturn(Optional.empty()).when(borrowingHeaderRepository)
                .findById(borrowingHeaderNotExist.getId());


        borrowingHeaderOnBorrowingState = BorrowingHeader.builder()
                .id(1)
                .accountId(1)
                .borrowingStatus(BorrowingStatus.BORROWING)
                .borrowingEpoch(1000)
                .promisedReturnEpoch(1500)
                .build();

        doReturn(true).when(borrowingHeaderRepository)
                .existsById(borrowingHeaderOnBorrowingState.getId());
        doReturn(Optional.of(borrowingHeaderOnBorrowingState)).when(borrowingHeaderRepository)
                .findById(borrowingHeaderOnBorrowingState.getId());
        doReturn(borrowingHeaderOnBorrowingState).when(borrowingHeaderRepository).save(any());


        borrowingHeaderOnReturnedState = BorrowingHeader.builder()
                .id(2)
                .accountId(1)
                .borrowingStatus(BorrowingStatus.RETURNED)
                .borrowingEpoch(1000)
                .promisedReturnEpoch(1500)
                .build();

        doReturn(true).when(borrowingHeaderRepository)
                .existsById(borrowingHeaderOnReturnedState.getId());
        doReturn(Optional.of(borrowingHeaderOnReturnedState)).when(borrowingHeaderRepository)
                .findById(borrowingHeaderOnReturnedState.getId());


        borrowingHeaderOnOvertimeState = BorrowingHeader.builder()
                .id(3)
                .accountId(1)
                .borrowingStatus(BorrowingStatus.OVERTIME)
                .borrowingEpoch(1000)
                .promisedReturnEpoch(1500)
                .build();

        doReturn(true).when(borrowingHeaderRepository)
                .existsById(borrowingHeaderOnOvertimeState.getId());
        doReturn(Optional.of(borrowingHeaderOnOvertimeState)).when(borrowingHeaderRepository)
                .findById(borrowingHeaderOnOvertimeState.getId());

        bookAvailable = Book.builder()
                .id(1)
                .bookName("book name")
                .build();
        doReturn(true).when(bookRepository).existsById(bookAvailable.getId());

        bookBorrowed = Book.builder()
                .id(2)
                .bookName("book name")
                .build();
        doReturn(true).when(bookRepository).existsById(bookBorrowed.getId());
        doReturn(Arrays.asList(bookBorrowed)).when(borrowingHeaderRepository).getBorrowedBooks();

        bookNotExist = Book.builder()
                .id(0)
                .bookName("book name")
                .build();
        doReturn(false).when(bookRepository).existsById(bookNotExist.getId());


        accountAvailable = Account.builder()
                .id(1)
                .firstName("first name")
                .lastName("last name")
                .build();

        doReturn(true).when(accountRepository).existsById(accountAvailable.getId());

        accountNotExist = Account.builder()
                .id(0)
                .firstName("first name")
                .lastName("last name")
                .build();

        doReturn(false).when(accountRepository).existsById(accountNotExist.getId());

    }

    @Test
    @DisplayName("POST /borrow books not exist")
    public void borrowBooksTests1(){
        ResponseWrapper<String> response = borrowingController.borrowBooks(BorrowBookRequest.builder()
                .accountId(accountAvailable.getId())
                .borrowingDuration(1000)
                .bookIds(Arrays.asList(bookNotExist.getId()))
                .build());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /borrow books already borrowed")
    public void borrowBooksTests2(){
        ResponseWrapper<String> response = borrowingController.borrowBooks(BorrowBookRequest.builder()
                .accountId(accountAvailable.getId())
                .borrowingDuration(1000)
                .bookIds(Arrays.asList(bookBorrowed.getId()))
                .build());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().equalsIgnoreCase("book already borrowed!"), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /borrow accounts not exist")
    public void borrowBooksTests3(){
        ResponseWrapper<String> response = borrowingController.borrowBooks(BorrowBookRequest.builder()
                .accountId(accountNotExist.getId())
                .borrowingDuration(1000)
                .bookIds(Arrays.asList(bookAvailable.getId()))
                .build());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /borrow books and account available")
    public void borrowBooksTests4() {
        ResponseWrapper<String> response = borrowingController.borrowBooks(BorrowBookRequest.builder()
                .accountId(accountAvailable.getId())
                .borrowingDuration(1000)
                .bookIds(Arrays.asList(bookAvailable.getId()))
                .build());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /return/{id} header on BORROWING state")
    public void returnBooksTests1(){
        ResponseWrapper<String> response = borrowingController.returnBooks(borrowingHeaderOnBorrowingState.getId());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
    }


    @Test
    @DisplayName("POST /return/{id} header on RETURNED state")
    public void returnBooksTests2(){
        ResponseWrapper<String> response = borrowingController.returnBooks(borrowingHeaderOnReturnedState.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("invalid"), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /return/{id} header on OVERTIME state")
    public void returnBooksTests3(){
        ResponseWrapper<String> response = borrowingController.returnBooks(borrowingHeaderOnOvertimeState.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("invalid"), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /return/{id} id not exist")
    public void returnBooksTests4(){
        ResponseWrapper<String> response = borrowingController.returnBooks(borrowingHeaderNotExist.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
    }


}
