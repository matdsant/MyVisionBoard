package com.myvisionboard.app.controller;

import com.myvisionboard.app.dto.request.TagRequest;
import com.myvisionboard.app.dto.response.TagResponse;
import com.myvisionboard.app.model.Tag;
import com.myvisionboard.app.model.User;
import com.myvisionboard.app.service.TagService;
import com.myvisionboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Gerenciamento de tags para categorizar notas")
@SecurityRequirement(name = "Bearer Authentication")
public class TagController {

    private final TagService tagService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar todas as tags")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tags recuperada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<List<TagResponse>> findAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<TagResponse> tags = tagService.findAll()
                .stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter tag pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag encontrada"),
            @ApiResponse(responseCode = "404", description = "Tag nao encontrada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<TagResponse> findById(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID da tag", required = true)
            @PathVariable String id) {
        return tagService.findById(id)
                .map(tag -> ResponseEntity.ok(new TagResponse(tag.getId(), tag.getName())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tag com este nome ja existe"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<TagResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
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
    @Operation(summary = "Atualizar tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag nao encontrada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<TagResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID da tag", required = true)
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
    @Operation(summary = "Deletar tag")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag nao encontrada"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID da tag", required = true)
            @PathVariable String id) {
        return tagService.findById(id)
                .map(existing -> {
                    tagService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
