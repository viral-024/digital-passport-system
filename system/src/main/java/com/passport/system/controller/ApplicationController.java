package com.passport.system.controller;

import com.passport.system.dto.ApplicationRequestDTO;
import com.passport.system.dto.ResponseDTO;
import com.passport.system.entity.PassportApplication;
import com.passport.system.entity.Role;
import com.passport.system.entity.User;
import com.passport.system.repository.ApplicationRepository;
import com.passport.system.service.ApplicationService;
import com.passport.system.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;
    private final CurrentUserService currentUserService;

    public ApplicationController(ApplicationService applicationService,
                                 ApplicationRepository applicationRepository,
                                 CurrentUserService currentUserService) {
        this.applicationService = applicationService;
        this.applicationRepository = applicationRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseDTO create(@RequestBody ApplicationRequestDTO dto,
                              Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        Long userId = currentUser.getRole() == Role.ADMIN ? dto.getUserId() : currentUser.getId();

        PassportApplication app = new PassportApplication();
        app.setFullName(dto.getFullName());
        app.setDob(dto.getDob());
        app.setAddress(dto.getAddress());

        PassportApplication saved = applicationService.createApplication(userId, app);

        return new ResponseDTO("Application created", saved);
    }

    @GetMapping
    public ResponseDTO getAll(Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        List<PassportApplication> list = currentUser.getRole() == Role.CITIZEN
                ? applicationRepository.findByUserId(currentUser.getId())
                : applicationRepository.findAll();
        return new ResponseDTO("All applications", list);
    }

    @GetMapping("/{id}")
    public ResponseDTO getById(@PathVariable Long id,
                               Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        PassportApplication application = currentUser.getRole() == Role.CITIZEN
                ? applicationRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own application"))
                : applicationRepository.findById(id).orElseThrow();

        return new ResponseDTO("Application found",
                application);
    }

    @PutMapping("/{id}")
    public ResponseDTO update(@PathVariable Long id,
                              @RequestBody PassportApplication app,
                              Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        PassportApplication existing = currentUser.getRole() == Role.CITIZEN
                ? applicationRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own application"))
                : applicationRepository.findById(id).orElseThrow();

        app.setId(id);
        app.setUser(existing.getUser());
        app.setCreatedAt(existing.getCreatedAt());
        if (app.getStatus() == null) {
            app.setStatus(existing.getStatus());
        }

        return new ResponseDTO("Updated",
                applicationRepository.save(app));
    }

    @DeleteMapping("/{id}")
    public ResponseDTO delete(@PathVariable Long id,
                              Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        PassportApplication application = currentUser.getRole() == Role.CITIZEN
                ? applicationRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own application"))
                : applicationRepository.findById(id).orElseThrow();

        applicationRepository.delete(application);
        return new ResponseDTO("Deleted", null);
    }
}
