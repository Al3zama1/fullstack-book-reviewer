package com.example.fullstackbookreviewer.service.user;

import com.example.fullstackbookreviewer.entity.User;
import com.example.fullstackbookreviewer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final Clock clock;
    @Override
    public User getOrCreateUser(String name, String email) {
        Optional<User> userOptional = userRepository.findByNameAndEmail(name, email);

        if (userOptional.isPresent()) return userOptional.get();

        User user = User.builder()
                .name(name)
                .email(email)
                .createdAt(LocalDateTime.now(clock))
                .build();

        return userRepository.save(user);
    }
}
