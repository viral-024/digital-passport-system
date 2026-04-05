package com.passport.system.service;

import com.passport.system.entity.Document;
import com.passport.system.entity.PassportApplication;
import com.passport.system.repository.DocumentRepository;
import com.passport.system.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApplicationRepository applicationRepository;

    public DocumentService(DocumentRepository documentRepository,
                           ApplicationRepository applicationRepository) {
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
    }

    public Document addDocument(Long appId, Document document) {
        PassportApplication app = applicationRepository.findById(appId).orElseThrow();
        document.setApplication(app);
        return documentRepository.save(document);
    }
}