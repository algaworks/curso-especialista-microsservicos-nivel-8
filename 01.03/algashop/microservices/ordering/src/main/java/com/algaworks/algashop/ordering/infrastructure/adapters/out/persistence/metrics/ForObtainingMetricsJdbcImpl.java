package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.metrics;

import com.algaworks.algashop.ordering.core.ports.out.metrics.ForObtainingMetrics;
import com.algaworks.algashop.ordering.core.ports.out.metrics.SalesOverviewOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForObtainingMetricsJdbcImpl implements ForObtainingMetrics {

	private static final String SALES_OVERVIEW_SQL = """
			select to_char(g, 'yyyy-mm') as year_month,
                   count(o.id) as quantity,
                   coalesce(sum(o.total_amount), 0) as revenue
            from generate_series(cast(:startDate as date), cast(:endDate as date), interval '1 month') as g
            left join "order" o
                on date_trunc('month', o.paid_at) = date_trunc('month', g)
                and o.paid_at is not null
                and o.canceled_at is null
            group by 1
            order by 1
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<SalesOverviewOutput> getSalesOverview(LocalDate startDate, LocalDate endDate) {
		return jdbcTemplate.query(SALES_OVERVIEW_SQL,
				Map.of("startDate", startDate, "endDate", endDate),
				(rs, rowNum) -> new SalesOverviewOutput(
						YearMonth.parse(rs.getString("year_month")),
						rs.getInt("quantity"),
						rs.getBigDecimal("revenue")
					)
				);
	}
}
