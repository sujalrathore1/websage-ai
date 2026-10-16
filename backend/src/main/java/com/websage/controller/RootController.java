package com.websage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    /**
     * Root endpoint to serve platform pingers, load balancers, and direct browser visits.
     * GET /
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "service", "WebSage AI Backend",
                "status", "UP",
                "version", "1.0.0",
                "healthEndpoint", "/api/health",
                "summarizeEndpoint", "/api/summarize"
        ));
    }
}
