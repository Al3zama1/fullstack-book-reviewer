package com.example.fullstackbookreviewer.controller;

import com.example.fullstackbookreviewer.mapstruct.dto.BookResponse;
import com.example.fullstackbookreviewer.service.book.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
