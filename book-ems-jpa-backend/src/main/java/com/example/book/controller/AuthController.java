// file: src/main/java/com/example/book/controller/AuthController.java
package com.example.book.controller;

import com.example.book.entity.AppRole;
import com.example.book.entity.Role;
import com.example.book.entity.User;
import com.example.book.repository.RoleRepository;
import com.example.book.repository.UserRepository;
import com.example.book.security.jwt.JwtUtils;
import com.example.book.security.request.LoginRequest;
import com.example.book.security.request.SignupRequest;
import com.example.book.security.response.LoginResponse;
import com.example.book.security.response.MessageResponse;
import com.example.book.security.response.UserInfoResponse;
import com.example.book.security.services.UserDetailsImpl;
import com.example.book.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "使用者認證相關 API")
@RequiredArgsConstructor // 改用建構子注入，捨棄 @Autowired
public class AuthController {

    private final JwtUtils jwtUtils;

    private final IUserService userService; // 注意：這裡要注入介面，而不是實作類別
    private final AuthenticationManager authenticationManager;

    @PostMapping("/public/signin")
    @Operation(summary = "使用者登入", description = "驗證使用者帳密並回傳 JWT Token", responses = {
            @ApiResponse(responseCode = "200", description = "登入成功"),
            @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤")
    })
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 使用 Record 建立回應物件
        LoginResponse response = new LoginResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles,
                jwtToken
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/signup")
    @Operation(summary = "註冊新使用者")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.registerUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getRealName()
        );
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/user")
    @Operation(summary = "取得當前使用者資訊", description = "需攜帶 JWT Token，回傳完整的 User 詳細資料")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得資訊", content = @Content(schema = @Schema(implementation = UserInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "未授權 (Token 無效或過期)")
    })
    public ResponseEntity<UserInfoResponse> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                roles
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    @Operation(summary = "取得當前使用者名稱", description = "簡易測試端點，需攜帶 JWT Token")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "回傳使用者名稱字串")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails != null ? userDetails.getUsername() : "";
    }
}