package com.websage.service;

import com.websage.dto.SummarizeResponse;
import com.websage.exception.AiServiceException;
import com.websage.exception.ScrapingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummarizeServiceTest {

    @Mock
    private WebScraperService webScraperService;

    @Mock
    private GeminiService geminiService;

    private SummarizeService summarizeService;

    @BeforeEach
    void setUp() {
        summarizeService = new SummarizeService(webScraperService, geminiService);
    }

    @Test
    @DisplayName("Should successfully orchestrate scraping and AI summarization")
    void shouldSummarizeSuccessfully() {
        String testUrl = "https://example.com/tech-news";
        String expectedTitle = "Tech News 2026";
        String expectedContent = "Artificial intelligence developments are advancing rapidly across industries.";
        String expectedSummary = "This article covers recent breakthroughs in artificial intelligence.";

        when(webScraperService.scrape(testUrl))
                .thenReturn(new WebScraperService.ScrapedContent(expectedTitle, expectedContent));
        when(geminiService.summarize(expectedTitle, expectedContent))
                .thenReturn(expectedSummary);

        SummarizeResponse response = summarizeService.summarizeWebpage(testUrl);

        assertNotNull(response);
        assertEquals(testUrl, response.getUrl());
        assertEquals(expectedTitle, response.getTitle());
        assertEquals(expectedSummary, response.getSummary());

        verify(webScraperService, times(1)).scrape(testUrl);
        verify(geminiService, times(1)).summarize(expectedTitle, expectedContent);
    }

    @Test
    @DisplayName("Should propagate ScrapingException when scraping fails")
    void shouldPropagateScrapingException() {
        String testUrl = "https://example.com/broken-page";

        when(webScraperService.scrape(testUrl))
                .thenThrow(new ScrapingException("Unable to connect to target website", 502));

        ScrapingException ex = assertThrows(ScrapingException.class, () ->
                summarizeService.summarizeWebpage(testUrl)
        );

        assertEquals(502, ex.getStatusCode());
        verify(geminiService, never()).summarize(anyString(), anyString());
    }

    @Test
    @DisplayName("Should propagate AiServiceException when Gemini AI call fails")
    void shouldPropagateAiServiceException() {
        String testUrl = "https://example.com/news";
        String expectedTitle = "News Page";
        String expectedContent = "Valid readable content on page.";

        when(webScraperService.scrape(testUrl))
                .thenReturn(new WebScraperService.ScrapedContent(expectedTitle, expectedContent));
        when(geminiService.summarize(expectedTitle, expectedContent))
                .thenThrow(new AiServiceException("Gemini API rate limit exceeded", 429));

        AiServiceException ex = assertThrows(AiServiceException.class, () ->
                summarizeService.summarizeWebpage(testUrl)
        );

        assertEquals(429, ex.getStatusCode());
    }
}
