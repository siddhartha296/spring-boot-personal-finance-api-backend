package io.aiven.spring.mysql.demo_siddu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Find all expenses by user
    List<Expense> findByUserId(Long userId);

    // Find expenses by user and category
    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    // Find expenses by user within date range
    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // Find expenses by user and payment method
    List<Expense> findByUserIdAndPaymentMethod(Long userId, String paymentMethod);

    // Calculate total expenses for a user
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpensesByUser(@Param("userId") Long userId);

    // Calculate total expenses for a user by category
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId")
    BigDecimal getTotalExpensesByUserAndCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    // Calculate total expenses for a user within date range
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByUserAndDateRange(@Param("userId") Long userId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // Get expenses by user ordered by date (most recent first)
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);
}