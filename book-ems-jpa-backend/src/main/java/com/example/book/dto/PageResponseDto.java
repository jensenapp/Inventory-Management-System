package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;      // 當前頁面的資料陣列
    private int pageNo;           // 當前頁碼 (從 0 開始)
    private int pageSize;         // 每頁筆數
    private long totalElements;   // 總筆數
    private int totalPages;       // 總頁數
    private boolean last;         // 是否為最後一頁
}