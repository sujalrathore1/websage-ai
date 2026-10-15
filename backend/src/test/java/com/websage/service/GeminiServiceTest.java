package com.websage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websage.exception.AiServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeminiServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should throw AiServiceException when GEMINI_API_KEY is not configured")
    void shouldThrowExceptionWhenApiKeyIsMissing() {
        GeminiService service = new GeminiService(
                "",
                "gemini-1.5-flash",
                "https://generativelanguage.googleapis.com/v1beta/models",
                5000,
                objectMapper
        );

        AiServiceException ex = assertThrows(AiServiceException.class, () ->
                service.summarize("Title", "Content")
        );

        assertTrue(ex.getMessage().contains("GEMINI_API_KEY is not configured"));
        assertEquals(500, ex.getStatusCode());
    }
}
