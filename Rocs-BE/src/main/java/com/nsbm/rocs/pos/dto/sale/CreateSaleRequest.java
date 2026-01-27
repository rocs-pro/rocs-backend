package com.nsbm.rocs.pos.dto.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CreateSaleRequest {


    private Long customerId;

    @NotEmpty(message = "Sale items are required")
    @Valid
    private List<SaleItemRequest> items;

    @Valid
    private List<PaymentRequest> payments;

    private BigDecimal discount;

    private String notes;
    private String status; // NEW FIELD for Hold/Pending

    @Override
    public String toString() {
        return "CreateSaleRequest{" +
                "customerId=" + customerId +
                ", items=" + items +
                ", payments=" + payments +
                ", discount=" + discount +
                ", status='" + status + '\'' + // Add to toString
                ", notes='" + notes + '\'' +
                '}';
    }
}