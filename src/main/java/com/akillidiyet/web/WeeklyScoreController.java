package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.security.AppUserDetails;
import com.akillidiyet.service.CurrentUserService;
import com.akillidiyet.service.WeeklyScoreService;
import com.akillidiyet.service.dto.WeeklyScoreDto;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/weekly-score")
@RequiredArgsConstructor
public class WeeklyScoreController {

    private final CurrentUserService currentUserService;
    private final WeeklyScoreService weeklyScoreService;

    @GetMapping
    public WeeklyScoreDto weeklyScore(
            @AuthenticationPrincipal AppUserDetails details,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        AppUser u = currentUserService.require(details);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        return weeklyScoreService.compute(u, end);
    }
}
