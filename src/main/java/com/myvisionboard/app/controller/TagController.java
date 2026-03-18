package com.myvisionboard.app.controller;

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

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Gerenciamento de tags para categorizar notas")
@SecurityRequirement(name = "bearer-jwt")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(
            summary = "Listar todas as tags",
            description = "Recupera a lista completa de tags disponíveis no sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tags recuperada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<Tag>> findAll() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter tag pelo ID",
            description = "Recupera uma tag específica pelo seu identificador"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag encontrada"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Tag> findById(
            @Parameter(description = "ID da tag", required = true)
            @PathVariable String id) {
        return tagService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Criar nova tag",
            description = "Cria uma nova tag no sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tag com este nome já existe"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Tag> create(@RequestBody Tag tag) {
        if (tagService.existsByName(tag.getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tagService.save(tag));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar tag",
            description = "Atualiza o nome de uma tag existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Tag> update(
            @Parameter(description = "ID da tag", required = true)
            @PathVariable String id,
            @RequestBody Tag tag) {
        return tagService.findById(id)
                .map(existing -> {
                    existing.setName(tag.getName());
                    return ResponseEntity.ok(tagService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar tag",
            description = "Remove uma tag do sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tag deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tag não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> delete(
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
