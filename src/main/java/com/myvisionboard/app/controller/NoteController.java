package com.myvisionboard.app.controller;

import com.myvisionboard.app.dto.request.NoteRequest;
import com.myvisionboard.app.dto.response.NoteResponse;
import com.myvisionboard.app.model.Note;
import com.myvisionboard.app.model.User;
import com.myvisionboard.app.service.NoteService;
import com.myvisionboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gerenciamento de notas do usuario")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Listar notas")
    public ResponseEntity<Page<Note>> findAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String title,
            Pageable pageable) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(noteService.findByUserIdAndTitle(user.getId(), title, pageable));
        }
        return ResponseEntity.ok(noteService.findByUserId(user.getId(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter nota pelo ID")
    public ResponseEntity<Note> findById(@PathVariable String id) {
        return noteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova nota")
    public ResponseEntity<NoteResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NoteRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
        Note saved = noteService.save(note);
        NoteResponse response = new NoteResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                null,
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota")
    public ResponseEntity<NoteResponse> update(
            @PathVariable String id,
            @RequestBody NoteRequest request) {
        return noteService.findById(id)
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setContent(request.getContent());
                    Note saved = noteService.save(existing);
                    NoteResponse response = new NoteResponse(
                            saved.getId(),
                            saved.getTitle(),
                            saved.getContent(),
                            null,
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar nota")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return noteService.findById(id)
                .map(existing -> {
                    noteService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
