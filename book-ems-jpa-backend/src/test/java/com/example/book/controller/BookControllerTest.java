package com.example.book.controller;

import com.example.book.dto.BookDto;
import com.example.book.dto.PageResponseDto;
import com.example.book.exception.GlobalExceptionHandler;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.security.jwt.AuthTokenFilter;
import com.example.book.service.IBookService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IBookService bookService;

    @MockitoBean
    private AuthTokenFilter authTokenFilter;


    @Test
    void getAllBooks_shouldReturnPagedBooks() throws Exception {

        // arrange
        BookDto book1 = new BookDto();
        book1.setBookId(1L);
        book1.setTitle("Java 深入淺出");
        book1.setAuthor("Kathy Sierra");
        book1.setPrice(850);

        BookDto book2 = new BookDto();
        book2.setBookId(2L);
        book2.setTitle("Spring Boot 3 實戰開發");
        book2.setAuthor("王大明");
        book2.setPrice(650);

        PageResponseDto<BookDto> pageResponse =
                new PageResponseDto<>(
                        List.of(book1, book2),
                        0,
                        5,
                        2,
                        1,
                        true
                );

        when(bookService.getAllBooks(0, 5, "bookId", "desc"))
                .thenReturn(pageResponse);

        // act + assert
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].bookId").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Java 深入淺出"))
                .andExpect(jsonPath("$.content[1].bookId").value(2))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Spring Boot 3 實戰開發"))
                .andExpect(jsonPath("$.pageNo").value(0))
                .andExpect(jsonPath("$.pageSize").value(5))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));

        // verify
        verify(bookService)
                .getAllBooks(0, 5, "bookId", "desc");
    }


    @Test
    void getBookById_WhenBookNotFound_shouldReturn404() throws Exception {

        //arrange
        when(bookService.getBookById(99L)).thenThrow(new ResourceNotFoundException("Book","bookId","99"));

        //act assert
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("404 NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("Book not found with the given input data bookId: 99"));

        //verify
        verify(bookService).getBookById(99L);
    }

    @Test
    void  getBookById_whenBookExists_shouldReturnBook() throws Exception {
        //arrange
        BookDto bookDto = new BookDto();
        bookDto.setBookId(1L);
        bookDto.setTitle("Spring Boot 3 實戰開發");
        bookDto.setAuthor("王大明");
        bookDto.setPrice(650);
        bookDto.setPublishDate(LocalDate.of(2019,1,1));
        bookDto.setPublisherId(1L);
        bookDto.setPublisherName("碁峰資訊");

        //act assert

        when(bookService.getBookById(1L)).thenReturn(bookDto);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.title").value("Spring Boot 3 實戰開發"))
                .andExpect(jsonPath("$.author").value("王大明"))
                .andExpect(jsonPath("$.price").value(650))
                .andExpect(jsonPath("$.publishDate").value("2019-01-01"))
                .andExpect(jsonPath("$.publisherId").value(1))
                .andExpect(jsonPath("$.publisherName").value("碁峰資訊"));

        //verify

        verify(bookService).getBookById(1L);

    }


}