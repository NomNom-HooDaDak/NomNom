package com.p1.nomnom.security.userdetails;

import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        return new com.p1.nomnom.security.userdetails.UserDetailsImpl(user);
    }

    // RefreshToken 기반으로 유저 정보 로드
    public UserDetails loadUserByRefreshToken(String refreshToken) {
        Optional<User> userOptional = userRepository.findByRefreshToken(refreshToken);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Invalid Refresh Token");
        }
        return new com.p1.nomnom.security.userdetails.UserDetailsImpl(userOptional.get());
    }
}
