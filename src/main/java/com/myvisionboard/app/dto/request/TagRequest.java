package com.myvisionboard.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {

    @NotBlank(message = "Name is required")
    private String name;
}