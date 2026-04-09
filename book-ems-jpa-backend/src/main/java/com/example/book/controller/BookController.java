// file: src/main/java/com/jensen/ems/controller/BookController.java
package com.example.book.controller;


import com.example.book.dto.BookDto;
import com.example.book.dto.PageResponseDto;
import com.example.book.dto.ResponseDto;
import com.example.book.service.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(summary = "取得所有書本 (分頁)", description = "回傳系統中所有的書本分頁列表，包含出版社資訊")
    public ResponseEntity<PageResponseDto<BookDto>> getAllBooks(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "bookId", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir
    ) {
        PageResponseDto<BookDto> pageResponse = bookService.getAllBooks(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根據bookId取得書本", description = "根據ID回傳書本列表，包含出版社資訊")
    public BookDto getBookById(@PathVariable Long id){
        return bookService.getBookById(id);
    }

    @PostMapping
    @Operation(summary = "新增書本", description = "建立一本新書，可綁定已存在的出版社 ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid BookDto bookDto) {
        BookDto book = bookService.createBook(bookDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新書本", description = "根據 ID 更新書本資訊")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        BookDto bookDto1 = bookService.updateBooks(id, bookDto);
        return ResponseEntity.ok(bookDto1);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除書本", description = "根據 ID 永久刪除指定書籍")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ResponseDto("200","成功刪除書本"));
    }

    @GetMapping("/search")
    @Operation(summary = "搜尋書本 (分頁)", description = "根據關鍵字模糊搜尋書名 (title) 或作者 (author)，並支援分頁")
    public ResponseEntity<PageResponseDto<BookDto>> searchBook(
            @RequestParam String text,
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "bookId", required = false) String sortBy,
            @RequestParam(defaultValue = "desc", required = false) String sortDir
    ){
        // 👉 傳遞分頁參數給 Service
        PageResponseDto<BookDto> pageResponse = bookService.searchBook(text, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(pageResponse);
    }

}