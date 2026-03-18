package com.myvisionboard.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class NoteResponse {

    private String id;
    private String title;
    private String content;
    private Set<TagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}