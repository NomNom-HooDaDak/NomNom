package com.p1.nomnom.security.userdetails;

import com.p1.nomnom.user.entity.RefreshToken;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        return new UserDetailsImpl(user);
    }

    // RefreshToken 기반으로 유저 정보 불러오기
    public UserDetails loadUserByRefreshToken(String refreshToken) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            throw new UsernameNotFoundException("Invalid Refresh Token");
        }
        User user = userRepository.findByUsername(storedToken.get().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found for Refresh Token"));

        return new UserDetailsImpl(user);
    }
}
