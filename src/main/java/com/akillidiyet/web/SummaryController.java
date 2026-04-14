package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.security.AppUserDetails;
import com.akillidiyet.service.CurrentUserService;
import com.akillidiyet.service.DailySummaryService;
import com.akillidiyet.service.dto.DailySummaryDto;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final CurrentUserService currentUserService;
    private final DailySummaryService dailySummaryService;

    @GetMapping
    public DailySummaryDto summary(
            @AuthenticationPrincipal AppUserDetails details,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AppUser u = currentUserService.require(details);
        LocalDate d = date != null ? date : LocalDate.now();
        return dailySummaryService.summarize(u, d);
    }
}
