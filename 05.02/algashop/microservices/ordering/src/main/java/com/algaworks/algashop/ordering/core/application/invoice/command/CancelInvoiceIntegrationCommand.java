package com.algaworks.algashop.ordering.core.application.invoice.command;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelInvoiceIntegrationCommand implements OutboundIntegrationCommand {

    @NotBlank
    private String orderId;

    @Override
    public String getAggregateId() {
        return orderId;
    }
}