package com.passport.system.service;

import com.passport.system.entity.PassportApplication;
import com.passport.system.entity.User;
import com.passport.system.repository.ApplicationRepository;
import com.passport.system.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public PassportApplication createApplication(Long userId, PassportApplication application) {
        User user = userRepository.findById(userId).orElseThrow();
        application.setUser(user);
        return applicationRepository.save(application);
    }
}