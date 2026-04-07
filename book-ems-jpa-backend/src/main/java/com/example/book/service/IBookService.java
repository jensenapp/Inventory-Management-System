package com.example.book.service;


import com.example.book.dto.BookDto;

import java.util.List;

public interface IBookService {
    List<BookDto> getAllBooks();
    BookDto updateBooks(Long bookId, BookDto bookDto);
    void deleteBook(Long bookId);
    BookDto createBook(BookDto bookDto);
    List<BookDto> searchBook(String searchTerm);
}
