package com.conexion.keycloak.services;

import com.conexion.keycloak.dto.UserRequestDTO;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void createUser(UserRequestDTO userRequestDTO){

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(userRequestDTO.getPassword());
        credentials.setTemporary(false);

        user.setCredentials(Collections.singletonList(credentials));



        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() == 201) {
                log.info("User created successfully");
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                log.info("ID del usuario: {}", userId);

                // Asignar el rol "USER" automáticamente
                assignDefaultRole(userId, "USER");
                sendVerificationEmail(userId);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void sendVerificationEmail(String userId) {
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            // Enviar correo de verificación
            userResource.sendVerifyEmail();
            log.info("Correo de verificación enviado al usuario con ID: {}", userId);
        } catch (Exception e) {
            log.error("Error al enviar el correo de verificación: {}", e.getMessage(), e);
        }
    }

    private void assignDefaultRole(String userId, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

        if (role != null) {
            UserResource userResource = realmResource.users().get(userId);
            userResource.roles().realmLevel().add(Collections.singletonList(role));
            log.info("Rol '{}' asignado automáticamente al usuario con ID: {}", roleName, userId);
        } else {
            log.warn("El rol '{}' no existe en el Realm", roleName);
        }
    }

}
