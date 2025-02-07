package com.conexion.keycloak.controllers;

import com.conexion.keycloak.dto.UserRequestDTO;
import com.conexion.keycloak.services.KeycloakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class KeucloakUserController {

    private final KeycloakService keycloakService;

    public KeucloakUserController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDTO userRequestDTO){
        keycloakService.createUser(userRequestDTO);
        return ResponseEntity.ok("Usuario Registrado!");
    }

}
