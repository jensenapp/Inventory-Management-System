package com.example.book.service.impl;

import com.example.book.dto.BookDto;
import com.example.book.entity.Book;
import com.example.book.entity.Publisher;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.BookRepository;
import com.example.book.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getBookById_whenBookExists_shouldReturnBookDto() {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Spring Boot 3 實戰開發");
        book.setAuthor("王大明");
        book.setPrice(650);
        book.setPublishDate(LocalDate.of(2023, 11, 15));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDto result = bookService.getBookById(1L);


        assertThat(result.getBookId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Spring Boot 3 實戰開發");
        assertThat(result.getAuthor()).isEqualTo("王大明");
        assertThat(result.getPrice()).isEqualTo(650);
        assertThat(result.getPublishDate()).isEqualTo(LocalDate.of(2023, 11, 15));
    }

    @Test
    void getBookById_whenBookNotFound_shouldThrowResourceNotFoundException() {
        //arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        //act
        ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(99L));
        //assert
        assertThat(resourceNotFoundException.getMessage()).contains("Book not found");

    }

    @Test
    void createBook_whenPublisherIdIsNull_shouldSaveBook(){
        // arrange
        BookDto bookDto = new BookDto();
        bookDto.setTitle("海龜喜來登");
        bookDto.setAuthor("強強");
        bookDto.setPrice(900);

        bookDto.setPublishDate(LocalDate.of(2008,01,01));



        Book book = new Book();

        book.setBookId(1L);
        book.setAuthor("強強");
        book.setTitle("海龜喜來登");
        book.setPrice(900);
        book.setPublishDate(LocalDate.of(2008,01,01));

        when(bookRepository.save(any(Book.class))).thenReturn(book);

       //act
        BookDto savedBook = bookService.createBook(bookDto);
        //assert
        assertThat(savedBook.getBookId()).isEqualTo(1L);
        assertThat(savedBook.getAuthor()).isEqualTo("強強");
        verifyNoInteractions(publisherRepository);
    }

    @Test
    void createBook_whenPublisherExists_shouldSaveBookWithPublisher() {
        // arrange
        BookDto bookDto = new BookDto();
        bookDto.setTitle("海龜喜來登");
        bookDto.setAuthor("強強");
        bookDto.setPrice(900);
        bookDto.setPublisherId(10L);
        bookDto.setPublisherId(1L);
        bookDto.setPublishDate(LocalDate.of(2008,01,01));

        Publisher publisher = new Publisher();
        publisher.setPublisherId(1L);
        publisher.setPublisherName("大海書局");

        Book book = new Book();
        book.setPublisher(publisher);
        book.setBookId(1L);
        book.setAuthor("強強");
        book.setTitle("海龜喜來登");
        book.setPrice(900);
        book.setPublishDate(LocalDate.of(2008,01,01));

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

        when(bookRepository.save(any(Book.class))).thenReturn(book);


        // act

        BookDto result = bookService.createBook(bookDto);


        // assert

        assertThat(result.getAuthor()).isEqualTo("強強");
        verify(publisherRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_whenPublisherNotFound_shouldThrowResourceNotFoundException(){
        // arrange
        BookDto bookDto = new BookDto();
        bookDto.setTitle("海龜喜來登");
        bookDto.setAuthor("強強");
        bookDto.setPrice(900);
        bookDto.setPublisherId(99L);
        bookDto.setPublishDate(LocalDate.of(2008,01,01));

        when(publisherRepository.findById(99L)).thenReturn(Optional.empty());

        //act   //assert

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(bookDto));

        assertThat(exception.getMessage()).
                contains("Publisher not found").
                contains("99");

        verify(publisherRepository).findById(99L);

        verify(bookRepository,never()).save(any(Book.class));

    }
}