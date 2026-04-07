package com.chakit.payment.controller;

import com.chakit.payment.domain.Transaction;
import com.chakit.payment.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@Tag(name = "Merchants", description = "Merchant transaction operations")
public class MerchantController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/{merchantId}/transactions")
    @Operation(summary = "Get transactions by merchant ID")
    public List<Transaction> getTransactionsByMerchant(@PathVariable String merchantId) {
        return transactionService.getTransactionsByMerchant(merchantId);
    }
}
