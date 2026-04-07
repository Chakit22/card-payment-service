package com.chakit.payment;

import com.chakit.payment.domain.Transaction;
import com.chakit.payment.domain.TransactionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldApproveSmallTransaction() throws Exception {
        Transaction t = mockTransaction(new BigDecimal("500.00"));

        MvcResult result = mvc.perform(post("/api/transactions")
                .content(objectMapper.writeValueAsBytes(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.amount", is(500.00)))
                .andReturn();

        Long id = objectMapper.readValue(result.getResponse().getContentAsString(), Transaction.class).getId();

        // Retrieve
        mvc.perform(get("/api/transactions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    public void shouldDeclineLargeTransaction() throws Exception {
        Transaction t = mockTransaction(new BigDecimal("15000.00"));

        mvc.perform(post("/api/transactions")
                .content(objectMapper.writeValueAsBytes(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("DECLINED")));
    }

    @Test
    public void shouldRefundTransaction() throws Exception {
        Transaction t = mockTransaction(new BigDecimal("100.00"));

        MvcResult result = mvc.perform(post("/api/transactions")
                .content(objectMapper.writeValueAsBytes(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readValue(result.getResponse().getContentAsString(), Transaction.class).getId();

        mvc.perform(post("/api/transactions/" + id + "/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REFUNDED")));
    }

    @Test
    public void shouldReturn404ForMissingTransaction() throws Exception {
        mvc.perform(get("/api/transactions/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldListTransactions() throws Exception {
        mvc.perform(get("/api/transactions?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetTransactionsByMerchant() throws Exception {
        Transaction t = mockTransaction(new BigDecimal("250.00"));

        mvc.perform(post("/api/transactions")
                .content(objectMapper.writeValueAsBytes(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/merchants/MERCH001/transactions"))
                .andExpect(status().isOk());
    }

    private Transaction mockTransaction(BigDecimal amount) {
        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setCurrency("USD");
        t.setCardLast4("4242");
        t.setMerchantId("MERCH001");
        return t;
    }
}
