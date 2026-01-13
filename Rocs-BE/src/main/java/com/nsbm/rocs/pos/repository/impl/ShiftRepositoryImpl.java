package com.nsbm.rocs.pos.repository.impl;

import com.nsbm.rocs.entity.pos.CashShift;
import com.nsbm.rocs.pos.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Repository
public class ShiftRepositoryImpl implements ShiftRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<CashShift> shiftRowMapper = new RowMapper<CashShift>() {
        @Override
        public CashShift mapRow(ResultSet rs, int rowNum) throws SQLException {
            CashShift shift = new CashShift();

            shift.setShiftId(rs.getLong("shift_id"));
            shift.setBranchId(rs.getLong("branch_id"));
            shift.setCashierId(rs.getLong("cashier_id"));

            // Handle timestamps (can be null for closedAt)
            Timestamp openedTimestamp = rs.getTimestamp("opened_at");
            if (openedTimestamp != null) {
                shift.setOpenedAt(openedTimestamp.toLocalDateTime());
            }

            Timestamp closedTimestamp = rs.getTimestamp("closed_at");
            if (closedTimestamp != null) {
                shift.setClosedAt(closedTimestamp.toLocalDateTime());
            }

            // Map money fields
            shift.setOpeningCash(rs.getBigDecimal("opening_cash"));
            shift.setClosingCash(rs.getBigDecimal("closing_cash"));
            shift.setExpectedCash(rs.getBigDecimal("expected_cash"));
            shift.setCashDifference(rs.getBigDecimal("cash_difference"));
            shift.setTotalSales(rs.getBigDecimal("total_sales"));
            shift.setTotalReturns(rs.getBigDecimal("total_returns"));

            // Map strings
            shift.setStatus(rs.getString("status"));
            shift.setNotes(rs.getString("notes"));

            return shift;
        }
    };

    @Override
    public Long save(CashShift shift) {
        String sql = "INSERT INTO cash_shifts " +
                "(branch_id, cashier_id, opened_at, opening_cash, status) " +
                "VALUES (?, ?, NOW(), ?, ?)";

        // Execute INSERT query
        jdbcTemplate.update(
                sql,
                shift.getBranchId(),
                shift.getCashierId(),
                shift.getOpeningCash(),
                shift.getStatus()
        );

        // Get the generated ID
        String idSql = "SELECT LAST_INSERT_ID()";
        return jdbcTemplate.queryForObject(idSql, Long.class);
    }

    @Override
    public void update(CashShift shift) {
        String sql = "UPDATE cash_shifts SET " +
                "closed_at = ?, " +
                "closing_cash = ?, " +
                "expected_cash = ?, " +
                "cash_difference = ?, " +
                "total_sales = ?, " +
                "total_returns = ?, " +
                "status = ?, " +
                "notes = ? " +
                "WHERE shift_id = ?";

        jdbcTemplate.update(
                sql,
                shift.getClosedAt(),
                shift.getClosingCash(),
                shift.getExpectedCash(),
                shift.getCashDifference(),
                shift.getTotalSales(),
                shift.getTotalReturns(),
                shift.getStatus(),
                shift.getNotes(),
                shift.getShiftId()
        );
    }

    @Override
    public Optional<CashShift> findById(Long shiftId) {
        try {
            String sql = "SELECT * FROM cash_shifts WHERE shift_id = ?";
            CashShift shift = jdbcTemplate.queryForObject(sql, shiftRowMapper, shiftId);
            return Optional.ofNullable(shift);
        } catch (EmptyResultDataAccessException e) {
            // No result found - return empty Optional
            return Optional.empty();
        }
    }

    @Override
    public Optional<CashShift> findOpenShiftByCashierId(Long cashierId) {
        try {
            String sql = "SELECT * FROM cash_shifts " +
                    "WHERE cashier_id = ? AND status = 'OPEN'";
            CashShift shift = jdbcTemplate.queryForObject(sql, shiftRowMapper, cashierId);
            return Optional.ofNullable(shift);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean hasOpenShift(Long cashierId) {
        String sql = "SELECT COUNT(*) FROM cash_shifts " +
                "WHERE cashier_id = ? AND status = 'OPEN'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cashierId);
        return count != null && count > 0;
    }

    @Override
    public Optional<CashShift> findByIdWithStats(Long shiftId) {
        try {
            String sql =
                    "SELECT cs.*, " +
                            "  COALESCE(COUNT(s.sale_id), 0) as transaction_count, " +
                            "  COALESCE(SUM(s.net_total), 0) as current_sales " +
                            "FROM cash_shifts cs " +
                            "LEFT JOIN sales s ON cs.shift_id = s.shift_id " +
                            "WHERE cs.shift_id = ? " +
                            "GROUP BY cs.shift_id";

            CashShift shift = jdbcTemplate.queryForObject(sql, shiftRowMapper, shiftId);
            return Optional.ofNullable(shift);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CashShift> findByBranchId(Long branchId, int limit) {
        String sql = "SELECT * FROM cash_shifts " +
                "WHERE branch_id = ? " +
                "ORDER BY opened_at DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, shiftRowMapper, branchId, limit);
    }

    @Override
    public List<CashShift> findByCashierId(Long cashierId, int limit) {
        String sql = "SELECT * FROM cash_shifts " +
                "WHERE cashier_id = ? " +
                "ORDER BY opened_at DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, shiftRowMapper, cashierId, limit);
    }
}