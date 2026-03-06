package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.service.GoogleOAuthService;
import com.productivity_mangement.productivity.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final GoogleOAuthService oauthService;
    private final UserService userService;

    public AuthController(GoogleOAuthService oauthService, UserService userService) {
        this.oauthService = oauthService;
        this.userService = userService;
    }

    @GetMapping("/auth/google")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect(oauthService.buildAuthUrl());
    }

    @GetMapping("/auth/google/callback")
    public void callback(
            @RequestParam String code,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, GeneralSecurityException {

        String email = oauthService.exchangeCode(code);


        boolean isNewUser = userService.handleLogin(email);

        request.getSession().setAttribute("email", email);
        request.getSession().setAttribute("isNewUser", isNewUser);

        response.sendRedirect("http://localhost:5173/dashboard");
    }


    @GetMapping("/api/user/status")
    public Map<String, Boolean> getUserStatus(HttpServletRequest request) {
        Boolean isNewUser = (Boolean) request.getSession().getAttribute("isNewUser");

        Map<String, Boolean> response = new HashMap<>();
        response.put("isNewUser", isNewUser != null && isNewUser);

        return response;
    }
    @PostMapping("/login")
    public String login(@RequestParam String email) {

        boolean firstLogin = userService.handleLogin(email);

        return firstLogin
                ? "Welcome! First login detected"
                : "Welcome back!";
    }


}
