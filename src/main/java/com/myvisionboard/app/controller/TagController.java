package com.myvisionboard.app.controller;

import com.myvisionboard.app.dto.request.TagRequest;
import com.myvisionboard.app.dto.response.TagResponse;
import com.myvisionboard.app.dto.response.MessageResponse;
import com.myvisionboard.app.model.Tag;
import com.myvisionboard.app.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Tag management for categorizing notes")
@SecurityRequirement(name = "Bearer Authentication")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "List all tags")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag list retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<TagResponse>> findAll() {
        List<TagResponse> tags = tagService.findAll()
                .stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag found"),
            @ApiResponse(responseCode = "404", description = "Tag not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TagResponse> findById(
            @Parameter(description = "Tag ID", required = true)
            @PathVariable String id) {
        return tagService.findById(id)
                .map(tag -> ResponseEntity.ok(new TagResponse(tag.getId(), tag.getName())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag created successfully"),
            @ApiResponse(responseCode = "400", description = "Tag with this name already exists"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TagResponse> create(
            @RequestBody TagRequest request) {
        if (tagService.existsByName(request.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Tag tag = Tag.builder()
                .name(request.getName())
                .build();
        Tag saved = tagService.save(tag);
        return ResponseEntity.ok(new TagResponse(saved.getId(), saved.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag updated successfully"),
            @ApiResponse(responseCode = "404", description = "Tag not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<TagResponse> update(
            @Parameter(description = "Tag ID", required = true)
            @PathVariable String id,
            @RequestBody TagRequest request) {
        return tagService.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    Tag saved = tagService.save(existing);
                    return ResponseEntity.ok(new TagResponse(saved.getId(), saved.getName()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Tag not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "Tag ID", required = true)
            @PathVariable String id) {
        return tagService.findById(id)
                .map(existing -> {
                    tagService.deleteById(id);
                    return ResponseEntity.ok(new MessageResponse("Tag deleted successfully."));
                })
                .orElse(ResponseEntity.status(404)
                        .body(new MessageResponse("Tag not found.")));
    }
}
