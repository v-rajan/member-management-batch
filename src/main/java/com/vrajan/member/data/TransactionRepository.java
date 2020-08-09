package com.vrajan.member.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vrajan.member.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Transaction findByTransactionIdentifier(String transactionIdentifier);
}
