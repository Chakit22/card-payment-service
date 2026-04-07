package com.chakit.payment.service;

import com.chakit.payment.domain.Transaction;
import com.chakit.payment.domain.TransactionStatus;
import com.chakit.payment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private static final BigDecimal APPROVAL_LIMIT = new BigDecimal("10000");

    public Transaction createTransaction(Transaction transaction) {
        transaction.setCreatedAt(LocalDateTime.now());
        if (transaction.getAmount().compareTo(APPROVAL_LIMIT) < 0) {
            transaction.setStatus(TransactionStatus.APPROVED);
        } else {
            transaction.setStatus(TransactionStatus.DECLINED);
        }
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }

    public Page<Transaction> getAllTransactions(int page, int size) {
        return transactionRepository.findAll(PageRequest.of(page, size));
    }

    public Optional<Transaction> refundTransaction(Long id) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.setStatus(TransactionStatus.REFUNDED);
            return transactionRepository.save(transaction);
        });
    }

    public List<Transaction> getTransactionsByMerchant(String merchantId) {
        return transactionRepository.findByMerchantId(merchantId);
    }
}
