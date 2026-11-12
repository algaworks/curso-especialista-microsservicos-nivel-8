package com.algaworks.algashop.notifications.infrastructure.template;

import com.algaworks.algashop.notifications.application.email.EmailTemplateRenderer;
import com.algaworks.algashop.notifications.application.email.TemplateRenderException;
import org.springframework.expression.EvaluationException;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class EmailTemplateRendererThymeleafImpl implements EmailTemplateRenderer {

	private static final Locale LOCALE = Locale.US;

	private final TemplateEngine subjectTemplateEngine = createEngine(TemplateMode.TEXT);

	private final TemplateEngine bodyTemplateEngine = createEngine(TemplateMode.HTML);

	@Override
	public String renderSubject(String subjectTemplate, Map<String, Object> variables) {
		return process(variables, subjectTemplateEngine, subjectTemplate);
	}

	@Override
	public String renderBody(String bodyTemplate, Map<String, Object> variables) {
		return process(variables, bodyTemplateEngine, bodyTemplate);
	}

	@Override
	public void assertRenderable(String subjectTemplate, String bodyTemplate) {
		try {
			renderSubject(subjectTemplate, Map.of());
			renderBody(bodyTemplate, Map.of());
		} catch (TemplateRenderException e) {
			if (!isEvaluationFailure(e)) {
				throw e;
			}
		}
	}

	private String process(Map<String, Object> variables,
	                       TemplateEngine engine,
	                       String subjectTemplate) {
		try {
			Context context = new Context(LOCALE, variables);
			return engine.process(subjectTemplate, context);
		} catch (TemplateEngineException e) {
			throw new TemplateRenderException("Failed to render email " + e.getMessage(), e);
		}
	}

	private boolean isEvaluationFailure(TemplateRenderException e) {
		for (Throwable t = e; t != null; t = t.getCause()) {
			if (t instanceof EvaluationException) {
				return true;
			}
		}
		return false;
	}

	private static TemplateEngine createEngine(TemplateMode mode) {
		StringTemplateResolver resolver = new StringTemplateResolver();
		resolver.setTemplateMode(mode);
		resolver.setCacheable(false);

		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(resolver);
		return engine;
	}
}
