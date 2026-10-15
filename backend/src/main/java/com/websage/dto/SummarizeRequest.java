package com.websage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SummarizeRequest {

    @NotBlank(message = "URL cannot be blank.")
    @Pattern(
        regexp = "^(https?)://[a-zA-Z0-9.-]+(:\\d+)?(/.*)?$",
        message = "URL must start with http:// or https:// and have a valid host format."
    )
    private String url;

    public SummarizeRequest() {
    }

    public SummarizeRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url != null ? url.trim() : null;
    }
}
