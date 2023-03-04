package com.abranlezama.fullstackbookreviewer.initializer;

import com.abranlezama.fullstackbookreviewer.stubs.OAuth2Stubs;
import com.abranlezama.fullstackbookreviewer.stubs.OpenLibraryStubs;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;

import java.util.Arrays;

@Order(Ordered.LOWEST_PRECEDENCE - 1000)
@Slf4j
public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("About to start the WireMockServer");

        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        wireMockServer.start();

        if (Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains("integration-test")) {

            RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
            rsaKeyGenerator.initializeKeys();

            OAuth2Stubs oAuth2Stubs = new OAuth2Stubs(wireMockServer, rsaKeyGenerator);

            oAuth2Stubs.stubForConfiguration();
            oAuth2Stubs.stubForJWKS();

            applicationContext.getBeanFactory().registerSingleton("oAuth2Stubs",oAuth2Stubs);
            applicationContext.getBeanFactory().registerSingleton("rsaKeyGenerator", rsaKeyGenerator);

            TestPropertyValues
                    .of("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:" + wireMockServer.port() + "/auth/realms/spring")
                    .applyTo(applicationContext);
        }

        OpenLibraryStubs openLibraryStubs = new OpenLibraryStubs(wireMockServer);

        applicationContext.getBeanFactory().registerSingleton("openLibraryStubs", openLibraryStubs);
        applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                log.info("Stopping the WireMockServer");
                wireMockServer.start();
            }
        });

        TestPropertyValues
                .of("clients.open-library.base-url=http://localhost:" + wireMockServer.port() + "/openLibrary")
                .applyTo(applicationContext);
    }
}
