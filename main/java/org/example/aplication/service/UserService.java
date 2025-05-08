package org.example.otp.service;

import org.example.otp.dao.UserRepository;
import org.example.otp.entity.User;  // Теперь импортируем наш User
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login '" + login + "' not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByLogin(username);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getEncryptedPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
