package com.abranlezama.fullstackbookreviewer.initializer;

import com.abranlezama.fullstackbookreviewer.stubs.OpenLibraryStubs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

//@Order(Ordered.LOWEST_PRECEDENCE - 100)
@Slf4j
public class DefaultBookStubsInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    log.info("About to mock the HTTP calls for the books that get initialized during startup");

    OpenLibraryStubs openLibraryStubs = applicationContext.getBeanFactory().getBean(OpenLibraryStubs.class);

    openLibraryStubs.stubForSuccessfulBookResponse("9780321751041", getValidResponse("9780321751041"));
    openLibraryStubs.stubForSuccessfulBookResponse("9780321160768", getValidResponse("9780321160768"));
    openLibraryStubs.stubForSuccessfulBookResponse("9780596004651", getValidResponse("9780596004651"));
  }

  private String getValidResponse(String isbn) {
    try {
      return new String(DefaultBookStubsInitializer.class
        .getClassLoader()
        .getResourceAsStream("stubs/openlibrary/success-" + isbn + ".json")
        .readAllBytes());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to stub OpenLibrary responses");
    }
  }
}
