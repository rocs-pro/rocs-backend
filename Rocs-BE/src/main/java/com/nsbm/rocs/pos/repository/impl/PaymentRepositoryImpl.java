package com.nsbm.rocs.pos.repository.impl;

import com.nsbm.rocs.entity.pos.Payment;
import com.nsbm.rocs.pos.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Payment> paymentRowMapper = (rs, _) -> {
        Payment payment = new Payment();

        payment.setPaymentId(rs.getLong("payment_id"));
        payment.setSaleId(rs.getLong("sale_id"));
        payment.setPaymentType(rs.getString("payment_type"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setReferenceNo(rs.getString("reference_no"));
        payment.setCardLast4(rs.getString("card_last4"));

        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            payment.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        return payment;
    };

    @Override
    public Long save(Payment payment) {
        String sql = "INSERT INTO payments " +
                "(sale_id, payment_type, amount, reference_no, card_last4) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                payment.getSaleId(),
                payment.getPaymentType(),
                payment.getAmount(),
                payment.getReferenceNo(),
                payment.getCardLast4()
        );

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @Override
    public void saveBatch(List<Payment> payments) {
        String sql = "INSERT INTO payments " +
                "(sale_id, payment_type, amount, reference_no, card_last4) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                Payment payment = payments.get(i);
                ps.setLong(1, payment.getSaleId());
                ps.setString(2, payment.getPaymentType());
                ps.setBigDecimal(3, payment.getAmount());
                ps.setString(4, payment.getReferenceNo());
                ps.setString(5, payment.getCardLast4());
            }

            @Override
            public int getBatchSize() {
                return payments.size();
            }
        });
    }

    @Override
    public List<Payment> findBySaleId(Long saleId) {
        String sql = "SELECT * FROM payments WHERE sale_id = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, saleId);
    }
}