package com.nsbm.rocs.pos.repository.impl;

import com.nsbm.rocs.entity.pos.Sale;
import com.nsbm.rocs.pos.repository.SaleRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class SaleRepositoryImpl implements SaleRepository {


    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Sale> saleRowMapper = (rs, _) -> {
        Sale sale = new Sale();

        // Primary key
        sale.setSaleId(rs.getLong("sale_id"));
        sale.setInvoiceNo(rs.getString("invoice_no"));

        // Foreign keys
        sale.setBranchId(rs.getLong("branch_id"));
        sale.setCashierId(rs.getLong("cashier_id"));

        // Handle nullable customer_id
        long customerId = rs.getLong("customer_id");
        if (!rs.wasNull()) {
            sale.setCustomerId(customerId);
        }

        // Handle nullable shift_id
        long shiftId = rs.getLong("shift_id");
        if (!rs.wasNull()) {
            sale.setShiftId(shiftId);
        }

        // Timestamps
        Timestamp saleTimestamp = rs.getTimestamp("sale_date");
        if (saleTimestamp != null) {
            sale.setSaleDate(saleTimestamp.toLocalDateTime());
        }

        Timestamp createdTimestamp = rs.getTimestamp("created_at");
        if (createdTimestamp != null) {
            sale.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        // Money fields
        sale.setGrossTotal(rs.getBigDecimal("gross_total"));
        sale.setDiscount(rs.getBigDecimal("discount"));
        sale.setTaxAmount(rs.getBigDecimal("tax_amount"));
        sale.setNetTotal(rs.getBigDecimal("net_total"));
        sale.setPaidAmount(rs.getBigDecimal("paid_amount"));
        sale.setChangeAmount(rs.getBigDecimal("change_amount"));

        // Status fields
        sale.setPaymentStatus(rs.getString("payment_status"));
        sale.setSaleType(rs.getString("sale_type"));
        sale.setNotes(rs.getString("notes"));

        return sale;
    };

    @Override
    public Long save(Sale sale) {
        String sql = "INSERT INTO sales " +
                "(invoice_no, branch_id, cashier_id, customer_id, shift_id, " +
                " gross_total, discount, tax_amount, net_total, paid_amount, " +
                " change_amount, payment_status, sale_type, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                sale.getInvoiceNo(),
                sale.getBranchId(),
                sale.getCashierId(),
                sale.getCustomerId(),
                sale.getShiftId(),
                sale.getGrossTotal(),
                sale.getDiscount(),
                sale.getTaxAmount(),
                sale.getNetTotal(),
                sale.getPaidAmount(),
                sale.getChangeAmount(),
                sale.getPaymentStatus(),
                sale.getSaleType(),
                sale.getNotes()
        );

        // Get generated ID
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @Override
    public List<Sale> findAll() {
        String sql = "SELECT * FROM sales ORDER BY sale_id DESC";
        return jdbcTemplate.query(sql, saleRowMapper);
    }

    @Override
    public List<Sale> findByPaymentStatus(String status) {
        String sql = "SELECT * FROM sales WHERE payment_status = ? ORDER BY sale_id DESC";
        return jdbcTemplate.query(sql, saleRowMapper, status);
    }

    @Override
    public Optional<Sale> findById(Long saleId) {
        try {
            String sql = "SELECT * FROM sales WHERE sale_id = ?";
            Sale sale = jdbcTemplate.queryForObject(sql, saleRowMapper, saleId);
            return Optional.ofNullable(sale);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Sale> findByInvoiceNo(String invoiceNo) {
        try {
            String sql = "SELECT * FROM sales WHERE invoice_no = ?";
            Sale sale = jdbcTemplate.queryForObject(sql, saleRowMapper, invoiceNo);
            return Optional.ofNullable(sale);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Sale> findByShiftId(Long shiftId) {
        String sql = "SELECT * FROM sales WHERE shift_id = ? ORDER BY sale_date DESC";
        return jdbcTemplate.query(sql, saleRowMapper, shiftId);
    }

    @Override
    public List<Sale> findByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM sales " +
                "WHERE branch_id = ? " +
                "  AND sale_date BETWEEN ? AND ? " +
                "ORDER BY sale_date DESC";
        return jdbcTemplate.query(sql, saleRowMapper, branchId, startDate, endDate);
    }

    @Override
    public int countByShiftId(Long shiftId) {
        String sql = "SELECT COUNT(*) FROM sales WHERE shift_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, shiftId);
        return count != null ? count : 0;
    }

    @Override
    public java.math.BigDecimal sumNetTotalByShiftId(Long shiftId) {
        String sql = "SELECT SUM(net_total) FROM sales WHERE shift_id = ?";
        java.math.BigDecimal sum = jdbcTemplate.queryForObject(sql, java.math.BigDecimal.class, shiftId);
        return sum != null ? sum : java.math.BigDecimal.ZERO;
    }
}