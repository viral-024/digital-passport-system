package com.passport.system.controller;

import com.passport.system.dto.ResponseDTO;
import com.passport.system.entity.Document;
import com.passport.system.entity.DocumentType;
import com.passport.system.entity.Role;
import com.passport.system.entity.User;
import com.passport.system.repository.ApplicationRepository;
import com.passport.system.repository.DocumentRepository;
import com.passport.system.security.CurrentUserService;
import com.passport.system.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;
    private final CurrentUserService currentUserService;

    public DocumentController(DocumentService documentService,
                              DocumentRepository documentRepository,
                              ApplicationRepository applicationRepository,
                              CurrentUserService currentUserService) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ResponseDTO add(@RequestBody Document document,
                           @RequestParam Long appId,
                           Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        if (currentUser.getRole() == Role.CITIZEN &&
                !applicationRepository.existsByIdAndUserId(appId, currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only add documents to your own application");
        }

        return new ResponseDTO("Document added",
                documentService.addDocument(appId, document));
    }

    @GetMapping("/application/{id}")
    public ResponseDTO getByApplication(@PathVariable Long id,
                                        Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        List<Document> documents = currentUser.getRole() == Role.CITIZEN
                ? documentRepository.findByApplicationIdAndApplicationUserId(id, currentUser.getId())
                : documentRepository.findByApplicationId(id);

        if (currentUser.getRole() == Role.CITIZEN &&
                !applicationRepository.existsByIdAndUserId(id, currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own application documents");
        }

        return new ResponseDTO(
            "Documents",
            documents
        );
    }

    @DeleteMapping("/{id}")
    public ResponseDTO delete(@PathVariable Long id,
                              Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        Document document = currentUser.getRole() == Role.CITIZEN
                ? documentRepository.findByIdAndApplicationUserId(id, currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own document"))
                : documentRepository.findById(id).orElseThrow();

        documentRepository.delete(document);
        return new ResponseDTO("Deleted", null);
    }

    @GetMapping("/check-complete/{userId}")
    public ResponseDTO checkDocumentsComplete(@PathVariable Long userId,
                                              Authentication authentication) {
        User currentUser = currentUserService.getCurrentUser(authentication);
        currentUserService.requireAdminOfficerOrOwner(currentUser, userId);

        List<Document> documents = documentRepository.findByApplicationUserId(userId);

        Set<DocumentType> uploadedTypes = documents.stream()
                .map(Document::getDocumentType)
                .collect(Collectors.toSet());

        Set<DocumentType> requiredTypes = Set.of(
                DocumentType.AADHAR,
                DocumentType.PAN,
                DocumentType.PHOTO,
                DocumentType.ADDRESS_PROOF
        );

        boolean isComplete = uploadedTypes.containsAll(requiredTypes);

        return new ResponseDTO(
                isComplete ? "All documents uploaded" : "Some documents missing",
                uploadedTypes
        );
    }
}
