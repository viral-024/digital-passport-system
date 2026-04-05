package com.passport.system.service;

import com.passport.system.entity.*;
import com.passport.system.repository.*;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public VerificationService(VerificationRepository verificationRepository,
                               ApplicationRepository applicationRepository,
                               UserRepository userRepository) {
        this.verificationRepository = verificationRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public Verification verifyApplication(Long appId, Long officerId, Verification verification) {

        PassportApplication app = applicationRepository.findById(appId).orElseThrow();
        User officer = userRepository.findById(officerId).orElseThrow();

        verification.setApplication(app);
        verification.setOfficer(officer);

        if (verification.getStatus() == VerificationStatus.VERIFIED) {
            app.setStatus("APPROVED");
        } else {
            app.setStatus("REJECTED");
        }

        applicationRepository.save(app);

        return verificationRepository.save(verification);
    }
}