package com.myvisionboard.app.controller;

import com.myvisionboard.app.model.User;
import com.myvisionboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Gerenciamento do perfil do usuário")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Retorna o perfil do usuário autenticado")
    public ResponseEntity<User> me(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza nome ou senha do usuário")
    public ResponseEntity<User> update(@RequestParam String email, @RequestBody User user) {
        return userService.findByEmail(email)
                .map(existing -> {
                    existing.setName(user.getName());
                    return ResponseEntity.ok(userService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deleta a conta do usuário")
    public ResponseEntity<Void> delete(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(existing -> {
                    userService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
