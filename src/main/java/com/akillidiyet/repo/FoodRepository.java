package com.akillidiyet.repo;

import com.akillidiyet.domain.AppUser;
import com.akillidiyet.domain.Food;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findTop50ByOwnerIsNullOrderByNameAsc();

    List<Food> findTop50ByOwnerOrderByNameAsc(AppUser owner);

    List<Food> findTop50ByOwnerIsNullAndNameContainingIgnoreCaseOrderByNameAsc(String namePart);

    List<Food> findTop50ByOwnerAndNameContainingIgnoreCaseOrderByNameAsc(AppUser owner, String namePart);

    List<Food> findByOwnerOrderByNameAsc(AppUser owner);

    List<Food> findByOwnerIsNullOrderByNameAsc();

    List<Food> findByOwnerIsNullAndNameContainingIgnoreCaseOrderByNameAsc(String namePart);

    List<Food> findByOwnerAndNameContainingIgnoreCaseOrderByNameAsc(AppUser owner, String namePart);

    boolean existsByOwnerIsNullAndNameIgnoreCase(String name);

    Optional<Food> findByOwnerIsNullAndNameIgnoreCase(String name);

    long countByOwnerIsNull();
}
