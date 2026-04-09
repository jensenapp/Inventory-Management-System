package com.example.book.service;


import com.example.book.dto.BookDto;
import com.example.book.dto.PageResponseDto;

import java.util.List;

public interface IBookService {
    PageResponseDto<BookDto> getAllBooks(int pageNo, int pageSize, String sortBy, String sortDir);
    BookDto getBookById(Long bookId);
    BookDto updateBooks(Long bookId, BookDto bookDto);
    void deleteBook(Long bookId);
    BookDto createBook(BookDto bookDto);
    PageResponseDto<BookDto> searchBook(String searchTerm, int pageNo, int pageSize, String sortBy, String sortDir);}
