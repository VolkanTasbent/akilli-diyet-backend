# Akıllı Diyet — Backend

Spring Boot 3.4, Java 17, JPA, JWT, H2 (geliştirme) veya PostgreSQL.

## Çalıştırma

```bash
mvn spring-boot:run
```

PostgreSQL için: `docker compose up -d` ardından `SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run`

Üretimde `JWT_SECRET` ortam değişkenini en az 32 karakter olacak şekilde ayarlayın. Tüm değişkenler için bkz. [docs/ENVIRONMENT.md](../docs/ENVIRONMENT.md).
