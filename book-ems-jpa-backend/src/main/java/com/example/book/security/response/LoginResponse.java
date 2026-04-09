package com.example.book.security.response;

import java.util.List;


public record LoginResponse(Long id, String username, List<String> roles, String jwtToken) {
}