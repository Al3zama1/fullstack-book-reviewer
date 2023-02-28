package com.example.fullstackbookreviewer.controller;

import com.example.fullstackbookreviewer.config.WebSecurityConfig;
import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.mapstruct.mapper.BookMapper;
import com.example.fullstackbookreviewer.mapstruct.mapper.BookMapperImpl;
import com.example.fullstackbookreviewer.service.IBookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(WebSecurityConfig.class)
class BookControllerTest {
    @MockBean
    private IBookService bookService;

    @Autowired
    private MockMvc mockMvc;

    private static BookMapper bookMapper;

    @BeforeAll
    static void setUp() {
        bookMapper = new BookMapperImpl();
    }

    @Test
    void shouldGetEmptyArrayWhenNoBooksExists() throws Exception {
        this.mockMvc.perform(get("/api/v1/books")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(0)));
    }

    @Test
    void shouldNotReturnXML() throws Exception {
        this.mockMvc.perform(get("/api/v1/books")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldGetBooksWhenServiceReturnsBooks() throws Exception {
        // Given
        Book bookOne = createBook(1L, "42", "Java 14", "Mike", "Good Book",
                "Software Engineering", 200L, "Oracle", "ftp://localhost:42");
        Book bookTwo = createBook(2L, "42", "Java 15", "Duke", "Good Book",
                "Software Engineering", 200L, "Oracle", "ftp://localhost:42");

        given(bookService.getAllBooks()).willReturn(List.of(bookMapper.mapBookToDto(bookOne), bookMapper.mapBookToDto(bookTwo)));

        // When
        this.mockMvc.perform(get("/api/v1/books")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].isbn", is("42")))
                .andExpect(jsonPath("$[0].title", is("Java 14")))
                .andExpect(jsonPath("$[0].author", is("Mike")))
                .andExpect(jsonPath("$[0].description", is("Good Book")))
                .andExpect(jsonPath("$[0].genre", is("Software Engineering")))
                .andExpect(jsonPath("$[0].pages", is(200)))
                .andExpect(jsonPath("$[0].publisher", is("Oracle")))
                .andExpect(jsonPath("$[0].thumbnailUrl", is("ftp://localhost:42")))
                .andExpect(jsonPath("$[1].id").doesNotExist());
    }

    private Book createBook(Long id, String isbn, String title, String author, String description, String genre, Long pages, String publisher, String thumbnailUrl) {
        Book result = new Book();
        result.setId(id);
        result.setIsbn(isbn);
        result.setTitle(title);
        result.setAuthor(author);
        result.setDescription(description);
        result.setGenre(genre);
        result.setPages(pages);
        result.setPublisher(publisher);
        result.setThumbnailUrl(thumbnailUrl);
        return result;
    }

}