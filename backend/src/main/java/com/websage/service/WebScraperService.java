package com.websage.service;

import com.websage.exception.ScrapingException;
import com.websage.exception.SecurityValidationException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class WebScraperService {

    private static final Logger log = LoggerFactory.getLogger(WebScraperService.class);

    private static final Set<String> BLOCKED_HOST_SUFFIXES = Set.of(
            ".local", ".localhost", ".internal", ".corp", ".lan", ".home", ".arpa"
    );

    private static final Set<String> BLOCKED_HOSTS = Set.of(
            "localhost", "127.0.0.1", "0.0.0.0", "::1", "metadata.google.internal",
            "instance-data", "169.254.169.254"
    );

    private final int timeoutMs;
    private final int maxContentLength;
    private final int maxBodyBytes;
    private final String userAgent;

    public WebScraperService(
            @Value("${app.scraper.timeout-ms:10000}") int timeoutMs,
            @Value("${app.scraper.max-content-length:12000}") int maxContentLength,
            @Value("${app.scraper.max-body-bytes:2097152}") int maxBodyBytes,
            @Value("${app.scraper.user-agent:WebSageAI-Scraper/1.0 (+https://github.com/websage-ai)}") String userAgent
    ) {
        this.timeoutMs = timeoutMs;
        this.maxContentLength = maxContentLength;
        this.maxBodyBytes = maxBodyBytes;
        this.userAgent = userAgent;
    }

    public record ScrapedContent(String title, String content) {}

    /**
     * Scrapes webpage content with SSRF safety checks, redirect protection, and HTML cleaning.
     */
    public ScrapedContent scrape(String targetUrl) {
        String currentUrl = targetUrl.trim();
        int maxRedirects = 3;
        int redirectCount = 0;

        while (true) {
            validateUrlSecurity(currentUrl);

            try {
                Connection.Response response = Jsoup.connect(currentUrl)
                        .userAgent(userAgent)
                        .timeout(timeoutMs)
                        .maxBodySize(maxBodyBytes)
                        .followRedirects(false)
                        .ignoreHttpErrors(true)
                        .execute();

                int statusCode = response.statusCode();

                // Handle Redirects safely with SSRF re-validation
                if (statusCode >= 300 && statusCode < 400) {
                    if (redirectCount >= maxRedirects) {
                        throw new ScrapingException("Too many redirects encountered while fetching the webpage.", 422);
                    }
                    String location = response.header("Location");
                    if (location == null || location.isBlank()) {
                        throw new ScrapingException("Received redirect status without a valid Location header.", 422);
                    }
                    URI currentUri = URI.create(currentUrl);
                    URI redirectUri = currentUri.resolve(location);
                    currentUrl = redirectUri.toString();
                    redirectCount++;
                    log.info("Following safe redirect ({}) to: {}", redirectCount, currentUrl);
                    continue;
                }

                if (statusCode >= 400) {
                    log.warn("Failed to fetch webpage, HTTP status: {}", statusCode);
                    throw new ScrapingException("Target webpage returned HTTP " + statusCode + ". Unable to scrape content.", statusCode);
                }

                String contentType = response.contentType();
                if (contentType != null && !contentType.toLowerCase(Locale.ROOT).contains("text/html")
                        && !contentType.toLowerCase(Locale.ROOT).contains("application/xhtml")) {
                    throw new ScrapingException("Unsupported content type (" + contentType + "). Only HTML webpages can be summarized.", 415);
                }

                Document document = response.parse();
                return extractCleanContent(document);

            } catch (SecurityValidationException | ScrapingException e) {
                throw e;
            } catch (UnknownHostException e) {
                log.warn("Host not found for URL {}: {}", currentUrl, e.getMessage());
                throw new ScrapingException("Target domain could not be resolved. Please check the URL.", 422, e);
            } catch (IOException e) {
                log.warn("I/O error fetching URL {}: {}", currentUrl, e.getMessage());
                throw new ScrapingException("Unable to connect to the target website: " + e.getMessage(), 502, e);
            } catch (Exception e) {
                log.error("Unexpected error scraping URL {}: {}", currentUrl, e.getMessage());
                throw new ScrapingException("Failed to scrape webpage: " + e.getMessage(), 500, e);
            }
        }
    }

    /**
     * SSRF and protocol safety checks on URL and resolved IP addresses.
     */
    public void validateUrlSecurity(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            throw new SecurityValidationException("URL cannot be empty.");
        }

        URI uri;
        try {
            uri = URI.create(urlString.trim());
        } catch (Exception e) {
            throw new SecurityValidationException("Malformed URL format: " + e.getMessage());
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new SecurityValidationException("Only HTTP and HTTPS protocols are supported.");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new SecurityValidationException("Invalid URL: Missing host.");
        }

        String lowerHost = host.toLowerCase(Locale.ROOT);

        if (BLOCKED_HOSTS.contains(lowerHost)) {
            throw new SecurityValidationException("Access to local or internal host '" + host + "' is blocked.");
        }

        for (String suffix : BLOCKED_HOST_SUFFIXES) {
            if (lowerHost.endsWith(suffix)) {
                throw new SecurityValidationException("Access to internal host domain '" + host + "' is blocked.");
            }
        }

        // Validate resolved IP addresses against loopback, private ranges, link-local, and AWS metadata
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            if (addresses == null || addresses.length == 0) {
                throw new SecurityValidationException("Unable to resolve DNS for host: " + host);
            }

            for (InetAddress address : addresses) {
                if (address.isAnyLocalAddress()) {
                    throw new SecurityValidationException("Host resolves to an unrestricted local address (0.0.0.0).");
                }
                if (address.isLoopbackAddress()) {
                    throw new SecurityValidationException("Access to loopback addresses is forbidden.");
                }
                if (address.isLinkLocalAddress()) {
                    throw new SecurityValidationException("Access to link-local addresses is forbidden.");
                }
                if (address.isSiteLocalAddress()) {
                    throw new SecurityValidationException("Access to private local network addresses is forbidden.");
                }
                if (address.isMulticastAddress()) {
                    throw new SecurityValidationException("Access to multicast addresses is forbidden.");
                }

                byte[] rawIp = address.getAddress();
                if (rawIp.length == 4) {
                    int b0 = rawIp[0] & 0xFF;
                    int b1 = rawIp[1] & 0xFF;

                    // 127.0.0.0/8 (Loopback)
                    if (b0 == 127) {
                        throw new SecurityValidationException("Access to loopback IP range is forbidden.");
                    }
                    // 10.0.0.0/8 (Private)
                    if (b0 == 10) {
                        throw new SecurityValidationException("Access to private IP range 10.0.0.0/8 is forbidden.");
                    }
                    // 172.16.0.0/12 (Private)
                    if (b0 == 172 && (b1 >= 16 && b1 <= 31)) {
                        throw new SecurityValidationException("Access to private IP range 172.16.0.0/12 is forbidden.");
                    }
                    // 192.168.0.0/16 (Private)
                    if (b0 == 192 && b1 == 168) {
                        throw new SecurityValidationException("Access to private IP range 192.168.0.0/16 is forbidden.");
                    }
                    // 169.254.0.0/16 (Link-local / Cloud metadata like 169.254.169.254)
                    if (b0 == 169 && b1 == 254) {
                        throw new SecurityValidationException("Access to cloud metadata / link-local IP range is forbidden.");
                    }
                    // 100.64.0.0/10 (Carrier-Grade NAT)
                    if (b0 == 100 && (b1 >= 64 && b1 <= 127)) {
                        throw new SecurityValidationException("Access to Carrier-Grade NAT range is forbidden.");
                    }
                    // 0.0.0.0/8
                    if (b0 == 0) {
                        throw new SecurityValidationException("Access to 0.0.0.0/8 network range is forbidden.");
                    }
                } else if (address instanceof Inet6Address) {
                    // Check IPv6 unique local (fc00::/7)
                    int b0 = rawIp[0] & 0xFF;
                    if ((b0 & 0xFE) == 0xFC) {
                        throw new SecurityValidationException("Access to IPv6 Unique Local Address range is forbidden.");
                    }
                }
            }
        } catch (UnknownHostException e) {
            throw new ScrapingException("Could not resolve host: " + host, 422, e);
        }
    }

    /**
     * Extracts page title and clean readable text from Jsoup Document.
     */
    private ScrapedContent extractCleanContent(Document doc) {
        // Extract title
        String title = doc.title();
        if (title == null || title.isBlank()) {
            Element ogTitle = doc.selectFirst("meta[property=og:title], meta[name=twitter:title]");
            if (ogTitle != null && ogTitle.hasAttr("content") && !ogTitle.attr("content").isBlank()) {
                title = ogTitle.attr("content");
            } else {
                Element h1 = doc.selectFirst("h1");
                title = (h1 != null && !h1.text().isBlank()) ? h1.text() : "Untitled Webpage";
            }
        }
        title = title.trim();

        // Remove scripts, styles, navigation, headers, footers, ads, and noise
        doc.select("script, style, noscript, nav, footer, header, aside, svg, iframe, form, button, input, .advertisement, .ads, .ad, .cookie-banner, .popup, #footer, #header, #nav, [role=navigation], [role=banner], [role=contentinfo]").remove();

        // Prefer article or main content if available
        String mainText = "";
        Element contentElement = doc.selectFirst("article, main, [role=main], .post-content, .entry-content, .article-body, #content");
        if (contentElement != null) {
            String candidate = contentElement.text();
            if (candidate.length() >= 150) {
                mainText = candidate;
            }
        }

        // Fallback to body text
        if (mainText.isBlank()) {
            Element body = doc.body();
            mainText = body != null ? body.text() : "";
        }

        // Normalize whitespace
        mainText = mainText.replaceAll("\\s+", " ").trim();

        if (mainText.length() < 30) {
            throw new ScrapingException("The webpage does not contain sufficient readable text to generate a summary. The site may require JavaScript rendering or user authentication.", 422);
        }

        // Truncate to maximum allowed length to avoid excessive token consumption
        if (mainText.length() > maxContentLength) {
            mainText = mainText.substring(0, maxContentLength) + "... [Content truncated for summary]";
        }

        return new ScrapedContent(title, mainText);
    }
}
