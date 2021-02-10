package com.example.demo.controller;

import com.example.demo.database.Book;
import com.example.demo.model.ResponseWrapper;
import com.example.demo.model.request.InsertBookRequest;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("book")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @PostMapping
    public ResponseWrapper<Book> insertBook(@RequestBody InsertBookRequest insertBookRequest){

        Book insertedBook = bookRepository.save(Book.builder()
                .bookName(insertBookRequest.getBookName())
                .build());

        return ResponseWrapper.<Book>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(insertedBook)
                .build();
    }

    @GetMapping
    public ResponseWrapper<List<Book>> getAllBook(){

        return ResponseWrapper.<List<Book>>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content((List<Book>) bookRepository.findAll())
                .build();
    }

    @GetMapping("{id}")
    public ResponseWrapper<Book> findBookById(@PathVariable int id){

        if(!bookRepository.existsById(id)){
            return ResponseWrapper.<Book>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("book not found!")
                    .success(false)
                    .content(null)
                    .build();
        }
        return ResponseWrapper.<Book>builder()
                        .code(HttpStatus.OK.value())
                        .message("OK")
                        .success(true)
                        .content(bookRepository.findById(id).get())
                        .build();
    }

    @PutMapping("{id}")
    public ResponseWrapper<Book> updateBook(@PathVariable int id, @RequestBody InsertBookRequest updateBookRequest){
        if(!bookRepository.existsById(id)){
            return ResponseWrapper.<Book>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("book not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        Book updatedBook = bookRepository.save(Book.builder()
                .id(id)
                .bookName(updateBookRequest.getBookName())
                .build());

        return ResponseWrapper.<Book>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(updatedBook)
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseWrapper<String> deleteBookById(@PathVariable int id){

        if(!bookRepository.existsById(id)){
            return ResponseWrapper.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("book not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        bookRepository.deleteById(id);
        return ResponseWrapper.<String>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(null)
                .build();
    }

}
