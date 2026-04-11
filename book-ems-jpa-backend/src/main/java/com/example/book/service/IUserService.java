package com.example.book.service;

import com.example.book.dto.UserDTO;
import com.example.book.entity.User;

import java.util.List;

public interface IUserService {
    void updateUserRole(Long userId, String roleName);
    List<User> getAllUsers();
    UserDTO getUserById(Long id);
    User findByUsername(String username);
    void registerUser(String username, String email, String password, String realName);
}