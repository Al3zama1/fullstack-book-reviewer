package com.abranlezama.fullstackbookreviewer.service.user;

import com.abranlezama.fullstackbookreviewer.entity.User;
import com.abranlezama.fullstackbookreviewer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private Clock clock;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService cut;

    @Test
    void shouldIncludeCurrentDateTimeWhenCreatingNewUser() {
        // Given
        given(userRepository.findByNameAndEmail("duke", "duke@spring.io")).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        LocalDateTime defaultLocalDateTime = LocalDateTime.of(2022, 12, 13, 12, 15);
        Clock fixedClock = Clock.fixed(defaultLocalDateTime.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());

        // When
        User result = cut.getOrCreateUser("duke", "duke@spring.io");

        assertThat(result.getCreatedAt()).isEqualTo(defaultLocalDateTime);
    }
}