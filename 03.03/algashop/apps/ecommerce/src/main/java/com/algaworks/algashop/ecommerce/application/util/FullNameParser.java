package com.algaworks.algashop.ecommerce.application.util;

public final class FullNameParser {

	private FullNameParser() {
	}

	public static NameParts split(String rawName) {
		if (rawName == null || rawName.isBlank()) {
			return new NameParts("", "");
		}

		String normalized = rawName.trim().replaceAll("\\s+", " ");
		int separator = normalized.indexOf(' ');

		if (separator < 0) {
			return new NameParts(normalized, "");
		}

		return new NameParts(
				normalized.substring(0, separator).trim(),
				normalized.substring(separator + 1).trim()
		);
	}

	public record NameParts(String firstName, String lastName) {
	}
}
