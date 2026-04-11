// file: src/main/java/com/example/book/security/SecurityConfig.java
package com.example.book.security;

import com.example.book.entity.AppRole;
import com.example.book.entity.Role;
import com.example.book.entity.User;
import com.example.book.repository.RoleRepository;
import com.example.book.repository.UserRepository;
import com.example.book.security.jwt.AuthEntryPointJwt;
import com.example.book.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.book.entity.Publisher;
import com.example.book.repository.PublisherRepository;
import java.util.List;
import com.example.book.entity.Book;
import com.example.book.repository.BookRepository;
import java.time.LocalDate;


@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor // 自動注入需要的元件
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter; // 直接注入，不用再寫 @Bean new AuthTokenFilter()

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 1. 關閉 CSRF 與啟用 CORS
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());

        // 2. 設定 HTTP 請求的授權規則
        http.authorizeHttpRequests((requests) -> requests
                // 開放註冊與登入的 API，以及 Swagger 相關文件
                .requestMatchers("/api/auth/public/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // 其他所有請求都需要登入驗證
                .anyRequest().authenticated()
        );

        // 3. 允許 H2 Console 的 Frame 顯示
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // 4. 將 Session 管理設為無狀態 (Stateless)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 5. 設定例外處理與自訂 JWT 過濾器
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173","https://ems.jensen-store.online"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      PublisherRepository publisherRepository) {
        return args -> {
            // 初始化角色
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            // 初始化預設使用者 user1
            if (!userRepository.existsByUsername("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"), "tommy");
                user1.setRole(userRole);
                userRepository.save(user1);
            }

            // 初始化預設管理員 admin
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"), "momo");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

            // 初始化預設的出版社
            if (publisherRepository.count() == 0) {
                Publisher p1 = new Publisher();
                p1.setPublisherName("碁峰資訊");
                publisherRepository.save(p1);

                Publisher p2 = new Publisher();
                p2.setPublisherName("歐萊禮 (O'Reilly)");
                publisherRepository.save(p2);
            }

            // 書本初始化 (如果書本資料表是空的，就自動塞入 10 本書)
            if (bookRepository.count() == 0) {
                Book b1 = new Book();
                b1.setTitle("Spring Boot 3 實戰開發");
                b1.setAuthor("王大明");
                b1.setPrice(650);
                b1.setPublishDate(LocalDate.parse("2023-11-15"));
                b1.setPublisher(p1);
                bookRepository.save(b1);

                Book b2 = new Book();
                b2.setTitle("React 學習手冊 (第二版)");
                b2.setAuthor("Alex Banks");
                b2.setPrice(720);
                b2.setPublishDate(LocalDate.parse("2022-05-20"));
                b2.setPublisher(p2);
                bookRepository.save(b2);

                Book b3 = new Book();
                b3.setTitle("Docker 容器化架構指南");
                b3.setAuthor("陳小華");
                b3.setPrice(580);
                b3.setPublishDate(LocalDate.parse("2023-08-10"));
                b3.setPublisher(p1);
                bookRepository.save(b3);

                Book b4 = new Book();
                b4.setTitle("Java 深入淺出 (第三版)");
                b4.setAuthor("Kathy Sierra");
                b4.setPrice(850);
                b4.setPublishDate(LocalDate.parse("2021-03-12"));
                b4.setPublisher(p2);
                bookRepository.save(b4);

                Book b5 = new Book();
                b5.setTitle("JavaScript 犀牛書");
                b5.setAuthor("David Flanagan");
                b5.setPrice(1200);
                b5.setPublishDate(LocalDate.parse("2020-09-05"));
                b5.setPublisher(p2);
                bookRepository.save(b5);

                Book b6 = new Book();
                b6.setTitle("Kubernetes 權威指南");
                b6.setAuthor("李四");
                b6.setPrice(900);
                b6.setPublishDate(LocalDate.parse("2023-12-01"));
                b6.setPublisher(p1);
                bookRepository.save(b6);

                Book b7 = new Book();
                b7.setTitle("Python 網路爬蟲實戰");
                b7.setAuthor("趙六");
                b7.setPrice(450);
                b7.setPublishDate(LocalDate.parse("2022-11-28"));
                b7.setPublisher(p1);
                bookRepository.save(b7);

                Book b8 = new Book();
                b8.setTitle("雲端架構設計與實務");
                b8.setAuthor("林阿呆");
                b8.setPrice(780);
                b8.setPublishDate(LocalDate.parse("2024-01-10"));
                b8.setPublisher(p2);
                bookRepository.save(b8);

                Book b9 = new Book();
                b9.setTitle("SQL 效能調校聖經");
                b9.setAuthor("張三");
                b9.setPrice(600);
                b9.setPublishDate(LocalDate.parse("2019-07-22"));
                b9.setPublisher(p1);
                bookRepository.save(b9);

                Book b10 = new Book();
                b10.setTitle("演算法圖鑑");
                b10.setAuthor("Aditya Bhargava");
                b10.setPrice(480);
                b10.setPublishDate(LocalDate.parse("2018-04-15"));
                b10.setPublisher(p2);
                bookRepository.save(b10);
            }
        };
    }
}