package com.passport.system.security;

import com.passport.system.entity.Role;
import com.passport.system.entity.User;
import com.passport.system.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Authentication required");
        }

        return userRepository.findByUsername(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
    }

    public boolean hasRole(User user, Role role) {
        return user.getRole() == role;
    }

    public void requireAdminOrOwner(User currentUser, Long ownerId) {
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(ownerId)) {
            throw new ResponseStatusException(FORBIDDEN, "You can only access your own data");
        }
    }

    public void requireAdminOfficerOrOwner(User currentUser, Long ownerId) {
        if (currentUser.getRole() != Role.ADMIN &&
                currentUser.getRole() != Role.OFFICER &&
                !currentUser.getId().equals(ownerId)) {
            throw new ResponseStatusException(FORBIDDEN, "You are not allowed to access this data");
        }
    }
}
