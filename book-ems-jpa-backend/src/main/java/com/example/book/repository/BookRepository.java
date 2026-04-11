package com.example.book.repository;

import com.example.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


    public interface BookRepository extends JpaRepository<Book, Long> {

        // =========================================================================
        // ❌ 有 N+1 問題的寫法 (已註解)
        // 說明：當你查詢 10 本書，前端需要顯示「出版社名稱」時，
        // Hibernate 會先下 1 次 SQL 查出 10 本書，
        // 接著為了拿出版社名字，又單獨下 10 次 SQL 查 Publisher 資料表，共 11 次查詢。
        // =========================================================================

        // 情境一：取得所有書本
        // @Query("SELECT b FROM Book b")
        // Page<Book> findAllBooks(Pageable pageable);

        // 情境二：關鍵字搜尋 (使用 Spring Data JPA 方法命名)
        // Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);


        // =========================================================================
        // ✅ 沒有 N+1 問題的寫法 (最佳實務)
        // 說明：加上 @EntityGraph 後，底層會改用 LEFT OUTER JOIN，
        // 無論有幾本書，都只會下「1 次」SQL 就能把書本與對應的出版社資料全部打包抓回來。
        // =========================================================================

        // 情境一：取得所有書本 (完美解決 N+1，且支援分頁)
        @EntityGraph(attributePaths = {"publisher"})
        @Query("SELECT b FROM Book b")
        Page<Book> findAllWithPublisher(Pageable pageable);

        // 情境二：關鍵字搜尋 (完美解決 N+1，且支援分頁)
        @EntityGraph(attributePaths = {"publisher"})
        Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);

    }


