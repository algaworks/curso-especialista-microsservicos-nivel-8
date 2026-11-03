package com.algaworks.algashop.notifications.application.template.management;

import com.algaworks.algashop.notifications.application.email.EmailTemplateHtmlValidator;
import com.algaworks.algashop.notifications.application.email.EmailTemplateRenderer;
import com.algaworks.algashop.notifications.application.email.InvalidTemplateHtmlException;
import com.algaworks.algashop.notifications.application.email.TemplateRenderException;
import com.algaworks.algashop.notifications.domain.model.template.EmailTemplate;
import com.algaworks.algashop.notifications.domain.model.template.EmailTemplateRepository;
import com.algaworks.algashop.notifications.domain.model.template.TemplateNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailTemplateManagementApplicationServiceTest {

    @Mock
    private EmailTemplateRepository repository;

    @Mock
    private EmailTemplateRenderer renderer;

    @Mock
    private EmailTemplateHtmlValidator htmlValidator;

    @InjectMocks
    private EmailTemplateManagementApplicationService service;

    private final EmailTemplateInput input = new EmailTemplateInput("welcome", "Welcome", "<p>Hello</p>");

    @Test
    void givenValidInput_whenCreate_shouldSaveTemplateAndReturnId() {
        when(repository.saveAndFlush(any(EmailTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID id = service.create(input);

        assertThat(id).isNotNull();
        verify(renderer).assertRenderable("Welcome", "<p>Hello</p>");
        verify(htmlValidator).validateSyntax("<p>Hello</p>");
        verify(repository).saveAndFlush(any(EmailTemplate.class));
    }

    @Test
    void givenInvalidTemplateSyntax_whenCreate_shouldThrowAndNotSave() {
        doThrow(new TemplateRenderException("bad syntax", null)).when(renderer).assertRenderable(any(), any());

        assertThatExceptionOfType(TemplateRenderException.class)
                .isThrownBy(() -> service.create(input));

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void givenInvalidHtml_whenCreate_shouldThrowAndNotSave() {
        doThrow(new InvalidTemplateHtmlException(List.of("Element <script> is not allowed")))
                .when(htmlValidator).validateSyntax(any());

        assertThatExceptionOfType(InvalidTemplateHtmlException.class)
                .isThrownBy(() -> service.create(input));

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void givenExistingTemplate_whenUpdate_shouldChangeFieldsAndSave() {
        EmailTemplate template = new EmailTemplate("old", "Old subject", "<p>Old</p>");
        when(repository.findById(template.getId())).thenReturn(Optional.of(template));

        service.update(template.getId(), input);

        assertThat(template.getName()).isEqualTo("welcome");
        assertThat(template.getSubject()).isEqualTo("Welcome");
        assertThat(template.getBody()).isEqualTo("<p>Hello</p>");
        verify(repository).saveAndFlush(template);
    }

    @Test
    void givenNonExistingTemplate_whenUpdate_shouldThrowTemplateNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(TemplateNotFoundException.class)
                .isThrownBy(() -> service.update(id, input));

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void givenInvalidHtml_whenUpdate_shouldThrowAndKeepTemplateUntouched() {
        EmailTemplate template = new EmailTemplate("old", "Old subject", "<p>Old</p>");
        when(repository.findById(template.getId())).thenReturn(Optional.of(template));
        doThrow(new InvalidTemplateHtmlException(List.of("bad"))).when(htmlValidator).validateSyntax(any());

        assertThatExceptionOfType(InvalidTemplateHtmlException.class)
                .isThrownBy(() -> service.update(template.getId(), input));

        assertThat(template.getName()).isEqualTo("old");
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void givenExistingTemplate_whenDelete_shouldDeleteIt() {
        EmailTemplate template = new EmailTemplate("welcome", "Welcome", "<p>Hello</p>");
        when(repository.findById(template.getId())).thenReturn(Optional.of(template));

        service.delete(template.getId());

        verify(repository).delete(template);
    }

    @Test
    void givenNonExistingTemplate_whenDelete_shouldThrowTemplateNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatExceptionOfType(TemplateNotFoundException.class)
                .isThrownBy(() -> service.delete(id));

        verify(repository, never()).delete(any());
    }
}
