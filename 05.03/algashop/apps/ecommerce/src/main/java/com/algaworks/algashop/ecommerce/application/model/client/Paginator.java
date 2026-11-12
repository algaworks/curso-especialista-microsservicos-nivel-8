package com.algaworks.algashop.ecommerce.application.model.client;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class Paginator {

	private Paginator() {}

    public static List<PageLinkModel> calculatePages(PageModel<?> pageModel, String path) {
        return calculatePages(pageModel.getTotalPages(), pageModel.getNumber()+1, pageModel.getSize(), path, new LinkedMultiValueMap<>());
    }

    public static List<PageLinkModel> calculatePages(int totalPages, int actualPage, int size, String path, MultiValueMap<String, String> queryParams) {
        int maxPages = 5;
        if (totalPages <= maxPages) {
            return buildPagesFromStart(totalPages, actualPage, size, path, queryParams);
        } else if (actualPage <= 3) {
            return buildPagesFromStart(maxPages, actualPage, size, path, queryParams);
        } else if (actualPage > totalPages - 3) {
            return buildPagesFromEnd(totalPages, actualPage, size, path, queryParams);
        } else {
            return buildPagesFromMiddle(actualPage, size, path, queryParams);
        }
    }

    private static List<PageLinkModel> buildPagesFromStart(int maxPages, int actualPage, int size, String path, MultiValueMap<String, String> queryParams) {
        List<PageLinkModel> pages = new ArrayList<>();
        for (int i = 1; i <= maxPages; i++) {
            pages.add(new PageLinkModel(path, size, i, i == actualPage,
                    i == 1, i == maxPages, i < maxPages, i > 1, queryParams));
        }
        return pages;
    }

    private static List<PageLinkModel> buildPagesFromEnd(int totalPages, int actualPage, int size, String path, MultiValueMap<String, String> queryParams) {
        List<PageLinkModel> pages = new ArrayList<>();
        for (int i = totalPages - 4; i <= totalPages; i++) {
            pages.add(new PageLinkModel(path, size, i, i == actualPage,
                    i == 1, i == totalPages, i < totalPages, i > 1, queryParams));
        }
        return pages;
    }

    private static List<PageLinkModel> buildPagesFromMiddle(int actualPage, int size, String path, MultiValueMap<String, String> queryParams) {
        List<PageLinkModel> pages = new ArrayList<>();
        for (int i = actualPage - 2; i <= actualPage + 2; i++) {
            pages.add(new PageLinkModel(path, size, i, i == actualPage, i == 1,
                    i > actualPage + 2, i < actualPage + 2, i > 1, queryParams));
        }
        return pages;
    }

    public static List<PageLinkModel> calculatePages(PageModel<?> pageModel, String path, MultiValueMap<String, String> queryParams) {
		return calculatePages(pageModel.getTotalPages(), pageModel.getNumber() + 1, pageModel.getSize(), path, queryParams);
    }
}