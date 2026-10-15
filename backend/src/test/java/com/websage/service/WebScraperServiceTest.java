package com.websage.service;

import com.websage.exception.SecurityValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class WebScraperServiceTest {

    private WebScraperService webScraperService;

    @BeforeEach
    void setUp() {
        webScraperService = new WebScraperService(
                5000,
                10000,
                1048576,
                "WebSageAI-Test/1.0"
        );
    }

    @ParameterizedTest(name = "Should block unsafe URL: {0}")
    @ValueSource(strings = {
            "http://localhost",
            "http://localhost:8080/internal",
            "http://127.0.0.1",
            "http://127.0.0.1:3000",
            "http://0.0.0.0",
            "http://169.254.169.254/latest/meta-data",
            "http://metadata.google.internal",
            "http://app.local",
            "http://service.internal",
            "ftp://example.com/file.txt",
            "file:///etc/passwd",
            "javascript:alert(1)"
    })
    @DisplayName("SSRF Protection: Block internal and non-HTTP protocols")
    void shouldRejectUnsafeUrls(String unsafeUrl) {
        assertThrows(SecurityValidationException.class, () ->
                webScraperService.validateUrlSecurity(unsafeUrl)
        );
    }

    @Test
    @DisplayName("Validation: Reject blank or null URL")
    void shouldRejectBlankUrl() {
        assertThrows(SecurityValidationException.class, () ->
                webScraperService.validateUrlSecurity("")
        );
        assertThrows(SecurityValidationException.class, () ->
                webScraperService.validateUrlSecurity("   ")
        );
        assertThrows(SecurityValidationException.class, () ->
                webScraperService.validateUrlSecurity(null)
        );
    }

    @Test
    @DisplayName("Validation: Allow legitimate public HTTPS URL")
    void shouldAllowPublicHttpsUrl() {
        assertDoesNotThrow(() ->
                webScraperService.validateUrlSecurity("https://example.com")
        );
    }
}
