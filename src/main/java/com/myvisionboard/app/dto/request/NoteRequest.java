package com.myvisionboard.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class NoteRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    private Set<String> tagIds;
}