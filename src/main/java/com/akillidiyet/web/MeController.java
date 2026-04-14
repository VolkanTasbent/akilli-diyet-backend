package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.security.AppUserDetails;
import com.akillidiyet.service.CurrentUserService;
import com.akillidiyet.service.UserProfileService;
import com.akillidiyet.web.dto.UpdateProfileRequest;
import com.akillidiyet.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final CurrentUserService currentUserService;
    private final UserProfileService profileService;

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal AppUserDetails details) {
        AppUser u = currentUserService.require(details);
        return UserMapper.toResponse(u);
    }

    @PatchMapping
    public UserResponse update(
            @AuthenticationPrincipal AppUserDetails details, @RequestBody UpdateProfileRequest body) {
        AppUser u = currentUserService.require(details);
        return profileService.updateProfile(u, body);
    }
}
