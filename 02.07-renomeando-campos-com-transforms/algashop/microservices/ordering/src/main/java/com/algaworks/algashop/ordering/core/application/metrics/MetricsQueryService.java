package com.algaworks.algashop.ordering.core.application.metrics;

import com.algaworks.algashop.ordering.core.ports.in.metrics.ForQueryingMetrics;
import com.algaworks.algashop.ordering.core.ports.out.metrics.ForObtainingMetrics;
import com.algaworks.algashop.ordering.core.ports.out.metrics.SalesOverviewOutput;
import com.algaworks.algashop.ordering.core.ports.out.metrics.TotalOverviewOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsQueryService implements ForQueryingMetrics {

	private final ForObtainingMetrics forObtainingMetrics;

	@Override
	public List<SalesOverviewOutput> getSalesOverview(YearMonth yearMonthStart, YearMonth yearMonthEnd) {
		return forObtainingMetrics.getSalesOverview(
				yearMonthStart.atDay(1),
				yearMonthEnd.atEndOfMonth()
		);
	}

	@Override
	public TotalOverviewOutput getTotalOverview() {
		return forObtainingMetrics.getTotalOverview();
	}
}
