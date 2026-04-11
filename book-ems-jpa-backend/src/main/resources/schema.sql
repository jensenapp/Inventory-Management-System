-- 每次啟動前先刪除舊表 (適合開發測試環境)
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS publisher;

-- 1. 必須先建立 publisher 表格 (因為 book 會依賴它)
CREATE TABLE publisher(
                          publisher_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(200) NOT NULL
);

-- 2. 再建立 book 表格
CREATE TABLE book (
                      book_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      price INT NOT NULL,
                      publish_date DATE default (CURRENT_DATE),
                      publisher_id BIGINT,
                      FOREIGN KEY (publisher_id) REFERENCES publisher(publisher_id)
);