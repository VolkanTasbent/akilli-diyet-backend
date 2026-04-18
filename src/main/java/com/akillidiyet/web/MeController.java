package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.security.AppUserDetails;
import com.akillidiyet.service.CurrentUserService;
import com.akillidiyet.service.UserAvatarService;
import com.akillidiyet.service.UserProfileService;
import com.akillidiyet.web.dto.UpdateProfileRequest;
import com.akillidiyet.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final CurrentUserService currentUserService;
    private final UserProfileService profileService;
    private final UserAvatarService avatarService;

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

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> avatar(@AuthenticationPrincipal AppUserDetails details) {
        AppUser u = currentUserService.require(details);
        if (u.getAvatarMediaType() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = avatarService.loadAvatarBytes(u);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(u.getAvatarMediaType()))
                .body(bytes);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse uploadAvatar(
            @AuthenticationPrincipal AppUserDetails details, @RequestParam("file") MultipartFile file) {
        AppUser u = currentUserService.require(details);
        avatarService.saveAvatar(u, file);
        return UserMapper.toResponse(currentUserService.require(details));
    }

    @DeleteMapping("/avatar")
    public UserResponse deleteAvatar(@AuthenticationPrincipal AppUserDetails details) {
        AppUser u = currentUserService.require(details);
        avatarService.deleteAvatar(u);
        return UserMapper.toResponse(currentUserService.require(details));
    }
}
