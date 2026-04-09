// file: src/main/java/com/example/book/security/request/LoginRequest.java
package com.example.book.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // @Data 已包含 Getter, Setter, RequiredArgsConstructor, ToString, EqualsAndHashCode
public class LoginRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}