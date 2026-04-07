// file: src/main/java/com/jensen/ems/controller/BookController.java
package com.example.book.controller;


import com.example.book.dto.BookDto;
import com.example.book.service.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Books", description = "書本管理相關 API") // 幫整個 Controller 命名分類
public class BookController {

    private final IBookService bookService;

    @GetMapping
    @Operation(summary = "取得所有書本", description = "回傳系統中所有的書本列表，包含出版社資訊") // 幫單一 API 加上說明
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    @Operation(summary = "新增書本", description = "建立一本新書，可綁定已存在的出版社 ID")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        BookDto book = bookService.createBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新書本", description = "根據 ID 更新書本資訊")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        BookDto bookDto1 = bookService.updateBooks(id, bookDto);
        return ResponseEntity.ok(bookDto1);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除書本", description = "根據 ID 永久刪除指定書籍")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("book deleted");
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋書本", description = "根據關鍵字模糊搜尋書名 (title) 或作者 (author)")
    public ResponseEntity<List<BookDto>> searchBook(@RequestParam String text){
        List<BookDto> bookDtos = bookService.searchBook(text);
        return ResponseEntity.ok(bookDtos);
    }
}