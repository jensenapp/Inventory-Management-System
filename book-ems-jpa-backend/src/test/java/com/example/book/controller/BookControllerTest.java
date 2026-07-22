package com.example.book.controller;

import com.example.book.dto.BookDto;
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