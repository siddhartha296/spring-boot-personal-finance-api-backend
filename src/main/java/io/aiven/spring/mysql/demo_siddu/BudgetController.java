package io.aiven.spring.mysql.demo_siddu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // Create new budget
    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody BudgetRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());

            if (!user.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            if (!category.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
            }

            Budget budget = new Budget();
            budget.setUser(user.get());
            budget.setCategory(category.get());
            budget.setAmount(request.getAmount());
            budget.setStartDate(request.getStartDate());
            budget.setEndDate(request.getEndDate());
            budget.setAlertThreshold(request.getAlertThreshold());

            Budget savedBudget = budgetRepository.save(budget);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBudget);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating budget: " + e.getMessage());
        }
    }

    // Get all budgets
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return ResponseEntity.ok(budgetRepository.findAll());
    }

    // Get budget by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable Long id) {
        Optional<Budget> budget = budgetRepository.findById(id);
        if (budget.isPresent()) {
            return ResponseEntity.ok(budget.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found");
    }

    // Get all budgets by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetRepository.findByUserId(userId));
    }

    // Get active budgets for user
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Budget>> getActiveBudgetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetRepository.findActiveBudgetsByUser(userId, LocalDate.now()));
    }

    // Get budget status (spent vs budget amount)
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getBudgetStatus(@PathVariable Long id) {
        Optional<Budget> budgetOptional = budgetRepository.findById(id);

        if (!budgetOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found");
        }

        Budget budget = budgetOptional.get();
        BigDecimal spent = expenseRepository.getTotalExpensesByUserAndDateRange(
                budget.getUser().getId(),
                budget.getStartDate(),
                budget.getEndDate()
        );

        if (spent == null) spent = BigDecimal.ZERO;

        BigDecimal remaining = budget.getAmount().subtract(spent);
        BigDecimal percentageUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
                ? spent.divide(budget.getAmount(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;

        Map<String, Object> status = new HashMap<>();
        status.put("budget", budget);
        status.put("budgetAmount", budget.getAmount());
        status.put("spent", spent);
        status.put("remaining", remaining);
        status.put("percentageUsed", percentageUsed);
        status.put("isOverBudget", spent.compareTo(budget.getAmount()) > 0);
        status.put("alertThresholdReached", percentageUsed.compareTo(budget.getAlertThreshold()) >= 0);

        return ResponseEntity.ok(status);
    }

    // Update budget
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody BudgetRequest request) {
        Optional<Budget> budgetOptional = budgetRepository.findById(id);

        if (!budgetOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found");
        }

        Budget budget = budgetOptional.get();
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setAlertThreshold(request.getAlertThreshold());

        if (request.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());
            if (category.isPresent()) {
                budget.setCategory(category.get());
            }
        }

        Budget updatedBudget = budgetRepository.save(budget);
        return ResponseEntity.ok(updatedBudget);
    }

    // Delete budget
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        if (budgetRepository.existsById(id)) {
            budgetRepository.deleteById(id);
            return ResponseEntity.ok("Budget deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found");
    }
}

// Request DTO class
class BudgetRequest {
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal alertThreshold;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(BigDecimal alertThreshold) { this.alertThreshold = alertThreshold; }
}