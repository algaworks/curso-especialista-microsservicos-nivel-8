package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class PageLinkModel {
    private String path;
	private int size;
	private int number;

	private boolean current;
	private boolean isFirstOfGroup;
	private boolean isLastOfGroup;
	private boolean hasNext;
	private boolean hasPrevious;

	private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    public String getLink() {
		StringBuilder link = new StringBuilder(path + "?page=" + (number - 1) + "&size=" + size);
		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			String queryKey = entry.getKey();
			for (String queryValue : entry.getValue()) {
				if (!queryKey.equals("page") && !queryKey.equals("size")) {
					link.append("&").append(queryKey).append("=").append(queryValue);
				}
			}
		}
		return link.toString();
    }
}
