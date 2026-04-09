package com.example.book.service.impl;


import com.example.book.dto.BookDto;
import com.example.book.dto.PageResponseDto;
import com.example.book.entity.Book;
import com.example.book.entity.Publisher;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.mapper.BookMapper;
import com.example.book.repository.BookRepository;
import com.example.book.repository.PublisherRepository;
import com.example.book.service.IBookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResponseDto<BookDto> getAllBooks(int pageNo, int pageSize, String sortBy, String sortDir) {
        // 1. 建立排序條件
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // 2. 建立分頁請求物件
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // 3. 從資料庫撈出分頁資料 (Page<Book>)
        Page<Book> bookPage = bookRepository.findAllWithPublisher(pageable);

        // 4. 將 Entity 轉成 DTO
        List<BookDto> content = bookPage.getContent().stream()
                .map(BookMapper::mapToBookDto)
                .toList();

        // 5. 包裝進 PageResponseDto 裡回傳
        return new PageResponseDto<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }

    @Override
    public BookDto getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book","bookId",bookId.toString()));
        return BookMapper.mapToBookDto(book);
    }

    @Override
    public BookDto updateBooks(Long bookId, BookDto bookDto) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book","bookId",bookId.toString()));
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPrice(bookDto.getPrice());
        book.setPublishDate(bookDto.getPublishDate());
        if (bookDto.getPublisherId()!=null){
            Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow(() -> new ResourceNotFoundException("Publisher","PublisherId",bookDto.getPublisherId().toString()));
            book.setPublisher(publisher);
        }

        Book saveBook = bookRepository.save(book);

        return BookMapper.mapToBookDto(saveBook);
    }


    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = BookMapper.mapToBook(bookDto);
        if (bookDto.getPublisherId()!=null){
            Publisher publisher = publisherRepository.findById(bookDto.getPublisherId()).orElseThrow(() -> new ResourceNotFoundException("Publisher","PublisherId",bookDto.getPublisherId().toString()));
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
    public PageResponseDto<BookDto> searchBook(String searchTerm, int pageNo, int pageSize, String sortBy, String sortDir) {
        // 1. 建立排序條件
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // 2. 建立分頁請求物件
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // 3. 呼叫 Repository 進行模糊搜尋與分頁
        Page<Book> bookPage = bookRepository.findByTitleOrAuthor(searchTerm, pageable);

        // 4. 將 Entity 轉成 DTO
        List<BookDto> content = bookPage.getContent().stream()
                .map(BookMapper::mapToBookDto)
                .toList();

        // 5. 包裝進 PageResponseDto 裡回傳
        return new PageResponseDto<>(
                content,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        );
    }
}
