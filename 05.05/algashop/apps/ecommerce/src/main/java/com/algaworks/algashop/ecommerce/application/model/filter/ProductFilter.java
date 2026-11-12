package com.algaworks.algashop.ecommerce.application.model.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
	private String term;
	private String categoryId;
	private String[] categoriesId;
	private Boolean hasDiscount;
	private BigDecimal priceFrom;
	private BigDecimal priceTo;

	public List<String> getCategoriesIdAsList() {
		if (categoriesId == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(categoriesId);
	}

	@Builder.Default
	private int size = 6;

	@Builder.Default
	private int page = 0;

	private Sort sort;

	public enum Property {
		ADDED_AT,
		SALE_PRICE
	}

	public MultiValueMap<String, String> toQueryParams() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		if (this.getCategoriesId() != null ) {
			for (String id : this.getCategoriesId()) {
				params.add("categoriesId", id);
			}
		}

		if (StringUtils.isNotBlank(this.getTerm())) {
			params.add("term", this.getTerm());
		}

		if (this.getHasDiscount() != null) {
			params.add("hasDiscount", this.getHasDiscount().toString());
		}

		if (this.getPriceFrom() != null) {
			params.add("priceFrom", this.getPriceFrom().toString());
		}

		if (this.getPriceTo() != null) {
			params.add("priceTo", this.getPriceTo().toString());
		}

		if (this.getSort() != null && this.getSort().iterator().hasNext()) {
			Sort.Order currentSort = this.getSort().iterator().next();
			params.add("sortByProperty", currentSort.getProperty());
			params.add("sortDirection", currentSort.getDirection().name());
		}

		params.add("size", Integer.valueOf(this.getSize()).toString());
		params.add("page", Integer.valueOf(this.getPage()).toString());

		return params;
	}

}
