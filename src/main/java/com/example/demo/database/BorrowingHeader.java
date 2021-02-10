package com.example.demo.database;

import com.example.demo.enums.BorrowingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    int accountId;

    @Enumerated(EnumType.STRING)
    BorrowingStatus borrowingStatus;

    long borrowingEpoch,
            promisedReturnEpoch,
            returnEpoch;
}
