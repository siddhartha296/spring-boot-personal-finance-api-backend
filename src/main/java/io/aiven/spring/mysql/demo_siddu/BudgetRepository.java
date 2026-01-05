package io.aiven.spring.mysql.demo_siddu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Find all budgets by user
    List<Budget> findByUserId(Long userId);

    // Find budgets by user and category
    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);

    // Find active budgets for a user (current date between start and end)
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND :currentDate BETWEEN b.startDate AND b.endDate")
    List<Budget> findActiveBudgetsByUser(@Param("userId") Long userId, @Param("currentDate") LocalDate currentDate);

    // Find active budget for a specific category
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.category.id = :categoryId AND :currentDate BETWEEN b.startDate AND b.endDate")
    Budget findActiveBudgetByUserAndCategory(@Param("userId") Long userId,
                                             @Param("categoryId") Long categoryId,
                                             @Param("currentDate") LocalDate currentDate);
}