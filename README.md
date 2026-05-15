# Book EMS — 書本庫存管理系統

Book EMS 是一個前後端分離的書籍庫存管理系統，使用 **Spring Boot 3、Spring Security、Spring Data JPA、MySQL、React 18** 建立，並透過 **Docker Compose** 部署至 VPS。

本專案支援使用者註冊、JWT 登入驗證、角色權限控管、書籍 CRUD、關鍵字搜尋、分頁排序、Swagger API 文件，以及 GitHub Actions 自動化部署流程。後端採用 DTO / Mapper 分層設計，並使用 `@EntityGraph` 優化 Book 與 Publisher 關聯查詢時可能產生的 N+1 問題。

---

## 線上 Demo

- **前端網站**：https://ems.jensen-store.online/books
- **API 文件**：https://ems-api.jensen-store.online/swagger-ui/index.html
- **GitHub Repository**：https://github.com/jensenapp/Inventory-Management-System

### Demo 測試帳號

| 角色 | 帳號 | 密碼 | 權限 |
|---|---|---|---|
| 管理員 | `admin` | `adminPass` | 新增、修改、刪除、瀏覽、搜尋書籍 |
| 一般使用者 | `user1` | `password1` | 瀏覽與搜尋書籍 |

> Demo 環境僅供作品展示與測試使用，資料可能會因重新部署或資料庫重建而重置。

---

## 目錄

- [核心功能](#核心功能)
- [技術棧](#技術棧)
- [系統架構](#系統架構)
- [工程亮點](#工程亮點)
- [DevOps 與自動化部署](#devops-與自動化部署)
- [本機啟動](#本機啟動)
- [API 文件](#api-文件)
- [專案結構](#專案結構)
- [環境變數](#環境變數)
- [學習重點](#學習重點)

---

## 核心功能

### 使用者認證與權限控管

- 使用 **Spring Security + JWT** 實作無狀態登入驗證。
- 支援使用者註冊與登入。
- 密碼以 **BCrypt** 加密後儲存。
- 使用 `ROLE_ADMIN` 與 `ROLE_USER` 區分操作權限。
- 前端透過 `ProtectedRoute` 控制頁面存取。
- 後端透過 `@PreAuthorize("hasRole('ADMIN')")` 保護新增、修改、刪除 API，避免只依賴前端限制。

### 書籍管理

- 管理員可新增、編輯、刪除書籍。
- 一般使用者可瀏覽書籍列表與搜尋資料。
- 書籍與出版社採用 `ManyToOne` 關聯設計。
- 使用 DTO / Mapper 分離 Entity 與 API 回應格式。

### 分頁、排序與搜尋

- 使用 Spring Data `Pageable` 實作伺服器端分頁。
- 支援依指定欄位與排序方向查詢資料。
- 搜尋功能可依書名或作者進行模糊查詢。
- 前端搜尋框使用 500ms debounce，減少高頻無效 API 請求。

### 錯誤處理與資料驗證

- 使用 `@RestControllerAdvice` 統一處理例外。
- 使用 `@Valid`、`@NotBlank`、`@NotNull`、`@Min` 等註解進行資料驗證。
- 自訂錯誤回應格式，讓前端能以一致方式處理錯誤訊息。
- 使用 `CommandLineRunner` 初始化角色、預設帳號、出版社與測試書籍資料。

---

## 技術棧

| 類別 | 技術 |
|---|---|
| 後端語言 | Java 17 |
| 後端框架 | Spring Boot 3 |
| 安全認證 | Spring Security、JWT、BCrypt |
| ORM / 資料存取 | Spring Data JPA、Hibernate |
| 資料庫 | MySQL 8 |
| API 文件 | Springdoc OpenAPI、Swagger UI |
| 前端框架 | React 18、Vite |
| 前端路由 | React Router |
| 前端狀態管理 | Context API、useReducer、localStorage |
| HTTP Client | Axios、Interceptors |
| UI | Bootstrap 5 |
| 容器化 | Docker、Docker Compose、Multi-stage Build |
| CI/CD | GitHub Actions |
| 反向代理 | Nginx、Nginx Proxy Manager |
| 開發工具 | Maven、Git、Postman |

---

## 系統架構

```text
使用者瀏覽器
     │
     ▼
Nginx Proxy Manager
SSL 憑證 / HTTPS / 反向代理
     │
     ├───────────────────────────────┐
     ▼                               ▼
React Frontend Container             Spring Boot Backend Container
Nginx Static Server                   RESTful API
Port: 8085                            Port: 8081
                                     │
                                     ▼
                              MySQL Container
                              Port: 127.0.0.1:3307 → 3306

Docker Network: ems-network
```

### 架構說明

- 前端使用 React + Vite 建置，正式環境由 Nginx 容器提供靜態資源。
- 後端使用 Spring Boot 提供 RESTful API。
- MySQL 使用 Docker volume 保存資料。
- MySQL 對外 port 綁定於 `127.0.0.1`，避免資料庫直接暴露在公開網路。
- Nginx Proxy Manager 負責 HTTPS、SSL 憑證與反向代理。

---

## 工程亮點

### 1. 使用 `@EntityGraph` 降低 N+1 查詢問題

書籍列表需要顯示出版社名稱，如果直接使用 Lazy Loading，查詢多筆書籍時可能發生 N+1 查詢問題。

本專案在 Repository 層使用 `@EntityGraph(attributePaths = {"publisher"})`，讓查詢書籍時一併載入 Publisher 資料，降低多次查詢造成的資料庫負擔，並保留 Spring Data `Pageable` 分頁能力。

```java
@EntityGraph(attributePaths = {"publisher"})
@Query("SELECT b FROM Book b")
Page<Book> findAllWithPublisher(Pageable pageable);

@EntityGraph(attributePaths = {"publisher"})
Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
    String title,
    String author,
    Pageable pageable
);
```

---

### 2. Axios Interceptors 統一處理 Token 與 401

前端建立共用的 Axios instance，集中處理 API base URL、JWT Token 注入與未授權回應。

```javascript
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("jwtToken");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  }
);
```

此設計避免每個 API 呼叫重複撰寫 token header，也能在 token 過期或無效時統一導回登入頁。

---

### 3. DTO / Mapper 分離 Entity 與 API 回應

後端不直接將 JPA Entity 回傳給前端，而是透過 DTO 控制 API 回應格式。

```java
public class BookDto {
    private Long bookId;
    private String title;
    private String author;
    private Integer price;
    private LocalDate publishDate;
    private Long publisherId;
    private String publisherName;
}
```

此設計能降低資料表結構直接暴露給前端的風險，也讓 API 格式更穩定。

---

### 4. 分層架構設計

後端採用常見的 Spring Boot 分層架構：

```text
Controller
   ↓
Service Interface
   ↓
Service Implementation
   ↓
Repository
   ↓
Database
```

各層職責如下：

| 層級 | 職責 |
|---|---|
| Controller | 接收 HTTP 請求、處理 Request / Response |
| Service | 撰寫商業邏輯與交易流程 |
| Repository | 負責資料庫存取 |
| DTO / Mapper | 控制 API 資料格式，轉換 Entity 與 DTO |
| Exception Handler | 統一處理錯誤回應 |

---

### 5. 前端登入狀態持久化

前端使用 `Context API + useReducer` 管理登入狀態，並將 JWT 與使用者資訊保存至 `localStorage`。當使用者重新整理頁面時，系統會從 localStorage 還原登入狀態。

```text
登入成功
   ↓
AuthContext 更新狀態
   ↓
localStorage 保存 jwtToken 與 user
   ↓
Axios interceptor 自動帶入 Bearer Token
   ↓
API 回傳 401 時清除登入狀態並導回登入頁
```

---

### 6. 開發與正式環境分離

後端使用 Spring Profiles 區分開發與正式環境：

- `application-dev.properties`：連線本機 MySQL。
- `application-prod.properties`：連線 Docker network 內的 MySQL container。

前端使用 Vite 環境變數區分 API 位置：

- `.env.development`：本機 API。
- `.env.production`：正式環境 API。

---

## DevOps 與自動化部署

本專案使用 Docker Compose 編排前端、後端與 MySQL，並透過 GitHub Actions 自動部署至個人 VPS。

### Docker Compose 服務

| Service | 說明 |
|---|---|
| `db` | MySQL 8 資料庫 |
| `backend` | Spring Boot REST API |
| `frontend` | React build 後由 Nginx 提供靜態檔案 |

### Multi-stage Build

前端與後端皆使用 Multi-stage Build：

- 前端：Node build 階段產生靜態檔，再交由 Nginx serve。
- 後端：Maven build 階段產生 jar，再使用 JRE image 執行。

### GitHub Actions 部署流程

```text
git push main
     │
     ▼
GitHub Actions 觸發 deploy job
     │
     ▼
透過 SSH 連線 VPS
     │
     ├── 首次部署：git clone repository
     ├── 更新部署：git pull origin main
     ├── 由 GitHub Secrets 產生 .env
     ├── docker compose down
     ├── docker compose up -d --build
     └── docker image prune -f
```

### 部署安全性

- DB 密碼與 JWT secret 不寫入程式碼。
- 正式環境敏感資訊由 GitHub Secrets 注入。
- MySQL port 僅綁定 VPS 本機端，不直接對外公開。
- HTTPS 與 SSL 憑證由 Nginx Proxy Manager 管理。

---

## 本機啟動

### 前置需求

請先安裝：

- Docker
- Docker Compose
- Git

### Clone 專案

```bash
git clone https://github.com/jensenapp/Inventory-Management-System.git
cd Inventory-Management-System
```

### 建立 `.env`

在專案根目錄建立 `.env`：

```bash
cat <<EOF > .env
DB_ROOT_PASSWORD=rootpassword
DB_PASSWORD=emspassword
JWT_SECRET=VGhpc0lzQUxvY2FsRGV2ZWxvcG1lbnRTZWNyZXRLZXlUaGF0SXNMb25nRW5vdWdo
EOF
```

> `JWT_SECRET` 建議使用 Base64 編碼且長度足夠的金鑰。正式環境請勿使用上方範例值。

### 啟動服務

```bash
docker compose up -d --build
```

### 查看容器狀態

```bash
docker compose ps
```

### 本機服務端點

| 服務 | URL |
|---|---|
| 前端 | http://localhost:8085 |
| 後端 API | http://localhost:8081/api/books |
| Swagger UI | http://localhost:8081/swagger-ui/index.html |
| MySQL | 127.0.0.1:3307 |

### 關閉服務

```bash
docker compose down
```

如需刪除資料庫 volume：

```bash
docker compose down -v
```

---

## API 文件

啟動後端後，可透過 Swagger UI 查看與測試 API：

```text
http://localhost:8081/swagger-ui/index.html
```

正式環境 API 文件：

```text
https://ems-api.jensen-store.online/swagger-ui/index.html
```

### 主要 API

| Method | Endpoint | 說明 | 權限 |
|---|---|---|---|
| `POST` | `/api/auth/public/signin` | 使用者登入，取得 JWT | Public |
| `POST` | `/api/auth/public/signup` | 使用者註冊 | Public |
| `GET` | `/api/auth/user` | 取得目前登入使用者資訊 | Authenticated |
| `GET` | `/api/books` | 取得書籍列表，支援分頁與排序 | Authenticated |
| `GET` | `/api/books/{id}` | 取得單本書籍 | Authenticated |
| `GET` | `/api/books/search?text={keyword}` | 依書名或作者搜尋書籍 | Authenticated |
| `POST` | `/api/books` | 新增書籍 | ADMIN |
| `PUT` | `/api/books/{id}` | 更新書籍 | ADMIN |
| `DELETE` | `/api/books/{id}` | 刪除書籍 | ADMIN |

### JWT 使用方式

登入成功後會取得 `jwtToken`，後續請求需在 Header 帶入：

```http
Authorization: Bearer <jwtToken>
```

Swagger UI 可點選 **Authorize**，輸入 Bearer Token 後測試需要登入的 API。

---

## 專案結構

```text
Inventory-Management-System/
├── .github/
│   └── workflows/
│       └── deploy.yml
│
├── book-ems-jpa-backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/example/book/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── exception/
│       │   │   ├── mapper/
│       │   │   ├── repository/
│       │   │   ├── security/
│       │   │   └── service/
│       │   └── resources/
│       │       ├── application.properties
│       │       ├── application-dev.properties
│       │       └── application-prod.properties
│       └── test/
│
├── book-ems-jpa-frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
│       ├── api/
│       ├── components/
│       ├── services/
│       ├── store/
│       ├── App.jsx
│       └── main.jsx
│
├── docker-compose.yml
└── README.md
```

---

## 環境變數

### Docker Compose `.env`

| 變數名稱 | 說明 |
|---|---|
| `DB_ROOT_PASSWORD` | MySQL root 密碼 |
| `DB_PASSWORD` | 應用程式連線 MySQL 使用的密碼 |
| `JWT_SECRET` | JWT 簽名金鑰，建議使用 Base64 編碼 |

### GitHub Secrets

| Secret | 說明 |
|---|---|
| `VPS_HOST` | VPS 主機位址 |
| `VPS_USERNAME` | VPS SSH 使用者名稱 |
| `VPS_SSH_KEY` | VPS SSH 私鑰 |
| `DB_ROOT_PASSWORD` | 正式環境 MySQL root 密碼 |
| `DB_PASSWORD` | 正式環境應用程式 DB 密碼 |
| `JWT_SECRET` | 正式環境 JWT 簽名金鑰 |

### `.gitignore` 建議

請確認 `.env` 不要提交至 GitHub：

```gitignore
.env
*.env.local
```

可以另外提供 `.env.example` 作為範例：

```env
DB_ROOT_PASSWORD=your_root_password
DB_PASSWORD=your_db_password
JWT_SECRET=your_base64_jwt_secret
```

---

## 學習重點

透過本專案，實作並熟悉以下主題：

- Spring Boot RESTful API 設計
- Spring Security + JWT 無狀態登入驗證
- 前後端分離架構下的 Token 傳遞與 401 處理
- ROLE_ADMIN / ROLE_USER 權限控管
- Spring Data JPA 分頁、排序與模糊搜尋
- DTO / Mapper 分離 Entity 與 API 回應格式
- 使用 `@EntityGraph` 降低 N+1 查詢問題
- React Context + useReducer 全域狀態管理
- Docker Compose 編排前端、後端與 MySQL
- GitHub Actions 透過 SSH 自動部署至 VPS
- Nginx Proxy Manager 管理 HTTPS 與反向代理

---

## 後續可改進方向

- 補充 Service / Controller 層單元測試與整合測試。
- 將出版社管理改為獨立 CRUD，而不是固定選項。
- 增加書籍圖片上傳功能。
- 增加審計欄位，例如建立時間、更新時間、建立者。
- 增加資料庫備份與還原流程。
- 增加健康檢查端點與基本監控。

---

## 關於本專案

本專案作為 Java 後端與全端整合能力的作品集，重點展示：

- 後端 API 設計
- JWT 登入與角色權限
- JPA 關聯查詢與分頁搜尋
- React 前端整合
- Docker 容器化部署
- GitHub Actions 自動化部署


