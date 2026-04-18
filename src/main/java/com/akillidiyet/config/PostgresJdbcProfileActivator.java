package com.akillidiyet.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Render vb. ortamlarda SPRING_DATASOURCE_URL (jdbc:postgresql) verilip profil unutulursa tablolar
 * Neon'da oluşmaz. Bu sınıf Postgres JDBC URL'si görülünce {@code prod} profilini ekler.
 */
public class PostgresJdbcProfileActivator implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = environment.getProperty("SPRING_DATASOURCE_URL");
        if (url == null || url.isBlank()) {
            return;
        }
        String t = url.trim();
        if (!t.startsWith("jdbc:postgresql:") && !t.startsWith("jdbc:postgres:")) {
            return;
        }
        for (String p : environment.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(p)) {
                return;
            }
        }
        environment.addActiveProfile("prod");
    }
}
