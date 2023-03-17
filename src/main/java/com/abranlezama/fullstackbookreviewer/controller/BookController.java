package com.abranlezama.fullstackbookreviewer.controller;

import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookResponse;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.NewBookRequest;
import com.abranlezama.fullstackbookreviewer.service.book.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final IBookService bookService;

    @GetMapping
    public List<BookResponse> getAvailableBooks(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        return bookService.getAllBooks(page, pageSize);
    }

    @PostMapping
    public ResponseEntity<Void> addNewBook(@RequestBody @Valid NewBookRequest newBookRequest) {
        bookService.createNewBook(newBookRequest.getIsbn());

        return ResponseEntity.status(202).build();
    }

}
