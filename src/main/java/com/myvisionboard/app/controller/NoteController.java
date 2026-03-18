package com.myvisionboard.app.controller;

import com.myvisionboard.app.model.Note;
import com.myvisionboard.app.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Gerenciamento de notas")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    @Operation(summary = "Lista notas com paginacao e filtro")
    public ResponseEntity<Page<Note>> findAll(
            @RequestParam(required = false) String title,
            @RequestParam String userId,
            Pageable pageable) {

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(noteService.findByUserIdAndTitle(userId, title, pageable));
        }
        return ResponseEntity.ok(noteService.findByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma nota pelo id")
    public ResponseEntity<Note> findById(@PathVariable String id) {
        return noteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cria uma nova nota")
    public ResponseEntity<Note> create(@RequestBody Note note) {
        return ResponseEntity.ok(noteService.save(note));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma nota pelo id")
    public ResponseEntity<Note> update(@PathVariable String id, @RequestBody Note note) {
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
    @Operation(summary = "Apaga uma nota pelo id")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return noteService.findById(id)
                .map(existing -> {
                    noteService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
