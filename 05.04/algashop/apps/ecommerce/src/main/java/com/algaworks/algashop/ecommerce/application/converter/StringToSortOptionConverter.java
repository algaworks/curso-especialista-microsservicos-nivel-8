package com.algaworks.algashop.ecommerce.application.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class StringToSortOptionConverter implements Converter<String, Sort> {

	@Override
	public Sort convert(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		String[] tokens = value.split(",", -1);
		if (tokens.length != 2) {
			return null;
		}

		String property = tokens[0];
		String direction = tokens[1];

		return Sort.by(Sort.Direction.fromString(direction), property);
	}
}
