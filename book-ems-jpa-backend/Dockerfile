```dockerfile
# ============================================================
# Stage 1: Build the application
# ============================================================
# 使用 Maven + Java 17 的 Docker Image 作為「建置環境」
#
# maven:3.9.4
#   → Maven 版本 3.9.4
#
# eclipse-temurin-17
#   → 使用 Eclipse Temurin Java 17
#
# alpine
#   → 使用 Alpine Linux，Image 相對輕量
#
# AS build
#   → 將這個 Stage 命名為「build」
#   → 後面的 Stage 可以透過 --from=build 從這裡複製檔案
#
# 這個 Stage 的主要工作：
#   Spring Boot 原始碼
#       ↓
#   Maven 編譯 / 打包
#       ↓
#   產生 target/*.jar
#
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build


# ------------------------------------------------------------
# 設定 Docker Container 裡面的工作目錄
# ------------------------------------------------------------
# 後續的 COPY、RUN 等指令，
# 如果沒有特別指定路徑，就會以 /app 作為目前工作目錄。
#
# 可以把它想成：
#
#     cd /app
#
# 之後的檔案結構會類似：
#
# /app
# ├── pom.xml
# └── src/
#
WORKDIR /app


# ------------------------------------------------------------
# 複製 pom.xml 到 Docker Container
# ------------------------------------------------------------
# 語法：
#
#     COPY <來源> <目的地>
#
# 第一個：
#     pom.xml
#     → 本機專案的 pom.xml
#
# 第二個：
#     .
#     → 目前 WORKDIR，也就是 /app
#
# 所以最後會變成：
#
#     /app/pom.xml
#
# 為什麼這裡先只 COPY pom.xml？
#
# 因為 pom.xml 定義了 Maven dependencies。
# 例如：
#
#     Spring Boot
#     Spring Security
#     Spring Data JPA
#     MySQL Driver
#     JWT
#     Lombok
#     JUnit
#     Mockito
#     ...
#
# Docker 可以利用 Layer Cache：
#
# 如果 pom.xml 沒有修改，
# 後面的 dependencies layer 就可以重複使用，
# 不需要每次 Docker build 都重新下載所有 dependencies。
#
COPY pom.xml .


# ------------------------------------------------------------
# 預先下載 Maven dependencies
# ------------------------------------------------------------
# 執行：
#
#     mvn dependency:go-offline
#
# Maven 會根據 pom.xml，
# 將後續建置需要的 dependencies 預先下載。
#
# 例如：
#
#     pom.xml
#         ↓
#     Maven 分析 dependencies
#         ↓
#     下載 Spring Boot / Hibernate / MySQL Driver / JWT...
#
# 這樣做的主要目的：
#
#     1. 提前下載 dependencies
#     2. 利用 Docker Layer Cache
#     3. 後續只修改 src/ 時，可以減少重新下載 dependencies 的時間
#
RUN mvn dependency:go-offline


# ------------------------------------------------------------
# 複製 Spring Boot 原始碼
# ------------------------------------------------------------
# 將本機的：
#
#     src/
#
# 複製到 Docker Container：
#
#     /app/src/
#
# 現在 Docker Container 裡面大致是：
#
# /app
# ├── pom.xml
# └── src/
#     ├── main/
#     └── test/
#
# 此時 Maven 已經取得：
#
#     pom.xml
#     +
#     src/
#     +
#     Maven dependencies
#
# 已經具備完整的 Spring Boot 專案。
#
COPY src ./src


# ------------------------------------------------------------
# 使用 Maven 編譯並打包 Spring Boot Application
# ------------------------------------------------------------
# 執行：
#
#     mvn clean package -DskipTests
#
# clean
#   → 清除之前產生的 target/ 目錄與舊的 build artifacts
#
# package
#   → 執行 Maven build lifecycle，
#     最後將 Spring Boot Application 打包成 JAR
#
# 最後通常會產生：
#
#     /app/target/xxx.jar
#
# 例如：
#
#     /app/target/banking-app-0.0.1-SNAPSHOT.jar
#
# -DskipTests
#   → 建置 JAR，但跳過「執行測試」
#
# 注意：
#
# 這不代表專案不需要測試。
#
# 實務上可以讓：
#
#     GitHub Actions CI
#         ↓
#     mvn test
#         ↓
#     測試通過
#         ↓
#     Docker build
#         ↓
#     建立 Image
#
# 也就是把「測試」和「Docker Image 建置」分開。
#
RUN mvn clean package -DskipTests



# ============================================================
# Stage 2: Run the application
# ============================================================
# 開始第二個 Stage。
#
# 這裡不再使用 Maven。
#
# 因為 Maven 只需要在 Stage 1：
#
#     編譯 + 打包
#
# Stage 2 的目的只是：
#
#     執行 Spring Boot Application
#
# 因此只需要 Java Runtime Environment (JRE) 即可。
#
# 使用：
#
#     Eclipse Temurin Java 17 JRE
#     +
#     Alpine Linux
#
# 相較 Stage 1：
#
#     Stage 1 → Maven + JDK → 負責 Build
#     Stage 2 → JRE         → 負責 Run
#
# 這就是 Docker 的 Multi-stage Build。
#
FROM eclipse-temurin:17-jre-alpine


# ------------------------------------------------------------
# 設定第二個 Stage 的工作目錄
# ------------------------------------------------------------
# Container 裡面使用：
#
#     /app
#
# 所以最後 Application 會放在：
#
#     /app/app.jar
#
WORKDIR /app


# ------------------------------------------------------------
# 從 Stage 1 複製打包完成的 JAR
# ------------------------------------------------------------
# --from=build
#   → 指定從第一個 Stage「build」取得檔案
#
# /app/target/*.jar
#   → Stage 1 裡 Maven 產生的 JAR
#
# app.jar
#   → 複製到 Stage 2 後重新命名為 app.jar
#
# 也就是：
#
# Stage 1
#
# /app/target/banking-app-0.0.1-SNAPSHOT.jar
#                       │
#                       │ COPY --from=build
#                       ↓
#
# Stage 2
#
# /app/app.jar
#
# 注意：
#
# Stage 2 不需要：
#
#     pom.xml
#     src/
#     Maven
#     Maven dependencies
#
# 它只需要執行 Application 所需要的 JAR。
#
COPY --from=build /app/target/*.jar app.jar


# ------------------------------------------------------------
# 宣告 Container 使用的 Port
# ------------------------------------------------------------
# Spring Boot 預設使用：
#
#     8080
#
# 所以這裡宣告：
#
#     Container 內的 Application 使用 8080 Port
#
# 注意：
#
# EXPOSE 不是「把 Port 對外開放」。
#
# 它比較像是在 Dockerfile 中宣告：
#
#     「這個 Application 預期會使用 8080」
#
# 真正的 Port Mapping 通常在 docker run 或 docker-compose.yml
# 中設定，例如：
#
#     ports:
#       - "8080:8080"
#
# 代表：
#
#     主機 8080
#          ↓
#     Container 8080
#
EXPOSE 8080


# ------------------------------------------------------------
# Container 啟動時執行的主要指令
# ------------------------------------------------------------
# Container 啟動後執行：
#
#     java -jar app.jar
#
# 也就是啟動 Spring Boot Application。
#
# 啟動流程：
#
#     Docker Container 啟動
#             ↓
#     java -jar app.jar
#             ↓
#     Spring Boot 啟動
#             ↓
#     Embedded Tomcat 啟動
#             ↓
#     Application Listening on 8080
#
# 使用 JSON Array / Exec Form：
#
#     ["java", "-jar", "app.jar"]
#
# 而不是：
#
#     "java -jar app.jar"
#
# Exec Form 是 Docker 常見的推薦寫法，
# 可以讓 Java 程序直接成為 Container 的主要程序。
#
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 最後把整份 Dockerfile 濃縮成一張圖

```text
                    Dockerfile
                         │
                         ▼
              ┌─────────────────────┐
              │ Stage 1: BUILD      │
              │                     │
              │ Maven + JDK 17      │
              │                     │
              │ pom.xml             │
              │       ↓             │
              │ dependencies       │
              │       ↓             │
              │ src/                │
              │       ↓             │
              │ mvn package         │
              │       ↓             │
              │ target/app.jar      │
              └──────────┬──────────┘
                         │
                  COPY --from=build
                         │
                         ▼
              ┌─────────────────────┐
              │ Stage 2: RUN       │
              │                     │
              │ Java 17 JRE         │
              │                     │
              │ app.jar             │
              │       ↓             │
              │ java -jar app.jar   │
              │       ↓             │
              │ Spring Boot         │
              │       ↓             │
              │ Port 8080           │
              └─────────────────────┘
```

**目前最需要熟悉**

```text
Dockerfile
   ↓
Stage 1：Build
   ↓
Maven + Java 17
   ↓
pom.xml + src/
   ↓
產生 JAR
   ↓
Stage 2：Run
   ↓
Java 17 JRE
   ↓
app.jar
   ↓
java -jar app.jar
   ↓
Spring Boot :8080
```

