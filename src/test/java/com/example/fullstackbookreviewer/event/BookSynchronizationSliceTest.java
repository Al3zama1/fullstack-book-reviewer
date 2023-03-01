package com.example.fullstackbookreviewer.event;

import com.example.fullstackbookreviewer.client.OpenLibraryApiClient;
import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.example.fullstackbookreviewer.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(SpringExtension.class)
@Import(value = {BookSynchronizationListener.class})
@Slf4j
@DirtiesContext

public class BookSynchronizationSliceTest {


    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"))
            .withReuse(true)
            .withLogConsumer(new Slf4jLogConsumer(log));

    private static final String ISBN = "9780596004651";

    static {
        kafka.start();
    }

    @TestConfiguration
    @EnableKafka
    static class testConfig {

        @Bean
        public ConsumerFactory<String, BookSynchronization> bookSynchronizationConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroup");
            return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(BookSynchronization.class));
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, BookSynchronization> userKafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, BookSynchronization> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(bookSynchronizationConsumerFactory());
            return factory;
        }

        @Bean
        public ProducerFactory<String, BookSynchronization> bookSynchronizationProducerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, BookSynchronization> kafkaTemplate() {
            return new KafkaTemplate<>(bookSynchronizationProducerFactory());
        }

        @Bean
        public NewTopic topic1() {
            return TopicBuilder.name("book-synchronization").build();
        }

        @Bean
        KafkaAdmin admin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
            return new KafkaAdmin(configs);
        }

    }

    @Autowired
    private BookSynchronizationListener cut;
    @Autowired
    private KafkaTemplate<String, BookSynchronization> kafkaTemplate;

    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private OpenLibraryApiClient openLibraryApiClient;

    @Test
    void shouldExecuteMethodWhenABookSynchronizationEvenIsReceived() {
        // Given
        given(bookRepository.findByIsbn(ISBN)).willReturn(Optional.of(new Book()));

        // When
        kafkaTemplate.send("book-synchronization", new BookSynchronization(ISBN));

        // Then
        Awaitility.given()
                .await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> then(bookRepository).should().findByIsbn(ISBN));

    }

}
