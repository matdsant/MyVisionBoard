package com.myvisionboard.app.controller;

import com.myvisionboard.app.model.Tag;
import com.myvisionboard.app.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Gerenciamento de tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Lista todas as tags")
    public ResponseEntity<List<Tag>> findAll() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma tag pelo id")
    public ResponseEntity<Tag> findById(@PathVariable String id) {
        return tagService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cria uma nova tag")
    public ResponseEntity<Tag> create(@RequestBody Tag tag) {
        if (tagService.existsByName(tag.getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tagService.save(tag));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Renomeia uma tag pelo id")
    public ResponseEntity<Tag> update(@PathVariable String id, @RequestBody Tag tag) {
        return tagService.findById(id)
                .map(existing -> {
                    existing.setName(tag.getName());
                    return ResponseEntity.ok(tagService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Apaga uma tag pelo id")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return tagService.findById(id)
                .map(existing -> {
                    tagService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
