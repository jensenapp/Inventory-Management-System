package com.example.book.service.impl;

import com.example.book.dto.BookDto;
import com.example.book.dto.PageResponseDto;
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
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.data.domain.*;
import org.springframework.data.querydsl.QPageRequest;

import java.time.LocalDate;
import java.util.List;
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
    void getAllBooks_shouldReturnPagedBookDtos(){

        //arrange
        Book book1 = new Book();
        book1.setBookId(1L);
        book1.setTitle("ABC");
        book1.setAuthor("tom");
        book1.setPrice(1000);

        Book book2 = new Book();
        book2.setBookId(2L);
        book2.setTitle("DEF");
        book2.setAuthor("tim");
        book2.setPrice(2000);

        Pageable pageable = PageRequest.of(0, 3, Sort.by("bookId").ascending());

        Page<Book> page=new PageImpl<>(List.of(book1,book2),pageable,2);

        when(bookRepository.findAllWithPublisher(pageable)).thenReturn(page);

        //act

        PageResponseDto<BookDto> allBooks = bookService.getAllBooks(0, 3, "bookId", "asc");

        //assert

        assertThat(allBooks.getContent().get(0).getBookId()).isEqualTo(1L);
        assertThat(allBooks.getContent().get(1).getAuthor()).isEqualTo("tim");
        assertThat(allBooks.getPageNo()).isEqualTo(0);
        assertThat(allBooks.getTotalPages()).isEqualTo(1);
        assertThat(allBooks.getPageSize()).isEqualTo(3);
        assertThat(allBooks.isLast()).isTrue();

        verify(bookRepository).findAllWithPublisher(any(Pageable.class));

    }

    @Test
    void updateBooks_whenPublisherNotFound_shouldThrowResourceNotFoundException(){
        // arrange
        BookDto newBookDto = new BookDto();
        newBookDto.setTitle("海龜凱薩");
        newBookDto.setAuthor("強強");
        newBookDto.setPrice(1900);
        newBookDto.setPublisherId(2L);
        newBookDto.setPublishDate(LocalDate.of(2008,1,1));



        Book existingBook = new Book();
//        existingBook.setPublisher(publisher);
        existingBook.setBookId(1L);
        existingBook.setAuthor("小強");
        existingBook.setTitle("海龜喜來登");
        existingBook.setPrice(900);
        existingBook.setPublishDate(LocalDate.of(2008,1,1));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(publisherRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> bookService.updateBooks(1L, newBookDto));

        assertThat(exception.getMessage()).contains("Publisher not found").contains("2");

        verify(bookRepository).findById(1L);
        verify(publisherRepository).findById(2L);
        verify(bookRepository,never()).save(existingBook);
    }

    @Test
    void updateBooks_whenBookNotFound_shouldThrowResourceNotFoundException(){

        //arrange
        BookDto newBookDto = new BookDto();
        newBookDto.setTitle("海龜凱薩");
        newBookDto.setAuthor("強強");
        newBookDto.setPrice(1900);
        newBookDto.setPublisherId(2L);
        newBookDto.setPublishDate(LocalDate.of(2008,1,1));

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        //act
        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class, () -> bookService.updateBooks(99L, newBookDto));

        //assert
        assertThat(exception.getMessage()).contains("Book not found").contains("99");

        //verify
        verify(bookRepository).findById(99L);
        verify(publisherRepository,never()).findById(2L);
        verify(bookRepository,never()).save(any(Book.class));
    }

    @Test
    void updateBooks_whenBookExists_shouldUpdateAndReturnBookDto(){
        // arrange
        BookDto newBookDto = new BookDto();
        newBookDto.setTitle("海龜凱薩");
        newBookDto.setAuthor("強強");
        newBookDto.setPrice(1900);
        newBookDto.setPublisherId(2L);
        newBookDto.setPublishDate(LocalDate.of(2008,01,01));

        Publisher publisher = new Publisher();
        publisher.setPublisherId(2L);
        publisher.setPublisherName("大海書局");

        Book existingBook = new Book();
//        existingBook.setPublisher(publisher);
        existingBook.setBookId(1L);
        existingBook.setAuthor("小強");
        existingBook.setTitle("海龜喜來登");
        existingBook.setPrice(900);
        existingBook.setPublishDate(LocalDate.of(2008,01,01));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        when(publisherRepository.findById(2L)).thenReturn(Optional.of(publisher));

        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        //act

        BookDto updateBooks = bookService.updateBooks(1L, newBookDto);

        //assert

        assertThat(updateBooks.getAuthor()).isEqualTo("強強");

        verify(bookRepository).save(any(Book.class));

    }

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