package com.example.book.dto;


import lombok.Data;


import java.time.LocalDate;

@Data
public class BookDto {
    private Long bookId;
    private String title;
    private String author;
    private Integer price;
    private LocalDate publishDate;
    private Long publisherId;
    private String publisherName;
}
