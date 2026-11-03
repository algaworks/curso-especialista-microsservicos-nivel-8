package com.algaworks.algashop.ordering.core.ports.out.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalOverviewOutput {
    private Integer activeCustomers;
    private Integer activeShoppingCarts;
}
