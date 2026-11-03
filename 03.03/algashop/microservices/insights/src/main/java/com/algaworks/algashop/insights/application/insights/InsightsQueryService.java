package com.algaworks.algashop.insights.application.insights;

import java.time.YearMonth;
import java.util.List;

public interface InsightsQueryService {
	List<TopSellingProductOutput> getTopSellingProducts(YearMonth yearMonth);
}
