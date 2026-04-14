package com.akillidiyet.web;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.security.AppUserDetails;
import com.akillidiyet.service.CurrentUserService;
import com.akillidiyet.service.FoodCatalogService;
import com.akillidiyet.web.dto.CreateFoodRequest;
import com.akillidiyet.web.dto.FoodResponse;
import com.akillidiyet.web.dto.UpdateFoodRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final CurrentUserService currentUserService;
    private final FoodCatalogService foodCatalogService;

    @GetMapping("/mine")
    public List<FoodResponse> listMine(@AuthenticationPrincipal AppUserDetails details) {
        AppUser u = currentUserService.require(details);
        return foodCatalogService.listMine(u);
    }

    @GetMapping
    public List<FoodResponse> search(
            @AuthenticationPrincipal AppUserDetails details, @RequestParam(required = false, defaultValue = "") String q) {
        AppUser u = currentUserService.require(details);
        return foodCatalogService.search(u, q);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse create(
            @AuthenticationPrincipal AppUserDetails details, @Valid @RequestBody CreateFoodRequest body) {
        AppUser u = currentUserService.require(details);
        return foodCatalogService.createCustom(u, body);
    }

    @PutMapping("/{id}")
    public FoodResponse update(
            @AuthenticationPrincipal AppUserDetails details,
            @PathVariable Long id,
            @Valid @RequestBody UpdateFoodRequest body) {
        AppUser u = currentUserService.require(details);
        return foodCatalogService.updateCustom(u, id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AppUserDetails details, @PathVariable Long id) {
        AppUser u = currentUserService.require(details);
        foodCatalogService.deleteCustom(u, id);
    }
}
