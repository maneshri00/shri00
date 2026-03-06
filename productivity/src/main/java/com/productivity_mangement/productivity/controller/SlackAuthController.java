package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.helper.SessionUser;
import com.productivity_mangement.productivity.service.SlackOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/slack")
public class SlackAuthController {

    private final SlackOAuthService slackOAuthService;
    private final SessionUser sessionUser;

    public SlackAuthController(
            SlackOAuthService slackOAuthService,
            SessionUser sessionUser
    ) {
        this.slackOAuthService = slackOAuthService;
        this.sessionUser = sessionUser;
    }

    /**
     * STEP 1
     * Start Slack OAuth flow
     * URL → http://localhost:8080/auth/slack
     */
    @GetMapping
    public void startAuth(HttpServletResponse response) throws Exception {

        String email = sessionUser.getEmail();

        if (email == null || email.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "User must be logged in before connecting Slack"
            );
            return;
        }

        // Generate Slack authorization URL
        String authUrl = slackOAuthService.buildAuthUrl();

        // Redirect user to Slack login
        response.sendRedirect(authUrl);
    }

    /**
     * STEP 2
     * Slack redirects here after user authorizes app
     *
     * Example:
     * /auth/slack/callback?code=xxxx
     */
    @GetMapping("/callback")
    public void callback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            HttpServletResponse response
    ) throws Exception {

        if (error != null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Slack authorization failed: " + error
            );
            return;
        }

        if (code == null || code.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Slack OAuth did not return authorization code"
            );
            return;
        }

        String email = sessionUser.getEmail();

        if (email == null || email.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "User session not found"
            );
            return;
        }

        // Exchange Slack code for access token
        slackOAuthService.exchangeCode(code, email);

        // Redirect back to frontend dashboard
        response.sendRedirect("http://localhost:5173/dashboard/work");
    }
}