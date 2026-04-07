package com.example.book.service.impl;


import com.example.book.dto.BookDto;
import com.example.book.entity.Book;
import com.example.book.entity.Publisher;
import com.example.book.mapper.BookMapper;
import com.example.book.repository.BookRepository;
import com.example.book.repository.PublisherRepository;
import com.example.book.service.IBookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements IBookService {

private final BookRepository bookRepository;

private final PublisherRepository publisherRepository;


    @Override
    public List<BookDto> getAllBooks() {
        List<Book> bookList = bookRepository.findAllWithPublisher();
        List<BookDto> bookDtoList = bookList.stream().map(book -> BookMapper.mapToBookDto(book)).toList();
        return bookDtoList;
    }

    @Override
    public BookDto getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("book not found"));
        return BookMapper.mapToBookDto(book);
    }

    @Override
    public BookDto updateBooks(Long bookId, BookDto bookDto) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("book not found"));
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPrice(bookDto.getPrice());
        book.setPublishDate(bookDto.getPublishDate());
        if (bookDto.getPublisherId()!=null){
            Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow(() -> new RuntimeException("Publisher not found"));
            book.setPublisher(publisher);
        }

        Book saveBook = bookRepository.save(book);

        return BookMapper.mapToBookDto(saveBook);
    }


    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = BookMapper.mapToBook(bookDto);
        if (bookDto.getPublisherId()!=null){
            Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow(() -> new RuntimeException("Publisher not found"));
            book.setPublisher(publisher);
        }
        Book saveBook = bookRepository.save(book);
        return BookMapper.mapToBookDto(saveBook);
    }


    @Override
    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }

    @Override
    public List<BookDto> searchBook(String searchTerm) {
        List<Book> bookList = bookRepository.findByTitleOrAuthor(searchTerm);
        List<BookDto> list = bookList.stream().map(book -> BookMapper.mapToBookDto(book)).toList();
        return list;
    }
}
