package com.algaworks.algashop.ordering.core.ports.in.metrics;

import com.algaworks.algashop.ordering.core.ports.out.metrics.SalesOverviewOutput;
import com.algaworks.algashop.ordering.core.ports.out.metrics.TotalOverviewOutput;

import java.time.YearMonth;
import java.util.List;

public interface ForQueryingMetrics {
	List<SalesOverviewOutput> getSalesOverview(YearMonth yearMonthStart,
	                                           YearMonth yearMonthEnd);

	TotalOverviewOutput getTotalOverview();
}
