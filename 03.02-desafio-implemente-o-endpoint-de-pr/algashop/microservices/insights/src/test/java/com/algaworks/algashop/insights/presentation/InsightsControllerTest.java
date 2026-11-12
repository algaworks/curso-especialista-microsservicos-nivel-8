package com.algaworks.algashop.insights.presentation;

import com.algaworks.algashop.insights.application.insights.InsightsQueryService;
import com.algaworks.algashop.insights.application.insights.TopSellingProductOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InsightsController.class)
class InsightsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private InsightsQueryService insightsQueryService;

	@Test
	void shouldParseYearMonth() throws Exception {
		UUID productId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");
		when(insightsQueryService.getTopSellingProducts(YearMonth.of(2026, 9))).thenReturn(List.of(
				new TopSellingProductOutput(productId, "Notebook X11", "Laptops", 18L, new BigDecimal("18000.00"))));

		mockMvc.perform(get("/api/v1/insights/top-selling-products").param("yearMonth", "2026-09"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].productId").value(productId.toString()))
				.andExpect(jsonPath("$[0].productName").value("Notebook X11"))
				.andExpect(jsonPath("$[0].categoryName").value("Laptops"))
				.andExpect(jsonPath("$[0].quantity").value(18))
				.andExpect(jsonPath("$[0].revenue").value(18000.00));

		verify(insightsQueryService).getTopSellingProducts(YearMonth.of(2026, 9));
	}

	@Test
	void shouldRespondBadRequestWhenYearMonthIsAbsent() throws Exception {
		mockMvc.perform(get("/api/v1/insights/top-selling-products"))
				.andExpect(status().isBadRequest());

		verify(insightsQueryService, never()).getTopSellingProducts(any());
	}

	@Test
	void shouldRespondBadRequestWhenYearMonthIsInvalid() throws Exception {
		mockMvc.perform(get("/api/v1/insights/top-selling-products").param("yearMonth", "abc"))
				.andExpect(status().isBadRequest());

		verify(insightsQueryService, never()).getTopSellingProducts(any());
	}
}
