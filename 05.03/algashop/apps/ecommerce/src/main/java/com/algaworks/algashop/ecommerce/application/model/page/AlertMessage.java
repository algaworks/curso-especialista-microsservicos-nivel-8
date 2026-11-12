package com.algaworks.algashop.ecommerce.application.model.page;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AlertMessage implements Serializable {

	private static final long serialVersionUID = 6521796774292506634L;

	private String content;
	private Type type;

	public enum Type {
		INFO,
		SUCCESS,
		WARNING,
		DANGER
	}

	public static AlertMessage success(String content) {
		return new AlertMessage(content, Type.SUCCESS);
	}

	public static AlertMessage warning(String content) {
		return new AlertMessage(content, Type.WARNING);
	}

	public static AlertMessage info(String content) {
		return new AlertMessage(content, Type.INFO);
	}

	public static AlertMessage danger(String content) {
		return new AlertMessage(content, Type.DANGER);
	}
}
