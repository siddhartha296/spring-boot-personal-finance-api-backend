package io.aiven.spring.mysql.demo_siddu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Create new expense
    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody ExpenseRequest request) {
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());

            if (!user.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            if (!category.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
            }

            Expense expense = new Expense();
            expense.setUser(user.get());
            expense.setTitle(request.getTitle());
            expense.setDescription(request.getDescription());
            expense.setAmount(request.getAmount());
            expense.setCategory(category.get());
            expense.setExpenseDate(request.getExpenseDate());
            expense.setPaymentMethod(request.getPaymentMethod());
            expense.setReceiptUrl(request.getReceiptUrl());

            Expense savedExpense = expenseRepository.save(expense);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating expense: " + e.getMessage());
        }
    }

    // Get all expenses
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    // Get expense by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()) {
            return ResponseEntity.ok(expense.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
    }

    // Get all expenses by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseRepository.findByUserIdOrderByExpenseDateDesc(userId));
    }

    // Get expenses by user and category
    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<Expense>> getExpensesByUserAndCategory(
            @PathVariable Long userId, @PathVariable Long categoryId) {
        return ResponseEntity.ok(expenseRepository.findByUserIdAndCategoryId(userId, categoryId));
    }

    // Get expenses by user within date range
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate));
    }

    // Get total expenses by user
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<?> getTotalExpensesByUser(@PathVariable Long userId) {
        BigDecimal total = expenseRepository.getTotalExpensesByUser(userId);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    // Get total expenses by user and category
    @GetMapping("/user/{userId}/category/{categoryId}/total")
    public ResponseEntity<?> getTotalExpensesByUserAndCategory(
            @PathVariable Long userId, @PathVariable Long categoryId) {
        BigDecimal total = expenseRepository.getTotalExpensesByUserAndCategory(userId, categoryId);
        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }

    // Update expense
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        Optional<Expense> expenseOptional = expenseRepository.findById(id);

        if (!expenseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
        }

        Expense expense = expenseOptional.get();
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setReceiptUrl(request.getReceiptUrl());

        if (request.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());
            if (category.isPresent()) {
                expense.setCategory(category.get());
            }
        }

        Expense updatedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(updatedExpense);
    }

    // Delete expense
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok("Expense deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
    }
}

// Request DTO class
class ExpenseRequest {
    private Long userId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Long categoryId;
    private LocalDate expenseDate;
    private String paymentMethod;
    private String receiptUrl;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
}