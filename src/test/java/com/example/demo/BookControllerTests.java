package com.example.demo;

import com.example.demo.controller.BookController;
import com.example.demo.database.Book;
import com.example.demo.model.ResponseWrapper;
import com.example.demo.model.request.InsertBookRequest;
import com.example.demo.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class BookControllerTests {

    @Autowired
    BookController bookController;

    @MockBean
    BookRepository bookRepository;

    Book bookWithId,
            bookWithoutId,
            bookInvalid;

    @BeforeEach
    public void prepareMock(){
        bookWithId = Book.builder()
                .bookName("book name 1")
                .id(1)
                .build();

        bookWithoutId = Book.builder()
                .bookName("book name 1")
                .build();

        bookInvalid = Book.builder()
                .bookName("book name")
                .id(0)
                .build();

        doReturn(Arrays.asList(bookWithId)).when(bookRepository).findAll();

        doReturn(Optional.of(bookWithId)).when(bookRepository).findById(1);
        doReturn(true).when(bookRepository).existsById(1);

        doReturn(false).when(bookRepository).existsById(bookInvalid.getId());
        doReturn(Optional.empty()).when(bookRepository).findById(bookInvalid.getId());

        doReturn(bookWithId).when(bookRepository).save(bookWithId);
        doReturn(bookWithId).when(bookRepository).save(bookWithoutId);
    }

    @Test
    @DisplayName("GET /book")
    public void getAllBookTest(){
        ResponseWrapper<List<Book>> response = bookController.getAllBook();

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals(1,
                response.getContent().size(), "invalid response - size mismatch!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(bookWithId.getBookName(), response.getContent()
                .get(0).getBookName(), "invalid response - data mismatch!");
    }

    @Test
    @DisplayName("GET /book/{id} founded case")
    public void findBookByIdTest1(){
        ResponseWrapper<Book> response = bookController.findBookById(bookWithId.getId());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(bookWithId.getId(),
                response.getContent().getId(), "invalid response - data mismatch!");
    }

    @Test
    @DisplayName("GET /book/{id} not founded case")
    public void findBookByIdTest2(){
        ResponseWrapper<Book> response = bookController.findBookById(bookInvalid.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertNull(response.getContent(), "invalid response - content not null!");
    }

    @Test
    @DisplayName("PUT /book/{id} founded case")
    public void updateBookTest1(){

        ResponseWrapper<Book> response = bookController.updateBook(bookWithId.getId(),
                InsertBookRequest.builder()
                        .bookName(bookWithId.getBookName())
                        .build());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(bookWithId.getId(), response.getContent().getId(), "invalid response - content mismatch!");
    }

    @Test
    @DisplayName("PUT /book/{id} not founded case")
    public void updateBookTest2(){
        ResponseWrapper<Book> response = bookController.updateBook(bookInvalid.getId(),
                InsertBookRequest.builder()
                        .bookName(bookInvalid.getBookName())
                        .build());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
        assertNull(response.getContent(), "invalid response - content not null!");
    }

    @Test
    @DisplayName("POST /book")
    public void insertBookTest(){
        ResponseWrapper<Book> response = bookController.insertBook(
                InsertBookRequest.builder()
                        .bookName(bookWithoutId.getBookName())
                        .build());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(bookWithId.getId(), response.getContent().getId(), "invalid response - content mismatch!");
    }

    @Test
    @DisplayName("DELETE /book/{id} founded case")
    public void deleteBookTest1(){
        ResponseWrapper<String> response = bookController.deleteBookById(bookWithId.getId());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("DELETE /book/{id} not founded case")
    public void deleteBookTest2(){
        ResponseWrapper<String> response = bookController.deleteBookById(bookInvalid.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
    }
}
