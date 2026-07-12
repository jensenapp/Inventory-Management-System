package com.example.book.mapper;

import com.example.book.dto.BookDto;
import com.example.book.entity.Book;
import com.example.book.entity.Publisher;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    @Test
    void mapToBookDto_shouldMapBookFieldsAndPublisher() {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Spring Boot 3 實戰開發");
        book.setAuthor("王大明");
        book.setPrice(650);
        book.setPublishDate(LocalDate.of(2023, 11, 15));

        Publisher publisher = new Publisher();
        publisher.setPublisherId(10L);
        publisher.setPublisherName("碁峰資訊");
        book.setPublisher(publisher);

        BookDto dto = BookMapper.mapToBookDto(book);

        assertThat(dto.getBookId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Spring Boot 3 實戰開發");
        assertThat(dto.getAuthor()).isEqualTo("王大明");
        assertThat(dto.getPrice()).isEqualTo(650);
        assertThat(dto.getPublishDate()).isEqualTo(LocalDate.of(2023, 11, 15));
        assertThat(dto.getPublisherId()).isEqualTo(10L);
        assertThat(dto.getPublisherName()).isEqualTo("碁峰資訊");
    }

    @Test
    void mapToBookDto_shouldWorkWhenPublisherIsNull() {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Java 深入淺出");
        book.setAuthor("Kathy Sierra");
        book.setPrice(850);

        BookDto dto = BookMapper.mapToBookDto(book);

        assertThat(dto.getPublisherId()).isNull();
        assertThat(dto.getPublisherName()).isNull();
    }

    @Test
    void mapToBook_shouldMapDtoToEntity() {
        BookDto dto = new BookDto();
        dto.setTitle("Docker 容器化架構指南");
        dto.setAuthor("陳小華");
        dto.setPrice(580);
        dto.setPublishDate(LocalDate.of(2023, 8, 10));

        Book book = BookMapper.mapToBook(dto);

        assertThat(book.getTitle()).isEqualTo("Docker 容器化架構指南");
        assertThat(book.getAuthor()).isEqualTo("陳小華");
        assertThat(book.getPrice()).isEqualTo(580);
        assertThat(book.getPublishDate()).isEqualTo(LocalDate.of(2023, 8, 10));
    }
}