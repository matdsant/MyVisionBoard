package com.myvisionboard.app.controller;

import com.myvisionboard.app.dto.request.UserRequest;
import com.myvisionboard.app.dto.response.UserResponse;
import com.myvisionboard.app.model.User;
import com.myvisionboard.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Authenticated user profile management")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<UserResponse> me(
            @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserRequest request) {
        return userService.findByEmail(userDetails.getUsername())
                .map(existing -> {
                    existing.setName(request.getName());
                    User saved = userService.save(existing);
                    return ResponseEntity.ok(new UserResponse(
                            saved.getId(),
                            saved.getName(),
                            saved.getEmail(),
                            saved.getCreatedAt()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .map(existing -> {
                    userService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
