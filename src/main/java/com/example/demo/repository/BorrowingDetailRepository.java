package com.example.demo.repository;

import com.example.demo.database.BorrowingDetail;
import org.springframework.data.repository.CrudRepository;

public interface BorrowingDetailRepository extends CrudRepository<BorrowingDetail, Integer> {
}
