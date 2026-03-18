package com.myvisionboard.app.controller;

import com.myvisionboard.app.model.Note;
import com.myvisionboard.app.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gerenciamento de notas do usuário")
@SecurityRequirement(name = "bearer-jwt")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    @Operation(
            summary = "Listar notas",
            description = "Lista as notas do usuário com paginação e filtro por título"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notas recuperadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<Page<Note>> findAll(
            @Parameter(description = "Filtrar por título da nota")
            @RequestParam(required = false) String title,
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam String userId,
            Pageable pageable) {

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(noteService.findByUserIdAndTitle(userId, title, pageable));
        }
        return ResponseEntity.ok(noteService.findByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obter nota pelo ID",
            description = "Recupera uma nota específica pelo seu identificador único"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Note> findById(
            @Parameter(description = "ID da nota", required = true)
            @PathVariable String id) {
        return noteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Criar nova nota",
            description = "Cria uma nova nota associada ao usuário autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Note> create(@RequestBody Note note) {
        return ResponseEntity.ok(noteService.save(note));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar nota",
            description = "Atualiza os dados de uma nota existente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Note> update(
            @Parameter(description = "ID da nota", required = true)
            @PathVariable String id,
            @RequestBody Note note) {
        return noteService.findById(id)
                .map(existing -> {
                    existing.setTitle(note.getTitle());
                    existing.setContent(note.getContent());
                    existing.setTags(note.getTags());
                    return ResponseEntity.ok(noteService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar nota",
            description = "Remove uma nota do sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Nota deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da nota", required = true)
            @PathVariable String id) {
        return noteService.findById(id)
                .map(existing -> {
                    noteService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
