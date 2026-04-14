package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.repo.AppUserRepository;
import com.akillidiyet.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final AppUserRepository userRepository;

    public AppUser require(AppUserDetails details) {
        return userRepository
                .findById(details.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Oturum geçersiz"));
    }
}
