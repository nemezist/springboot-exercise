package com.example.demo.controller;

import com.example.demo.database.Account;
import com.example.demo.model.ResponseWrapper;
import com.example.demo.model.request.InsertAccountRequest;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("account")
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @PostMapping
    public ResponseWrapper<Account> insertAccount(@RequestBody InsertAccountRequest insertAccountRequest){
        Account insertedAccount =  accountRepository.save(Account.builder()
                .firstName(insertAccountRequest.getFirstName())
                .lastName(insertAccountRequest.getLastName()).build());

        return ResponseWrapper.<Account>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(insertedAccount)
                .build();
    }

    @GetMapping
    public ResponseWrapper<List<Account>> getAllAccounts(){
        return ResponseWrapper.<List<Account>>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content((List<Account>) accountRepository.findAll())
                .build();
    }

    @GetMapping("{id}")
    public ResponseWrapper<Account> getAccountById(@PathVariable int id){

        if(!accountRepository.existsById(id)){
            return ResponseWrapper.<Account>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("account not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        return ResponseWrapper.<Account>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(accountRepository.findById(id).get())
                .build();
    }

    @PutMapping("{id}")
    public ResponseWrapper<Account> updateAccount(@PathVariable int id, @RequestBody InsertAccountRequest updateAccountRequest){
        if(!accountRepository.existsById(id)){
            return ResponseWrapper.<Account>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("account not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        Account updatedAccount = accountRepository.save(Account.builder()
                .id(id)
                .firstName(updateAccountRequest.getFirstName())
                .lastName(updateAccountRequest.getLastName())
                .build());

        return ResponseWrapper.<Account>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .success(true)
                .content(updatedAccount)
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseWrapper<String> deleteAccountById(@PathVariable int id){

        if(!accountRepository.existsById(id)){
            return ResponseWrapper.<String>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("account not found!")
                    .success(false)
                    .content(null)
                    .build();
        }

        accountRepository.deleteById(id);
        return ResponseWrapper.<String>builder()
                    .code(HttpStatus.OK.value())
                    .message("OK")
                    .success(true)
                    .content(null)
                    .build();
        }


}
