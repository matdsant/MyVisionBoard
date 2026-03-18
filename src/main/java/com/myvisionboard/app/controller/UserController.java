package com.myvisionboard.app.controller;

import com.myvisionboard.app.model.User;
import com.myvisionboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Gerenciamento do perfil do usuário autenticado")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Obter perfil do usuário",
            description = "Retorna as informações do perfil do usuário autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil recuperado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<User> me(
            @Parameter(description = "Email do usuário autenticado", required = true)
            @RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(
            summary = "Atualizar perfil",
            description = "Atualiza os dados do perfil do usuário (nome e/ou senha)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<User> update(
            @Parameter(description = "Email do usuário autenticado", required = true)
            @RequestParam String email,
            @RequestBody User user) {
        return userService.findByEmail(email)
                .map(existing -> {
                    existing.setName(user.getName());
                    return ResponseEntity.ok(userService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "Deletar conta",
            description = "Remove a conta do usuário e todos os seus dados associados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Email do usuário autenticado", required = true)
            @RequestParam String email) {
        return userService.findByEmail(email)
                .map(existing -> {
                    userService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
