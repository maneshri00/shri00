package com.productivity_mangement.productivity.controller;

import com.productivity_mangement.productivity.service.GitHubOAuthService;
import com.productivity_mangement.productivity.service.JiraOAuthService;
import com.productivity_mangement.productivity.helper.SessionUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class IntegrationController {

    private final GitHubOAuthService gitHubOAuthService;
    private final JiraOAuthService jiraOAuthService;
    private final SessionUser sessionUser;

    public IntegrationController(GitHubOAuthService gitHubOAuthService,
                                 JiraOAuthService jiraOAuthService,
                                 SessionUser sessionUser) {
        this.gitHubOAuthService = gitHubOAuthService;
        this.jiraOAuthService = jiraOAuthService;
        this.sessionUser = sessionUser;
    }

    @GetMapping("/github")
    public String githubStart() {
        return gitHubOAuthService.buildAuthUrl();
    }

    @GetMapping("/github/callback")
    public String githubCallback(@RequestParam String code) throws Exception {
        String email = sessionUser.getEmail();
        gitHubOAuthService.exchangeCode(code, email);
        return "GitHub connected";
    }

    @GetMapping("/jira")
    public String jiraStart() {
        return jiraOAuthService.buildAuthUrl();
    }

    @GetMapping("/jira/callback")
    public String jiraCallback(@RequestParam String code) throws Exception {
        String email = sessionUser.getEmail();
        jiraOAuthService.exchangeCode(code, email);
        return "Jira connected";
    }
}