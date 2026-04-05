package com.passport.system.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginDTO {

    private String email;
    private String password;

}