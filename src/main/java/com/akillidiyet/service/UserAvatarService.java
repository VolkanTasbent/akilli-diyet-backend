package com.akillidiyet.service;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.repo.AppUserRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final AppUserRepository userRepository;

    @Value("${app.avatar.storage-dir}")
    private String storageDirRaw;

    @Value("${app.avatar.max-bytes:2097152}")
    private long maxBytes;

    private Path storageRoot() {
        return Paths.get(storageDirRaw).toAbsolutePath().normalize();
    }

    private Path fileForUser(Long userId) {
        return storageRoot().resolve(userId.toString());
    }

    @Transactional
    public void saveAvatar(AppUser user, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya seçilmedi.");
        }
        long userId = user.getId();
        AppUser managed =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Oturum geçersiz"));
        long size = file.getSize();
        if (size > maxBytes) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Resim en fazla " + (maxBytes / 1024) + " KB olabilir.");
        }
        String normalizedType = normalizeContentType(file.getContentType());
        if (!ALLOWED_TYPES.contains(normalizedType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "İzin verilen formatlar: JPEG, PNG, WebP, GIF.");
        }
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya okunamadı.");
        }
        if (data.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya boş.");
        }
        try {
            Files.createDirectories(storageRoot());
            Path target = fileForUser(userId);
            Files.write(target, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resim kaydedilemedi.");
        }
        managed.setAvatarMediaType(normalizedType);
        managed.setAvatarUpdatedAt(Instant.now());
        userRepository.save(managed);
    }

    public byte[] loadAvatarBytes(AppUser user) {
        if (user.getAvatarMediaType() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Path p = fileForUser(user.getId());
        if (!Files.isRegularFile(p)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        try {
            return Files.readAllBytes(p);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void deleteAvatar(AppUser user) {
        long userId = user.getId();
        AppUser managed =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Oturum geçersiz"));
        try {
            Files.deleteIfExists(fileForUser(userId));
        } catch (IOException ignored) {
            // disk temizlenemese bile kayıt sıfırlanır
        }
        managed.setAvatarMediaType(null);
        managed.setAvatarUpdatedAt(null);
        userRepository.save(managed);
    }

    private static String normalizeContentType(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String base = raw.toLowerCase(Locale.ROOT).split(";", 2)[0].trim();
        if ("image/jpg".equals(base)) {
            return "image/jpeg";
        }
        return base;
    }
}
