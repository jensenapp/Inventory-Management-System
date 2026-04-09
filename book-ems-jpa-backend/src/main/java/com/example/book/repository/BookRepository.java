package com.example.book.repository;

import com.example.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    //改用 @EntityGraph 讓 Spring Boot 自動JOIN 出版社，且完美支援 Pageable 分頁
    @EntityGraph(attributePaths = {"publisher"})
    @Query("SELECT b FROM Book b")
    Page<Book> findAllWithPublisher(Pageable pageable);

//    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.publisher WHERE b.title LIKE CONCAT('%', :text, '%') OR b.author LIKE CONCAT('%', :text, '%')")
//    List<Book> findByTitleOrAuthor(@Param("text") String text);

    //利用 @EntityGraph 解決 N+1，並傳入 Pageable 達成資料庫層級分頁
    @EntityGraph(attributePaths = {"publisher"})
    @Query("SELECT b FROM Book b WHERE b.title LIKE CONCAT('%', :text, '%') OR b.author LIKE CONCAT('%', :text, '%')")
    Page<Book> findByTitleOrAuthor(@Param("text") String text, Pageable pageable);
}
