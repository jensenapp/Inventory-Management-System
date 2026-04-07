package com.example.book.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;

@Data
public class BookDto {

    private Long bookId;
    @NotBlank(message = "書名不得為空")
    private String title;
    @NotBlank(message = "作者不得為空")
    private String author;

    @NotNull(message = "價格不得為空")
    @Min(value = 0,message = "價錢不得小於0")
    private Integer price;
    private LocalDate publishDate;
    private Long publisherId;
    private String publisherName;
}
