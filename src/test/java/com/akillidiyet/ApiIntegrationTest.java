package com.akillidiyet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.akillidiyet.repo.PasswordResetTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Test
    void forgotAndResetPassword() throws Exception {
        String email = "pw-" + System.nanoTime() + "@example.com";
        String reg =
                """
                {"email":"%s","password":"password12","displayName":"PW"}
                """
                        .formatted(email);
        mockMvc
                .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(reg))
                .andExpect(status().isCreated());

        mockMvc
                .perform(
                        post("/api/auth/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isNoContent());

        String token =
                passwordResetTokenRepository
                        .findFirstByUser_EmailIgnoreCaseOrderByIdDesc(email)
                        .orElseThrow()
                        .getToken();

        mockMvc
                .perform(
                        post("/api/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"token\":\""
                                                + token
                                                + "\",\"newPassword\":\"newpassword99\"}"))
                .andExpect(status().isNoContent());

        mockMvc
                .perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\":\""
                                                + email
                                                + "\",\"password\":\"newpassword99\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void foodsLogsAndCustomFoodFlow() throws Exception {
        String email = "itest-" + System.nanoTime() + "@example.com";
        String regJson =
                """
                {"email":"%s","password":"password12","displayName":"ITest"}
                """
                        .formatted(email);

        MvcResult reg = mockMvc
                .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(regJson))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode regNode = objectMapper.readTree(reg.getResponse().getContentAsString());
        String token = regNode.get("token").asText();
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/foods").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].custom").value(false));

        String today = LocalDate.now().toString();
        String createFood =
                """
                {"name":"Ev yapımı bar","caloriesPer100g":400,"proteinPer100g":15,"carbsPer100g":50,"fatPer100g":12}
                """;

        MvcResult foodRes = mockMvc
                .perform(
                        post("/api/foods")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createFood))
                .andExpect(status().isCreated())
                               .andExpect(jsonPath("$.custom").value(true))
                .andExpect(jsonPath("$.usedInLogs").value(false))
                .andReturn();
        long customFoodId = objectMapper.readTree(foodRes.getResponse().getContentAsString()).get("id").asLong();

        mockMvc
                .perform(get("/api/foods/mine").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ev yapımı bar"))
                .andExpect(jsonPath("$[0].usedInLogs").value(false));

        String addLog =
                """
                {"date":"%s","mealType":"LUNCH","foodId":%d,"grams":50}
                """
                        .formatted(today, customFoodId);

        mockMvc
                .perform(
                        post("/api/logs/food")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addLog))
                .andExpect(status().isCreated());

        mockMvc
                .perform(get("/api/foods/mine").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usedInLogs").value(true));

        MvcResult listRes = mockMvc
                .perform(
                        get("/api/logs/food")
                                .param("date", today)
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].foodName").value("Ev yapımı bar"))
                .andReturn();
        long entryId = objectMapper.readTree(listRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        mockMvc
                .perform(
                        delete("/api/foods/" + customFoodId).header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());

        String patchJson =
                """
                {"date":"%s","mealType":"DINNER","foodId":%d,"grams":60}
                """
                        .formatted(today, customFoodId);

        mockMvc
                .perform(
                        patch("/api/logs/food/" + entryId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(patchJson))
                .andExpect(status().isNoContent());

        mockMvc
                .perform(delete("/api/logs/food/" + entryId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc
                .perform(delete("/api/foods/" + customFoodId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void foodsRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/foods")).andExpect(status().isForbidden());
    }

    @Test
    void authApiGetRedirectsToFrontend() throws Exception {
        mockMvc.perform(get("/api/auth/register")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("http://localhost:5173/register"));
        mockMvc.perform(get("/api/auth/login")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("http://localhost:5173/login"));
        mockMvc
                .perform(get("/api/auth/reset-password").param("token", "abc/def"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost:5173/reset-password?token=abc%2Fdef"));
    }

    @Test
    void registerDuplicateEmailReturnsConflict() throws Exception {
        String email = "dup-" + System.nanoTime() + "@example.com";
        String body =
                """
                {"email":"%s","password":"password12","displayName":"Dup"}
                """
                        .formatted(email);
        mockMvc
                .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc
                .perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }
}
