package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageModel<T> {
	private int number;
	private int size;
	private int totalPages;
	private long totalElements;
	private List<T> content = new ArrayList<>();
}
