package com.coursemanagement.service;

import com.coursemanagement.config.JwtTokenExtractor;
import com.coursemanagement.entity.User;
import com.coursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void createOrUpdateUserFromKeycloak(JwtTokenExtractor.UserInfo userInfo) {
        userRepository.findByEmail(userInfo.email())
                .ifPresentOrElse(
                        user -> updateExistingUser(user, userInfo),
                        () -> createNewUser(userInfo)
                );
    }

    private void updateExistingUser(User user, JwtTokenExtractor.UserInfo userInfo) {
        if (!user.getRole().equals(userInfo.role())) {
            log.info("Updating role for user with email: {}", userInfo.email());
            user.setRole(userInfo.role());
            userRepository.save(user);
        }
    }

    private void createNewUser(JwtTokenExtractor.UserInfo userInfo) {
        log.info("Creating new user with email: {}", userInfo.email());

        String[] nameParts = splitFullName(userInfo.name());

        User newUser = User.builder()
                .keycloakId(userInfo.keycloakId())
                .email(userInfo.email())
                .firstName(nameParts[0])
                .lastName(nameParts[1])
                .role(userInfo.role())
                .build();

        userRepository.save(newUser);
    }

    private String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Unknown", ""};
        }

        String[] nameParts = fullName.split(" ", 2);
        return new String[]{
                nameParts[0],
                nameParts.length > 1 ? nameParts[1] : ""
        };
    }
}
