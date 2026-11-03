package com.algaworks.algashop.insights.infrastructure.persistence;

import com.algaworks.algashop.insights.application.insights.InsightsQueryService;
import com.algaworks.algashop.insights.application.insights.TopSellingProductOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InsightsQueryServiceImpl implements InsightsQueryService {

	private static final String TOP_SELLING_PRODUCTS_SQL = """
			select p.id as product_id, p.name as product_name, p.category_name,
			       sum(oi.quantity) as quantity, sum(oi.total_amount) as revenue
			from order_item oi
			inner join "order" o on o.id = oi.order_id
			inner join product p on p.id = oi.product_id
			where o.paid_at is not null and o.canceled_at is null
			    and o.paid_at >= :startDate and o.paid_at < :endDateExclusive
			group by p.id, p.name, p.category_name
			order by quantity desc, revenue desc, p.name
			limit 15
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<TopSellingProductOutput> getTopSellingProducts(YearMonth yearMonth) {
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDateExclusive = yearMonth.plusMonths(1).atDay(1);

		MapSqlParameterSource parameters = new MapSqlParameterSource()
				.addValue("startDate", startDate)
				.addValue("endDateExclusive", endDateExclusive);

		return jdbcTemplate.query(
				TOP_SELLING_PRODUCTS_SQL,
				parameters,
				(rs, rowNum) -> new TopSellingProductOutput(
						rs.getObject("product_id", UUID.class),
						rs.getString("product_name"),
						rs.getString("category_name"),
						rs.getLong("quantity"),
						rs.getBigDecimal("revenue")));
	}
}
