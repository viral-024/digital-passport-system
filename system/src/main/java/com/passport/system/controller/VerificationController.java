package com.passport.system.controller;

import com.passport.system.dto.ResponseDTO;
import com.passport.system.entity.Role;
import com.passport.system.entity.User;
import com.passport.system.entity.Verification;
import com.passport.system.repository.VerificationRepository;
import com.passport.system.security.CurrentUserService;
import com.passport.system.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/verifications")
public class VerificationController {

    private final VerificationService verificationService;
    private final VerificationRepository verificationRepository;
    private final CurrentUserService currentUserService;

    public VerificationController(VerificationService verificationService,
                                  VerificationRepository verificationRepository,
                                  CurrentUserService currentUserService) {
        this.verificationService = verificationService;
        this.verificationRepository = verificationRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseDTO create(@RequestBody Verification verification,
                              @RequestParam Long appId,
                              @RequestParam Long officerId,
                              Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        Long effectiveOfficerId = currentUser.getRole() == Role.ADMIN ? officerId : currentUser.getId();

        return new ResponseDTO("Verified",
                verificationService.verifyApplication(appId, effectiveOfficerId, verification));
    }

   @PutMapping("/{id}")
    public ResponseDTO update(@PathVariable Long id,
                            @RequestBody Verification verification,
                            Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);

        Verification existing;
        if (currentUser.getRole() == Role.ADMIN) {
            existing = verificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Verification not found"));
        } else {
            existing = verificationRepository.findByIdAndOfficerId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own verification"));
        }

        existing.setStatus(verification.getStatus());
        existing.setRemarks(verification.getRemarks());

        return new ResponseDTO("Updated", verificationRepository.save(existing));
    }

    @GetMapping
    public ResponseDTO getAll(Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        List<Verification> list;
        if (currentUser.getRole() == Role.CITIZEN) {
            list = verificationRepository.findByApplicationUserId(currentUser.getId());
        } else if (currentUser.getRole() == Role.OFFICER) {
            list = verificationRepository.findByOfficerId(currentUser.getId());
        } else {
            list = verificationRepository.findAll();
        }

        return new ResponseDTO("All verifications", list);
    }

    @GetMapping("/{id}")
    public ResponseDTO getById(@PathVariable Long id,
                               Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        Verification verification;
        if (currentUser.getRole() == Role.CITIZEN) {
            verification = verificationRepository.findByIdAndApplicationUserId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own verification"));
        } else if (currentUser.getRole() == Role.OFFICER) {
            verification = verificationRepository.findByIdAndOfficerId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own verification"));
        } else {
            verification = verificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Verification not found"));
        }

        return new ResponseDTO("Verification found", verification);
    }
}
