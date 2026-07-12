//package com.example.book.controller;
//
//import com.example.book.dto.BookDto;
//import com.example.book.service.IBookService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BookController.class)
//class BookControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private IBookService bookService;
//
//    @Test
//    void getBookById_shouldReturnBook() throws Exception {
//        BookDto dto = new BookDto();
//        dto.setBookId(1L);
//        dto.setTitle("Spring Boot 3 實戰開發");
//        dto.setAuthor("王大明");
//        dto.setPrice(650);
//
//        when(bookService.getBookById(1L)).thenReturn(dto);
//
//        mockMvc.perform(get("/api/books/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.bookId").value(1))
//                .andExpect(jsonPath("$.title").value("Spring Boot 3 實戰開發"))
//                .andExpect(jsonPath("$.author").value("王大明"))
//                .andExpect(jsonPath("$.price").value(650));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void createBook_whenPriceIsNegative_shouldReturnBadRequest() throws Exception {
//        String requestBody = """
//                {
//                  "title": "測試書",
//                  "author": "測試作者",
//                  "price": -1
//                }
//                """;
//
//        mockMvc.perform(post("/api/books")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.price").value("價錢不得小於0"));
//    }
//}