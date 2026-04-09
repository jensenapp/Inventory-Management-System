// file: src/main/java/com/example/book/security/response/UserInfoResponse.java
package com.example.book.security.response;

import java.util.List;

public record UserInfoResponse(
        Long id,
        String username,
        String email,
        boolean accountNonLocked,
        boolean accountNonExpired,
        boolean credentialsNonExpired,
        boolean enabled,
        List<String> roles) {
}