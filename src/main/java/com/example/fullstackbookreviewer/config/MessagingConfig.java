package com.example.fullstackbookreviewer.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    private static final AWSStaticCredentialsProvider CREDENTIALS =
            new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "bar"));

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder.standard()
                .withCredentials(CREDENTIALS)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "http://localhost:9324", "us-west-1"
                )).build();
    }

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(CREDENTIALS)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "http://localhost:9324", "us-west-1"
                )).build();
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }
}
