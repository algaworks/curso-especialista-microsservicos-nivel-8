package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CategoryClient;
import com.algaworks.algashop.ecommerce.application.client.ProductClient;
import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.model.page.ProductCatalogPageModel;
import com.algaworks.algashop.ecommerce.application.model.page.ProductPageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductCatalogController {

	private final ProductClient productClient;
	private final CategoryClient categoryClient;

	@GetMapping("/products")
	public ModelAndView index(ProductFilter productFilter) {
		var pageBuilder = ProductCatalogPageModel.builder();

		if (productFilter.getCategoryId() != null) {
			productFilter.setCategoriesId(new String[]{productFilter.getCategoryId()});
		}


		try {
			PageModel<ProductModel> products = productClient.findAll(productFilter);
			pageBuilder.products(products.getContent());
			MultiValueMap<String, String> queryParams = productFilter.toQueryParams();
			List<PageLinkModel> pageLinks = Paginator.calculatePages(products, "/products", queryParams);
			pageBuilder.pageLinks(pageLinks);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return pageBuilder
				.productFilter(productFilter)
				.build()
				.toModelAndView();
	}

	@GetMapping("/products/{id}")
	public ModelAndView getById(@PathVariable String id,
	                            @RequestParam(value = "addedToCart", required = false) Boolean addedToCart,
	                            RedirectAttributes redirectAttributes) {
		ProductModel product;
		try {
			product = productClient.findById(id);
			return new ModelAndView(String.format("redirect:/products/%s/%s", product.getSlug(), id));
		} catch (HttpClientErrorException.NotFound e) {
			redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.danger("Product not found."));
			return new ModelAndView("redirect:/products");
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.danger(
					"An unknown error occurred while trying to load the product. Please try again later."));
			return new ModelAndView("redirect:/products");
		}
	}

	@GetMapping("/products/{slug}/{id}")
	public ModelAndView getBySlugAndId(@PathVariable String slug, @PathVariable String id,
								@RequestParam(value = "addedToCart", required = false) Boolean addedToCart,
								RedirectAttributes redirectAttributes) {
		var pageBuilder = ProductPageModel.builder();

		PageModel<CategoryModel> categories = categoryClient.findAll();
		pageBuilder.categories(categories.getContent());

		ProductModel product;
		try {
			product = productClient.findById(id);
		} catch (HttpClientErrorException.NotFound e) {
			redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.danger("Product not found."));
			return new ModelAndView("redirect:/products");
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			redirectAttributes.addFlashAttribute("alertMessage", AlertMessage.danger(
					"An unknown error occurred while trying to load the product. Please try again later."));
			return new ModelAndView("redirect:/products");
		}

		List<ImageModel> images = productClient.findImagesByProductId(id);

		return pageBuilder
				.product(product)
				.addedToCart(addedToCart)
				.images(images)
				.build()
				.toModelAndView();
	}

}