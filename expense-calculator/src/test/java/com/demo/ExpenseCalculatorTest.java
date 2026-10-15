package com.demo;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseCalculatorTest {
    private WebDriver driver;
    private HttpServer appServer;

    @BeforeEach
    void setUp() throws Exception {
        // Start the actual application on an available port. This makes
        // `mvn clean package` work without requiring a separate terminal
        // to run the application first.
        appServer = ExpenseServer.start(0);

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1440,1000"
        );
        driver = new ChromeDriver(options);
        driver.get("http://127.0.0.1:" + appServer.getAddress().getPort() + "/");
    }

    @Test
    void pageLoads() {
        assertTrue(driver.getTitle().contains("ExpenseFlow"), "ExpenseFlow page title should be visible");
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Expense Calculator"));
        assertTrue(driver.findElement(By.id("expense-form")).isDisplayed());
    }

    @Test
    void addingExpenseUpdatesTotal() {
        addExpense("Lunch", "500", "Food");
        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(d -> d.findElement(By.id("total-expenses")).getText().contains("500"));
        assertEquals("₹500.00", driver.findElement(By.id("total-expenses")).getText());
        assertEquals("1", driver.findElement(By.id("expense-count")).getText());
        assertTrue(driver.findElement(By.id("expense-rows")).getText().contains("Lunch"));
    }

    @Test
    void totalAddsMultipleExpenses() {
        addExpense("Lunch", "500", "Food");
        addExpense("Bus ticket", "300", "Travel");
        assertEquals("₹800.00", driver.findElement(By.id("total-expenses")).getText());
        assertEquals("2", driver.findElement(By.id("expense-count")).getText());
    }

    @Test
    void zeroAmountIsRejectedByFormValidation() {
        driver.findElement(By.id("expense-name")).sendKeys("Invalid item");
        driver.findElement(By.id("expense-amount")).sendKeys("0");
        driver.findElement(By.id("expense-category")).sendKeys("Food");
        driver.findElement(By.id("expense-date")).sendKeys("2026-10-15");
        driver.findElement(By.cssSelector("#expense-form button[type='submit']")).click();

        // The number input has min="0.01", so native browser validation
        // prevents submission and the totals remain unchanged.
        assertEquals("₹0.00", driver.findElement(By.id("total-expenses")).getText());
        assertEquals("0", driver.findElement(By.id("expense-count")).getText());
    }

    private void addExpense(String name, String amount, String category) {
        driver.findElement(By.id("expense-name")).sendKeys(name);
        driver.findElement(By.id("expense-amount")).sendKeys(amount);
        driver.findElement(By.id("expense-category")).sendKeys(category);
        driver.findElement(By.id("expense-date")).sendKeys("2026-10-15");
        driver.findElement(By.cssSelector("#expense-form button[type='submit']")).click();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (appServer != null) {
            appServer.stop(0);
        }
    }
}
