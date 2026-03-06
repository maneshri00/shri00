package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.entity.User;
import com.productivity_mangement.productivity.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean handleLogin(String email) {

        return userRepository.findByEmail(email)
                .map(user -> {

                    if (user.isFirstLogin()) {
                        user.setFirstLogin(false);
                        userRepository.save(user);
                    }
                    return false;
                })
                .orElseGet(() -> {

                    User user = new User();
                    user.setEmail(email);
                    user.setFirstLogin(true);
                    userRepository.save(user);
                    return true;
                });
    }

}
