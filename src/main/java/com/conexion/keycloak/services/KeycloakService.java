package com.conexion.keycloak.services;

import com.conexion.keycloak.dto.UserRequestDTO;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
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
        user.setEmailVerified(true);

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(userRequestDTO.getPassword());
        credentials.setTemporary(false);

        user.setCredentials(Collections.singletonList(credentials));



        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() == 201)
                log.info("User created successfully");

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

}
