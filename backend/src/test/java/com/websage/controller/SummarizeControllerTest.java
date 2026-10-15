package com.websage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websage.dto.SummarizeRequest;
import com.websage.dto.SummarizeResponse;
import com.websage.exception.GlobalExceptionHandler;
import com.websage.exception.ScrapingException;
import com.websage.exception.SecurityValidationException;
import com.websage.service.SummarizeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SummarizeController.class)
@Import(GlobalExceptionHandler.class)
class SummarizeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SummarizeService summarizeService;

    @Test
    @DisplayName("POST /api/summarize - Successful summarization returns 200 OK")
    void shouldReturnOkForValidUrl() throws Exception {
        SummarizeRequest request = new SummarizeRequest("https://example.com/article");
        SummarizeResponse expectedResponse = new SummarizeResponse(
                "https://example.com/article",
                "Example Article",
                "A concise AI-generated summary of the webpage."
        );

        when(summarizeService.summarizeWebpage("https://example.com/article"))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://example.com/article"))
                .andExpect(jsonPath("$.title").value("Example Article"))
                .andExpect(jsonPath("$.summary").value("A concise AI-generated summary of the webpage."));
    }

    @Test
    @DisplayName("POST /api/summarize - Blank URL returns 400 Bad Request with error code")
    void shouldReturnBadRequestWhenUrlIsBlank() throws Exception {
        SummarizeRequest request = new SummarizeRequest("");

        mockMvc.perform(post("/api/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_URL"));
    }

    @Test
    @DisplayName("POST /api/summarize - Non-HTTP protocol returns 400 Bad Request")
    void shouldReturnBadRequestWhenProtocolIsInvalid() throws Exception {
        SummarizeRequest request = new SummarizeRequest("ftp://example.com/file.txt");

        mockMvc.perform(post("/api/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_URL"));
    }

    @Test
    @DisplayName("POST /api/summarize - Unsafe internal host returns 400 Bad Request with UNSAFE_URL")
    void shouldReturnBadRequestWhenUrlIsUnsafe() throws Exception {
        SummarizeRequest request = new SummarizeRequest("http://localhost:8080");

        when(summarizeService.summarizeWebpage(anyString()))
                .thenThrow(new SecurityValidationException("Access to local or internal host 'localhost' is blocked."));

        mockMvc.perform(post("/api/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("UNSAFE_URL"))
                .andExpect(jsonPath("$.message").value("Access to local or internal host 'localhost' is blocked."));
    }

    @Test
    @DisplayName("POST /api/summarize - Scraping failure returns appropriate error status")
    void shouldReturnUnprocessableEntityWhenScrapingFails() throws Exception {
        SummarizeRequest request = new SummarizeRequest("https://example.com/empty");

        when(summarizeService.summarizeWebpage(anyString()))
                .thenThrow(new ScrapingException("The webpage does not contain sufficient readable text.", 422));

        mockMvc.perform(post("/api/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("SCRAPING_FAILED"))
                .andExpect(jsonPath("$.message").value("The webpage does not contain sufficient readable text."));
    }

    @Test
    @DisplayName("GET /api/health - Health check returns 200 OK")
    void shouldReturnOkForHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("WebSage AI Backend"));
    }
}
