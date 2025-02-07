package com.conexion.keycloak.dto;

import lombok.Data;

@Data
public class UserRequestDTO {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;

}
