package com.algaworks.algashop.ecommerce.application.util;

public class SafeEnumConverterUtil {

	private SafeEnumConverterUtil() {}

	public static <T extends Enum<T>> T safeParseValue(Class<T> clazz, String value) {
		if (value == null) {
			return null;
		}
		try {
			return T.valueOf(clazz, value);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T extends Enum<T>> T safeParseValue(Class<T> clazz, String value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		try {
			return T.valueOf(clazz, value);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
