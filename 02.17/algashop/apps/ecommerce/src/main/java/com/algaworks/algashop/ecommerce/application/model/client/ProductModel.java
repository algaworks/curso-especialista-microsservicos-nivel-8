package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class ProductModel {
	private String id;
	private OffsetDateTime addedAt;
	private String name;
	private String brand;
	private String slug;
	private Boolean enabled;

	private BigDecimal regularPrice;
	private BigDecimal salePrice;

	private Boolean hasDiscount;
	private Boolean inStock;

	private Integer discountPercentageRounded;
	private String description;

	private CategoryModel category;
	private ImageModel mainImage;
}
