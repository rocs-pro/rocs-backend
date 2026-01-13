package com.nsbm.rocs.pos.repository.impl;

import com.nsbm.rocs.entity.pos.Product;
import com.nsbm.rocs.pos.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Product> productRowMapper = (rs, _) -> {
        Product product = new Product();
        product.setProductId(rs.getLong("product_id"));
        product.setSku(rs.getString("sku"));
        product.setBarcode(rs.getString("barcode"));
        product.setName(rs.getString("name"));
        product.setSellingPrice(rs.getBigDecimal("selling_price"));
        product.setTaxRate(rs.getBigDecimal("tax_rate"));
        product.setIsActive(rs.getBoolean("is_active"));
        return product;
    };

    @Override
    public Optional<Product> findById(Long productId) {
        try {
            String sql = "SELECT * FROM products WHERE product_id = ? AND is_active = TRUE";
            Product product = jdbcTemplate.queryForObject(sql, productRowMapper, productId);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        try {
            String sql = "SELECT * FROM products WHERE sku = ? AND is_active = TRUE";
            Product product = jdbcTemplate.queryForObject(sql, productRowMapper, sku);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Product> findByBarcode(String barcode) {
        try {
            String sql = "SELECT * FROM products WHERE barcode = ? AND is_active = TRUE";
            Product product = jdbcTemplate.queryForObject(sql, productRowMapper, barcode);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}