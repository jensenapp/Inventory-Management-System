package com.example.book.repository;

import com.example.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    // 使用 JOIN FETCH 一次抓取 Book 與 Publisher 解決 N+1 問題
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.publisher")
    List<Book> findAllWithPublisher();

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.publisher WHERE b.title LIKE CONCAT('%', :text, '%') OR b.author LIKE CONCAT('%', :text, '%')")
    List<Book> findByTitleOrAuthor(@Param("text") String text);
}
