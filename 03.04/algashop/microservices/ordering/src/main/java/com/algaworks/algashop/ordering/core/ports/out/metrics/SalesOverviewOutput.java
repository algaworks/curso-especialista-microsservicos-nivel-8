package com.algaworks.algashop.ordering.core.ports.out.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOverviewOutput {
    private YearMonth yearMonth;
    private Integer quantity;
    private BigDecimal revenue;
}