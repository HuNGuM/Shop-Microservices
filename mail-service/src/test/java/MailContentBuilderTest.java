import com.hungum.mail.service.MailContentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailContentBuilderTest {

    @Mock
    private TemplateEngine templateEngine;

    private MailContentBuilder mailContentBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mailContentBuilder = new MailContentBuilder(templateEngine);
    }

    @Test
    void testBuildShouldReturnProcessedTemplate() {
        // Arrange
        String message = "Test Message";
        String expectedContent = "Processed Content";
        when(templateEngine.process(eq("mailTemplate"), any(Context.class))).thenReturn(expectedContent);

        // Act
        String result = mailContentBuilder.build(message);

        // Assert
        assertNotNull(result);
        assertEquals(expectedContent, result);
        verify(templateEngine, times(1)).process(eq("mailTemplate"), any(Context.class)); // Проверка вызова метода process
    }

    @Test
    void testBuildShouldThrowExceptionWhenTemplateFails() {
        // Arrange
        String message = "Test Message";
        when(templateEngine.process(eq("mailTemplate"), any(Context.class))).thenThrow(new RuntimeException("Template error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> mailContentBuilder.build(message));
    }
}
