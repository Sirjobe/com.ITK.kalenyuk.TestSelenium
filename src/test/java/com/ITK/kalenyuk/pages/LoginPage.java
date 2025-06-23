package com.ITK.kalenyuk.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class LoginPage {
    private final WebDriver driver;

    // Локаторы
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.xpath("//span[text()='Войти']/..");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        waitForPageToLoad();
    }

    public void enterUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    public List<String> getAllErrorText() {
        List<String> errors = new ArrayList<>();
        List<WebElement> globalErrors = driver.findElements(By.xpath("//div[contains(@class, 'header__text')]" +
                "//div[contains(@class, 'header__text__error')]"));

        for (WebElement error : globalErrors){
            if (error.isDisplayed()){
                errors.add(error.getText());
            }
        }
        List<WebElement> fieldErrors = driver.findElements(By.xpath(
                "//div[contains(@class, 'login-page__error-message')]//span"
        ));

        for (WebElement error : fieldErrors) {
            if (error.isDisplayed()) {
                errors.add(error.getText());
            }
        }

        return errors;
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    public boolean isLoginPageDisplayed() {
        return driver.findElement(loginButton).isDisplayed();
    }

    public boolean isUsernameFieldHighlighted() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        return isElementHighlighted(field);
    }

    public boolean isPasswordFieldHighlighted() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField)); // Исправлено!
        return isElementHighlighted(field);
    }

    private boolean isElementHighlighted(WebElement element) {
        // 1. Проверяем через JavaScript реальное значение CSS-переменной
        String script = "return window.getComputedStyle(arguments[0]).getPropertyValue('border-color');";
        String computedColor = (String) ((JavascriptExecutor) driver).executeScript(script, element);

        // 2. Проверяем стандартные CSS-свойства
        String borderColor = element.getCssValue("border-color");
        String borderWidth = element.getCssValue("border-width");

        // 3. Проверяем классы элемента
        String classAttribute = element.getAttribute("class");

        System.out.println("Border color: " + borderColor);
        System.out.println("Computed color: " + computedColor);
        System.out.println("Classes: " + classAttribute);

        // 4. Основные проверки
        return computedColor.contains("219, 59, 75") ||        // RGB
                computedColor.contains("var(--ring-icon-error-color)") ||
                borderColor.contains("219, 59, 75") ||
                classAttribute.contains("error") ||
                classAttribute.contains("invalid") ||
                (!"0px".equals(borderWidth) && borderColor.contains("var"));
    }

}