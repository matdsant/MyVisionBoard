package com.myvisionboard.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}