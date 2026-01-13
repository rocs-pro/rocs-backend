package com.nsbm.rocs.pos.repository.impl;

import com.nsbm.rocs.entity.pos.SaleItem;
import com.nsbm.rocs.pos.repository.SaleItemRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SaleItemRepositoryImpl implements SaleItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<SaleItem> saleItemRowMapper = (rs, _) -> {
        SaleItem item = new SaleItem();

        item.setSaleItemId(rs.getLong("sale_item_id"));
        item.setSaleId(rs.getLong("sale_id"));
        item.setProductId(rs.getLong("product_id"));

        // Handle nullable serial_id
        long serialId = rs.getLong("serial_id");
        if (!rs.wasNull()) {
            item.setSerialId(serialId);
        }

        item.setQty(rs.getInt("qty"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setDiscount(rs.getBigDecimal("discount"));
        item.setTaxRate(rs.getBigDecimal("tax_rate"));
        item.setTotal(rs.getBigDecimal("total"));

        return item;
    };

    @Override
    public Long save(SaleItem saleItem) {
        String sql = "INSERT INTO sale_items " +
                "(sale_id, product_id, serial_id, qty, unit_price, discount, tax_rate, total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                saleItem.getSaleId(),
                saleItem.getProductId(),
                saleItem.getSerialId(),
                saleItem.getQty(),
                saleItem.getUnitPrice(),
                saleItem.getDiscount(),
                saleItem.getTaxRate(),
                saleItem.getTotal()
        );

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    @Override
    public void saveBatch(List<SaleItem> saleItems) {
        String sql = "INSERT INTO sale_items " +
                "(sale_id, product_id, serial_id, qty, unit_price, discount, tax_rate, total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, int i) throws SQLException {
                SaleItem item = saleItems.get(i);
                ps.setLong(1, item.getSaleId());
                ps.setLong(2, item.getProductId());

                if (item.getSerialId() != null) {
                    ps.setLong(3, item.getSerialId());
                } else {
                    ps.setNull(3, java.sql.Types.BIGINT);
                }

                ps.setInt(4, item.getQty());
                ps.setBigDecimal(5, item.getUnitPrice());
                ps.setBigDecimal(6, item.getDiscount());
                ps.setBigDecimal(7, item.getTaxRate());
                ps.setBigDecimal(8, item.getTotal());
            }

            @Override
            public int getBatchSize() {
                return saleItems.size();
            }
        });
    }

    @Override
    public List<SaleItem> findBySaleId(Long saleId) {
        String sql = "SELECT * FROM sale_items WHERE sale_id = ?";
        return jdbcTemplate.query(sql, saleItemRowMapper, saleId);
    }

    @Override
    public List<SaleItem> findBySaleIdWithProductDetails(Long saleId) {

        String sql = "SELECT si.*, p.name as product_name, p.sku, p.barcode, ps.serial_no " +
                "FROM sale_items si " +
                "JOIN products p ON si.product_id = p.product_id " +
                "LEFT JOIN product_serials ps ON si.serial_id = ps.serial_id " +
                "WHERE si.sale_id = ?";

        // Extended row mapper with product details
        // Note: SaleItem entity doesn't have product name field
        // In real implementation, you'd use a DTO for this
        // For now, we just return the basic item
        return jdbcTemplate.query(sql, saleItemRowMapper, saleId);
    }
}