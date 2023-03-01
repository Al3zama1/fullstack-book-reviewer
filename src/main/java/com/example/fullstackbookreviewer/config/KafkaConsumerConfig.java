package com.example.fullstackbookreviewer.config;

import com.example.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${messaging.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, BookSynchronization> bookSynchronizationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
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
}
