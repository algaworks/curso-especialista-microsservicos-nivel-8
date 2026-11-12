package com.algaworks.algashop.insights.presentation;

import com.algaworks.algashop.insights.application.insights.InsightsQueryService;
import com.algaworks.algashop.insights.infrastructure.security.InsightsSecurityConfig;
import com.algaworks.algashop.insights.infrastructure.token.JwtGrantedAuthoritiesDelegatingConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InsightsController.class)
@Import(InsightsSecurityConfig.class)
class InsightsControllerSecurityTest {

	private static final String URL = "/api/v1/insights/top-selling-products?yearMonth=2026-09";

	private final JwtGrantedAuthoritiesDelegatingConverter authoritiesConverter = new JwtGrantedAuthoritiesDelegatingConverter();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private InsightsQueryService insightsQueryService;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@Test
	void shouldRespondUnauthorizedWithoutToken() throws Exception {
		mockMvc.perform(get(URL))
				.andExpect(status().isUnauthorized())
				.andExpect(header().exists("WWW-Authenticate"));

		verify(insightsQueryService, never()).getTopSellingProducts(any());
	}

	@Test
	void shouldRespondForbiddenWithoutMetricsScope() throws Exception {
		mockMvc.perform(get(URL).with(jwt().jwt(j -> j.claim("scope", "products:read")).authorities(authoritiesConverter)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.type").value("/errors/forbidden"));

		verify(insightsQueryService, never()).getTopSellingProducts(any());
	}

	@Test
	void shouldRespondForbiddenForCustomer() throws Exception {
		mockMvc.perform(get(URL).with(jwt().jwt(j -> j.claim("scope", "metrics:read").claim("role", "CUSTOMER")).authorities(authoritiesConverter)))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.type").value("/errors/forbidden"));

		verify(insightsQueryService, never()).getTopSellingProducts(any());
	}

	@Test
	void shouldRespondOkWithMetricsScope() throws Exception {
		when(insightsQueryService.getTopSellingProducts(YearMonth.of(2026, 9))).thenReturn(List.of());

		mockMvc.perform(get(URL).with(jwt().jwt(j -> j.claim("scope", "metrics:read")).authorities(authoritiesConverter)))
				.andExpect(status().isOk());

		verify(insightsQueryService).getTopSellingProducts(YearMonth.of(2026, 9));
	}

	@Test
	void shouldRespondOkForManager() throws Exception {
		when(insightsQueryService.getTopSellingProducts(YearMonth.of(2026, 9))).thenReturn(List.of());

		mockMvc.perform(get(URL).with(jwt().jwt(j -> j.claim("scope", "metrics:read").claim("role", "MANAGER")).authorities(authoritiesConverter)))
				.andExpect(status().isOk());
	}
}
