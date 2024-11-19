package com.coursemanagement.service;

import com.coursemanagement.config.JwtTokenExtractor;
import com.coursemanagement.entity.User;
import com.coursemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final UserRepository userRepository;

    @Transactional
    public void createOrUpdateUserFromKeycloak(JwtTokenExtractor.TokenInfo tokenInfo) {
        userRepository.findByEmail(tokenInfo.email())
                .ifPresentOrElse(
                        user -> updateExistingUser(user, tokenInfo),
                        () -> createNewUser(tokenInfo)
                );
    }

    private void updateExistingUser(User user, JwtTokenExtractor.TokenInfo tokenInfo) {
        if (!user.getRole().equals(tokenInfo.role())) {
            log.info("Updating role for user with email: {}", tokenInfo.email());
            user.setRole(tokenInfo.role());
            user.setUpdatedAt(ZonedDateTime.now());
            userRepository.save(user);
        }
    }

    private void createNewUser(JwtTokenExtractor.TokenInfo tokenInfo) {
        log.info("Creating new user with email: {}", tokenInfo.email());

        String[] nameParts = splitFullName(tokenInfo.name());
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        User newUser = User.builder()
                .email(tokenInfo.email())
                .firstName(firstName)
                .lastName(lastName)
                .role(tokenInfo.role())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        userRepository.save(newUser);
    }

    private String[] splitFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"Unknown", ""};
        }

        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        return new String[]{firstName, lastName};
    }
}
