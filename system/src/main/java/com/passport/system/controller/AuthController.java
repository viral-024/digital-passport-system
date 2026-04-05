package com.passport.system.controller;

import com.passport.system.dto.LoginDTO;
import com.passport.system.dto.ResponseDTO;
import com.passport.system.entity.User;
import com.passport.system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseDTO register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return new ResponseDTO("User registered successfully",
                    userService.buildUserResponse(savedUser));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseDTO login(@RequestBody LoginDTO loginDTO) {
        try {
            return new ResponseDTO("Login successful",
                    userService.loginUser(loginDTO.getEmail(), loginDTO.getPassword()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }
}
