package com.nsbm.rocs.pos.dto.shift;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class CloseShiftRequest {

    @NotNull(message = "Closing cash is required")
    @PositiveOrZero(message = "Closing cash cannot be negative")
    private BigDecimal closingCash;

    private String notes; // Optional notes

    public CloseShiftRequest() {}

    public CloseShiftRequest(BigDecimal closingCash, String notes) {
        this.closingCash = closingCash;
        this.notes = notes;
    }

    public BigDecimal getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CloseShiftRequest{" +
                "closingCash=" + closingCash +
                ", notes='" + notes + '\'' +
                '}';
    }
}