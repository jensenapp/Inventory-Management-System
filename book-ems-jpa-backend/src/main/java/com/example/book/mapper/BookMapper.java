package com.example.book.mapper;


import com.example.book.dto.BookDto;
import com.example.book.entity.Book;

public class BookMapper {

    public static BookDto mapToBookDto(Book book){
        BookDto bookDto = new BookDto();
        bookDto.setBookId(book.getBookId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPrice(book.getPrice());
        bookDto.setPublishDate(book.getPublishDate());

        if (book.getPublisher()!=null){
            bookDto.setPublisherId(book.getPublisher().getPublisherId());
            bookDto.setPublisherName(book.getPublisher().getPublisherName());
        }

        return bookDto;
    }

    public static Book mapToBook(BookDto bookDto){
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPrice(bookDto.getPrice());
        book.setPublishDate(bookDto.getPublishDate());
        return book;
    }
}
