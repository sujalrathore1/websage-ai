package com.websage.controller;

import com.websage.dto.SummarizeRequest;
import com.websage.dto.SummarizeResponse;
import com.websage.service.SummarizeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SummarizeController {

    private static final Logger log = LoggerFactory.getLogger(SummarizeController.class);

    private final SummarizeService summarizeService;

    public SummarizeController(SummarizeService summarizeService) {
        this.summarizeService = summarizeService;
    }

    /**
     * Endpoint to summarize a webpage by URL.
     * POST /api/summarize
     *
     * @param request Validated request containing the target URL
     * @return SummarizeResponse with URL, title, and AI summary
     */
    @PostMapping("/summarize")
    public ResponseEntity<SummarizeResponse> summarize(@Valid @RequestBody SummarizeRequest request) {
        log.info("Received summarization request for: {}", request.getUrl());
        SummarizeResponse response = summarizeService.summarizeWebpage(request.getUrl());
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for uptime monitors and deployment platforms.
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "WebSage AI Backend",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
