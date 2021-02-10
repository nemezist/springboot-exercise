package com.example.demo;

import com.example.demo.controller.AccountController;
import com.example.demo.database.Account;
import com.example.demo.model.ResponseWrapper;
import com.example.demo.model.request.InsertAccountRequest;
import com.example.demo.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class AccountControllerTests {
    @Autowired
    AccountController accountController;

    @MockBean
    AccountRepository accountRepository;

    Account accountWithId,
            accountWithoutId,
            accountInvalid;

    @BeforeEach
    public void prepareMock(){
        accountWithId = Account.builder()
                .id(1)
                .firstName("first name")
                .lastName("last name")
                .build();

        accountWithoutId = Account.builder()
                .firstName("first name")
                .lastName("last name")
                .build();

        accountInvalid = Account.builder()
                .id(0)
                .firstName("first name")
                .lastName("last name")
                .build();

        doReturn(accountWithId).when(accountRepository).save(accountWithId);
        doReturn(accountWithId).when(accountRepository).save(accountWithoutId);

        doReturn(Arrays.asList(accountWithId)).when(accountRepository).findAll();

        doReturn(Optional.of(accountWithId)).when(accountRepository).findById(accountWithId.getId());
        doReturn(true).when(accountRepository).existsById(accountWithId.getId());

        doReturn(Optional.empty()).when(accountRepository).findById(accountInvalid.getId());
        doReturn(false).when(accountRepository).existsById(accountInvalid.getId());
    }

    @Test
    @DisplayName("GET /account")
    public void getAllAccountsTest(){
        ResponseWrapper<List<Account>> response = accountController.getAllAccounts();

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals(1, response.getContent().size(), "response invalid - length mismatch!");
    }

    @Test
    @DisplayName("GET /account/{id} founded case")
    public void getAccountByIdTest1(){
        ResponseWrapper<Account> response = accountController.getAccountById(accountWithId.getId());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(accountWithId.getId(),
                response.getContent().getId(), "invalid response - data mismatch!");
    }

    @Test
    @DisplayName("GET /account/{id} not founded case")
    public void getAccountByIdTest2(){
        ResponseWrapper<Account> response = accountController.getAccountById(accountInvalid.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
        assertNull(response.getContent(), "invalid response - data mismatch!");
    }

    @Test
    @DisplayName("PUT /account/{id} founded case")
    public void updateAccountTest1(){
        ResponseWrapper<Account> response = accountController.updateAccount(accountWithId.getId(), InsertAccountRequest.builder()
                .firstName(accountWithId.getFirstName())
                .lastName(accountWithId.getLastName())
                .build());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(accountWithId.getId(),
                response.getContent().getId(), "invalid response - data mismatch!");
    }

    @Test
    @DisplayName("PUT /account/{id} not founded case")
    public void updateAccountTest2(){
        ResponseWrapper<Account> response = accountController.updateAccount(accountInvalid.getId(), InsertAccountRequest.builder()
                .firstName(accountInvalid.getFirstName())
                .lastName(accountInvalid.getLastName())
                .build());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found"), "invalid response - message mismatch!");
        assertNull(response.getContent(), "invalid response - data mismatch!");
    }

    @Test
    @DisplayName("DELETE /account/{id} founded case")
    public void deleteAccountByIdTest1(){
        ResponseWrapper<String> response = accountController.deleteAccountById(accountWithId.getId());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");

    }


    @Test
    @DisplayName("DELETE /account/{id} not founded case")
    public void deleteAccountByIdTest2(){
        ResponseWrapper<String> response = accountController.deleteAccountById(accountInvalid.getId());

        assertFalse(response.isSuccess(), "request success!");
        assertTrue(response.getMessage().contains("not found!"), "invalid response - message mismatch!");
    }

    @Test
    @DisplayName("POST /account")
    public void insertAccountTest(){
        ResponseWrapper<Account> response = accountController.insertAccount(InsertAccountRequest.builder()
                .firstName(accountWithoutId.getFirstName())
                .lastName(accountWithoutId.getLastName())
                .build());

        assertTrue(response.isSuccess(), "request failed!");
        assertEquals("OK", response.getMessage(), "invalid response - message mismatch!");
        assertEquals(accountWithId.getId(),
                response.getContent().getId(), "invalid response - data mismatch!");
    }
}
