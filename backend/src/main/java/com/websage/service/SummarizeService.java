package com.websage.service;

import com.websage.dto.SummarizeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SummarizeService {

    private static final Logger log = LoggerFactory.getLogger(SummarizeService.class);

    private final WebScraperService webScraperService;
    private final GeminiService geminiService;

    public SummarizeService(WebScraperService webScraperService, GeminiService geminiService) {
        this.webScraperService = webScraperService;
        this.geminiService = geminiService;
    }

    /**
     * Coordinates web scraping and AI summarization.
     *
     * @param targetUrl The validated webpage URL to process
     * @return SummarizeResponse containing original URL, extracted title, and concise AI summary
     */
    public SummarizeResponse summarizeWebpage(String targetUrl) {
        log.info("Starting webpage summarization workflow for URL: {}", targetUrl);

        // Step 1: Scrape and sanitize HTML content
        WebScraperService.ScrapedContent scraped = webScraperService.scrape(targetUrl);
        log.info("Scraped webpage titled '{}' (Extracted text length: {} chars)", scraped.title(), scraped.content().length());

        // Step 2: Request AI summary from Google Gemini
        String summary = geminiService.summarize(scraped.title(), scraped.content());
        log.info("Successfully generated summary for '{}' (Length: {} chars)", scraped.title(), summary.length());

        return new SummarizeResponse(targetUrl, scraped.title(), summary);
    }
}
