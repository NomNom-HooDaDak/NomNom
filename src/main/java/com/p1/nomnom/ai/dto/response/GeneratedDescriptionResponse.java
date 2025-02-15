package com.p1.nomnom.ai.dto.response;

import java.util.UUID;

public class GeneratedDescriptionResponse {
    private UUID id;
    private String generatedDescription;

    public GeneratedDescriptionResponse(UUID id, String generatedDescription) {
        this.id = id;
        this.generatedDescription = generatedDescription;
    }

    public UUID getId() {
        return id;
    }

    public String getGeneratedDescription() {
        return generatedDescription;
    }
}
