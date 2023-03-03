package com.example.fullstackbookreviewer;

import com.example.fullstackbookreviewer.initializer.RSAKeyGenerator;
import com.example.fullstackbookreviewer.initializer.WireMockInitializer;
import com.example.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.example.fullstackbookreviewer.repository.BookRepository;
import com.example.fullstackbookreviewer.repository.ReviewRepository;
import com.example.fullstackbookreviewer.stubs.OAuth2Stubs;
import com.example.fullstackbookreviewer.stubs.OpenLibraryStubs;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveProfiles("integration-test")
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public abstract class AbstractIntegrationTest {

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("book_reviewer")
            .withUsername("test")
            .withPassword("test");

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
            .withReuse(true)
            .withLogConsumer(new Slf4jLogConsumer(log));

    static {
        kafka.start();
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @TestConfiguration
    static class testConfig {

        @Bean
        public ProducerFactory<String, BookSynchronization> bookSynchronizationProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }


        @Bean
        public ConsumerFactory<String, BookSynchronization> bookSynchronizationConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup");
            return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(BookSynchronization.class));
        }

    }

    @Autowired
    private RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    private OAuth2Stubs oAuth2Stubs;
    @Autowired
    protected OpenLibraryStubs openLibraryStubs;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void init() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
    }

    protected String getSignedJWT(String username, String email) throws JOSEException {
        return createJWT(username, email);
    }

    protected String getSignedJWT() throws JOSEException {
        return createJWT("duke", "duke@spring.io");
    }

    private String createJWT(String username, String email) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(RSAKeyGenerator.KEY_ID)
                .build();

        JWTClaimsSet payload = new JWTClaimsSet.Builder()
                .issuer(oAuth2Stubs.getIssuerUri())
                .audience("account")
                .subject(username)
                .claim("preferred_username", username)
                .claim("email", email)
                .claim("scope", "openid email profile")
                .claim("azp", "react-client")
                .claim("realm_access", Map.of("roles", List.of()))
                .expirationTime(Date.from(Instant.now().plusSeconds(120)))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(header, payload);
        signedJWT.sign(new RSASSASigner(rsaKeyGenerator.getPrivateKey()));
        return signedJWT.serialize();
    }

}
