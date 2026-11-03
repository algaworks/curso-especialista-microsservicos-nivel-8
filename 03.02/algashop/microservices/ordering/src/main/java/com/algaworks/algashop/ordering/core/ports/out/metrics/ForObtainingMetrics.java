package com.algaworks.algashop.ordering.core.ports.out.metrics;

import java.time.LocalDate;
import java.util.List;

public interface ForObtainingMetrics {
	List<SalesOverviewOutput> getSalesOverview(LocalDate startDate,
	                                           LocalDate endDate);

	TotalOverviewOutput getTotalOverview();
}
