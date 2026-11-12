package com.algaworks.algashop.insights.presentation;

import com.algaworks.algashop.insights.application.insights.InsightsQueryService;
import com.algaworks.algashop.insights.application.insights.TopSellingProductOutput;
import com.algaworks.algashop.insights.infrastructure.security.SecurityAnnotations.CanReadMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/insights")
@RequiredArgsConstructor
public class InsightsController {

	private final InsightsQueryService insightsQueryService;

	@GetMapping("/top-selling-products")
	@CanReadMetrics
	public List<TopSellingProductOutput> getTopSellingProducts(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
		return insightsQueryService.getTopSellingProducts(yearMonth);
	}
}
