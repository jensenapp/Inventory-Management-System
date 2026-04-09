// file: src/main/java/net/javaguides/banking/dto/UserDTO.java
package com.example.book.dto;

import com.example.book.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String email;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private Role role;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}