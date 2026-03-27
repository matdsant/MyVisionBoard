package com.myvisionboard.app.controller;

import com.myvisionboard.app.dto.request.NoteRequest;
import com.myvisionboard.app.dto.response.MessageResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "User note management")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "List notes")
    public ResponseEntity<Page<Note>> findAll(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String title,
            Pageable pageable) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found."));
        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(noteService.findByUserIdAndTitle(user.getId(), title, pageable));
        }
        return ResponseEntity.ok(noteService.findByUserId(user.getId(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by ID")
    public ResponseEntity<?> findById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found."));
        return noteService.findById(id)
                .map(note -> {
                    if (!note.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new MessageResponse("Access denied."));
                    }
                    return ResponseEntity.ok(note);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Note not found.")));
    }

    @PostMapping
    @Operation(summary = "Create new note")
    public ResponseEntity<NoteResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody NoteRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found."));
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
                new HashSet<>(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestBody NoteRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found."));
        return noteService.findById(id)
                .map(existing -> {
                    if (!existing.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new MessageResponse("Access denied."));
                    }
                    existing.setTitle(request.getTitle());
                    existing.setContent(request.getContent());
                    Note saved = noteService.save(existing);
                    return ResponseEntity.ok(new NoteResponse(
                            saved.getId(),
                            saved.getTitle(),
                            saved.getContent(),
                            new HashSet<>(),
                            saved.getCreatedAt(),
                            saved.getUpdatedAt()
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Note not found.")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note")
    public ResponseEntity<MessageResponse> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found."));
        return noteService.findById(id)
                .map(existing -> {
                    if (!existing.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new MessageResponse("Access denied."));
                    }
                    noteService.deleteById(id);
                    return ResponseEntity.ok(new MessageResponse("Note deleted successfully."));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Note not found.")));
    }
}
