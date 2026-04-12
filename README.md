----

#  Book EMS — 書本庫存管理系統

這是一個基於 **前後端分離架構 (Frontend-Backend Separation)** 開發的全端書本與庫存管理系統。旨在提供企業級的權限管控、流暢的資料操作體驗，並透過 Docker Compose 與 GitHub Actions 實現一鍵容器化部署與 CI/CD 自動流水線。

> ** 線上體驗 (Live Demo):** [https://ems.jensen-store.online/books](https://ems.jensen-store.online/books)
>
> | 角色 | 帳號 | 密碼 | 權限 |
> |------|------|------|------|
> | 管理員 | `admin` | `adminPass` | 新增 / 修改 / 刪除 / 瀏覽 |
> | 一般使用者 | `user1` | `password1` | 僅限瀏覽 |

-----

##  目錄

- [核心功能](https://www.google.com/search?q=%23%E6%A0%B8%E5%BF%83%E5%8A%9F%E8%83%BD)
- [技術棧](https://www.google.com/search?q=%23%E6%8A%80%E8%A1%93%E6%A3%A7)
- [系統架構圖](https://www.google.com/search?q=%23%E7%B3%BB%E7%B5%B1%E6%9E%B6%E6%A7%8B%E5%9C%96)
- [工程亮點（面試重點）](https://www.google.com/search?q=%23%E5%B7%A5%E7%A8%8B%E4%BA%AE%E9%BB%9E%E9%9D%A2%E8%A9%A6%E9%87%8D%E9%BB%9E)
- [DevOps 與 CI/CD 流程](https://www.google.com/search?q=%23devops-%E8%88%87-cicd-%E6%B5%81%E7%A8%8B)
- [本機運行指南](https://www.google.com/search?q=%23%E6%9C%AC%E6%A9%9F%E9%81%8B%E8%A1%8C%E6%8C%87%E5%8D%97)
- [API 文件](https://www.google.com/search?q=%23api-%E6%96%87%E4%BB%B6)
- [專案結構](https://www.google.com/search?q=%23%E5%B0%88%E6%A1%88%E7%B5%90%E6%A7%8B)
- [環境變數說明](https://www.google.com/search?q=%23%E7%92%B0%E5%A2%83%E8%AE%8A%E6%95%B8%E8%AA%AA%E6%98%8E)

-----

## 核心功能

### 安全與身分驗證 (Security & Auth)

- 基於 **Spring Security + JWT (JSON Web Token)** 實作**無狀態 (Stateless)** 身分驗證。
- **RBAC 角色權限控制**：嚴格區分 `ROLE_ADMIN` 與 `ROLE_USER` 雙層防護。
    - **前端**：按鈕與路由依角色隱藏（實作 `ProtectedRoute` 守衛）。
    - **後端**：API 層以 `@PreAuthorize("hasRole('ADMIN')")` 再次驗證，防止惡意繞過前端。
- 使用者註冊與登入，密碼以 BCrypt 加密儲存，保障資訊安全。

### 書本管理 (Book Management)

- 完整的書本 CRUD（新增、查詢、修改、刪除）功能。
- 與出版社 (Publisher) 實體形成 **多對一 (Many-to-One)** 關聯。

### 效能最佳化：分頁、排序與搜尋

- **伺服器端分頁 (Server-side Pagination)**：搭配 Spring Data `Pageable`，避免一次性載入大量資料，降低傳輸負擔。
- **動態排序**：支援自訂排序欄位與方向。
- **Debounce 防抖模糊搜尋**：前端 500ms 延遲後才發出 API 請求，對書名與作者進行全文搜尋，減少不必要的後端負載。

### 系統穩健性與防呆

- 透過 `CommandLineRunner` 於系統啟動時**自動初始化**角色、預設帳號與測試資料（冪等性設計，重啟不重複建立）。
- 完善的 **Global Exception Handling (全域例外處理)** (`@RestControllerAdvice`)，統一回傳結構化的錯誤 JSON。
- 結合 `@Valid` 與 `ConstraintViolationException` 進行嚴謹的參數校驗。

-----

## 技術棧

| 類別 | 技術 |
|------|------|
| **後端語言** | Java 17 |
| **後端框架** | Spring Boot 3.x |
| **安全認證** | Spring Security + JWT (jjwt) |
| **ORM** | Spring Data JPA / Hibernate |
| **資料庫** | MySQL 8.0 |
| **API 文件** | Springdoc OpenAPI (Swagger UI) |
| **程式碼簡化** | Lombok |
| **前端框架** | React 18 (Vite 建置) |
| **前端路由** | React Router Dom v6 |
| **全域狀態** | Context API + useReducer |
| **HTTP 客戶端** | Axios (含 Interceptors) |
| **UI 元件庫** | Bootstrap 5 |
| **容器化** | Docker + Docker Compose (Multi-stage Build) |
| **CI/CD** | GitHub Actions |
| **反向代理** | Nginx (前端容器) + Nginx Proxy Manager (伺服器端) |

-----

## 系統架構圖

```text
使用者瀏覽器
     │
     ▼
[Nginx Proxy Manager]  ← SSL 憑證 / 反向代理
     │
     ├─────────────────────────────────┐
     ▼                                 ▼
[React 前端容器 :8085]         [Spring Boot 後端容器 :8081]
 (Nginx 靜態伺服器)                     │
                                        ▼
                               [MySQL 資料庫容器 :3307]
                               (僅綁定 127.0.0.1，不對外暴露)

所有容器共享同一個 Docker Bridge Network: ems-network
```

-----

## 工程亮點

### 1\. 解決 ORM 常見的 N+1 查詢問題

查詢書本列表需一併顯示出版社名稱，若使用預設懶加載 (Lazy Loading)，Hibernate 會產生 **N+1 次 SQL**。
**解決方案：** 在 `BookRepository` 中使用 `@EntityGraph`，強制底層以 **LEFT OUTER JOIN** 的方式一次性抓取所需資料，且此方式**完美相容於 Spring Data 的 `Pageable` 分頁機制**（這是 `JOIN FETCH` 寫法的已知限制）。

```java
// BookRepository.java
@EntityGraph(attributePaths = {"publisher"})
@Query("SELECT b FROM Book b")
Page<Book> findAllWithPublisher(Pageable pageable);
```

### 2\. 前端 Axios Interceptors 攔截器設計

為了提升 UX 與安全性，實作了統一的 API 請求/回應攔截器：

- **Request 攔截：** 自動從 `localStorage` 提取 JWT Token 並注入 `Authorization` Header，避免每次發送 API 都要手動帶入。
- **Response 攔截：** 全域捕捉 HTTP `401 Unauthorized` 錯誤。一旦 Token 過期或無效，自動清除本機狀態並強制將使用者導向登入頁面。

<!-- end list -->

```javascript
// apiClient.js
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("jwtToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("jwtToken");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);
```

### 3\. 架構分層與 DTO (Data Transfer Object) 模式

後端嚴格遵守 **Controller → Service Interface → ServiceImpl → Repository** 的分層架構：

- **面向介面程式設計**：降低耦合，符合 SOLID 原則。
- **DTO + Mapper 模式**：避免將底層的 JPA Entity 直接暴露給前端，保護資料庫結構細節。
- **Java Record 輕量 DTO**：使用 Java 14+ `record` 建立不可變的回應物件（如 `LoginResponse`），大幅減少樣板代碼。

### 4\. 前後端環境變數完整分離 (Dev vs Prod)

- **後端**：透過 `spring.profiles.active` 區分 `application-dev.properties` (連線至本機 localhost) 與 `application-prod.properties` (連線至 Docker 內部 network 的 db 容器)。
- **前端**：透過 Vite 的 `.env.development` 與 `.env.production`，讓前端在開發時打向 localhost，而在 `npm run build` 打包時自動將正式機 API 網址燒錄至靜態檔中。

### 5\. 前端 Auth State 持久化設計

使用 **Context API + useReducer** 管理全域登入狀態，並搭配 `useEffect` 與 `localStorage` 實現**頁面重整後的登入狀態恢復**，無需依賴 Redux 等龐大外部函式庫即可達成同等效果。

-----

## DevOps 與自動化部署

為確保開發與正式環境的高度一致性，並降低手動部署的錯誤率，本專案導入了基礎的 DevOps 實踐：

### 初探容器化與 Multi-stage Build

前後端各自撰寫 `Dockerfile`，實踐多階段建置 (Multi-stage Builds)，有效縮減最終 Image 體積：

- **後端**：Stage 1 以 Maven 編譯 `.jar`；Stage 2 僅複製 `.jar` 至 `eclipse-temurin:17-jre-alpine` 輕量映像執行。
- **前端**：Stage 1 以 Node 環境執行 `npm run build`；Stage 2 以 Nginx 提供靜態資源服務。

### 實踐持續整合與交付 (GitHub Actions CI/CD)

整合 GitHub Actions 建立自動化流水線。當程式碼推送到 `main` 分支時，會自動觸發遠端部署流程：

```text
開發者 git push → main 分支
        │
        ▼
 GitHub Actions 觸發
        │
        ▼
 SSH 連線至 VPS 伺服器
        │
        ├── git pull (更新最新程式碼)
        ├── 動態寫入 .env 環境變數 (從 GitHub Secrets 注入)
        ├── docker compose down
        ├── docker compose up -d --build (重新建置並啟動)
        └── docker image prune -f (清理舊 Image，釋放空間)
```

### 資安加固

- 資料庫對外 Port 限制綁定於伺服器本機端 (`127.0.0.1:3307:3306`)，阻絕外部惡意掃描。
- 敏感資訊（DB 密碼、JWT 金鑰）全以 GitHub Secrets + Docker Compose 環境變數注入，不提交至版本控制。
- Nginx Proxy Manager 統一處理 SSL 憑證與 HTTPS 終止。

-----

## 本機運行指南 (Local Development Setup)

### 前置需求

- 電腦已安裝 **Docker Engine** & **Docker Compose**

### 啟動步驟

```bash
# 1. Clone 專案
git clone https://github.com/your-account/book-ems.git
cd book-ems

# 2. 建立本機環境變數檔（可自行修改密碼）
cat <<EOF > .env
DB_ROOT_PASSWORD=rootpassword
DB_PASSWORD=emspassword
JWT_SECRET=ThisIsALocalDevelopmentSecretKeyThatIsLongEnough
EOF

# 3. 一鍵啟動（MySQL + Spring Boot + React）
docker compose up -d --build

# 4. 查看啟動狀態
docker compose ps
```

### 服務端點

| 服務 | URL |
|------|-----|
| 前端 (React) | [http://localhost:8085](https://www.google.com/search?q=http://localhost:8085) |
| 後端 API | [http://localhost:8081/api/books](https://www.google.com/search?q=http://localhost:8081/api/books) |
| Swagger UI | [http://localhost:8081/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8081/swagger-ui/index.html) |

### 關閉服務

```bash
docker compose down
# 若需清除資料庫 Volume (刪除所有資料)
docker compose down -v
```

-----

## API 文件

啟動後端後，可透過自動生成的 Swagger UI 瀏覽與測試所有 API：
**[http://localhost:8081/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8081/swagger-ui/index.html)**

| Method | 路徑 | 說明 | 權限 |
|--------|------|------|------|
| `POST` | `/api/auth/public/signin` | 使用者登入，取得 JWT | 公開 |
| `POST` | `/api/auth/public/signup` | 使用者註冊 | 公開 |
| `GET` | `/api/books` | 取得書本列表（分頁） | 已登入 |
| `GET` | `/api/books/search?text={keyword}`| 模糊搜尋（分頁） | 已登入 |
| `POST` | `/api/books` | 新增書本 | ADMIN |
| `PUT` | `/api/books/{id}` | 更新書本 | ADMIN |
| `DELETE` | `/api/books/{id}` | 刪除書本 | ADMIN |

-----

## 專案結構 (Project Structure)

```text
book-ems/
├── .github/
│   └── workflows/
│       └── deploy.yml                  # GitHub Actions CI/CD 設定檔
│
├── book-ems-jpa-backend/               # Spring Boot 後端
│   ├── Dockerfile
│   └── src/main/java/com/example/book/
│       ├── config/                     # Swagger 等全域設定
│       ├── controller/                 # RESTful API 端點 (Auth, Book)
│       ├── dto/                        # 資料傳輸物件
│       ├── entity/                     # JPA 實體模型 (Book, Publisher, User, Role)
│       ├── exception/                  # 全域例外處理 (@RestControllerAdvice)
│       ├── mapper/                     # Entity ↔ DTO 轉換邏輯
│       ├── repository/                 # 資料庫存取層 (含 @EntityGraph 最佳化)
│       ├── security/                   # Spring Security & JWT 核心邏輯
│       └── service/                    # 業務邏輯層 (Interface & Impl)
│
├── book-ems-jpa-frontend/              # React 前端
│   ├── Dockerfile
│   ├── nginx.conf                      # Nginx SPA 路由 Fallback 設定
│   ├── .env.development                # 開發環境變數
│   ├── .env.production                 # 正式環境變數
│   └── src/
│       ├── api/                        # Axios 實體與攔截器設定
│       ├── components/                 # React UI 元件與受保護路由 (ProtectedRoute)
│       ├── services/                   # 封裝所有的 API 呼叫邏輯
│       ├── store/                      # Context API 狀態管理 (AuthContext)
│       └── App.jsx                     # 應用程式路由設定
│
└── docker-compose.yml                  # 容器編排檔 (db / backend / frontend)
```

-----

## 環境變數說明

### Docker Compose `.env` 檔（本機使用）

| 變數名稱 | 說明 |
|----------|------|
| `DB_ROOT_PASSWORD` | MySQL root 帳號密碼 |
| `DB_PASSWORD` | 應用程式使用的 DB 帳號密碼 |
| `JWT_SECRET` | JWT 簽名金鑰（Base64 編碼，建議長度大於 256 bits） |

### GitHub Secrets（自動部署使用）

| Secret 名稱 | 說明 |
|-------------|------|
| `VPS_HOST` / `VPS_USERNAME` / `VPS_SSH_KEY` | 伺服器 IP、登入帳號、SSH 私鑰內容 |
| `DB_ROOT_PASSWORD` / `DB_PASSWORD` | 遠端 MySQL 密碼 |
| `JWT_SECRET` | 正式環境 JWT 簽名金鑰 |

-----

## 關於作者

本專案為全端開發能力的完整展示，涵蓋後端 API 系統設計、安全認證實作、前端狀態管理，以及 DevOps 容器化與 CI/CD 持續交付的實踐。歡迎透過 Issue 或 PR 提出建議與回饋！