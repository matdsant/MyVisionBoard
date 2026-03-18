package com.myvisionboard.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do usuário", example = "João Silva")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email único do usuário", example = "joao@example.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Senha do usuário (mínimo 6 caracteres)", example = "senha123456")
    private String password;
}