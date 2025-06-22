package com.ITK.kalenyuk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RegistrationPage {
    private WebDriver driver;

    private By nameField = By.id("name");
    private By loginField = By.id("login");
    private By emailField = By.id("email");
    private By passwordField = By.id("password");
    private By passwordRepeatField = By.id("passwordRepeat");
    private By RegistrationLoginButton = By.xpath("//span[text()='Зарегистрироваться и войти']/..");
    private By accountHasLink = By.xpath("//span[text()='У меня уже есть аккаунт']/..");

    public RegistrationPage (WebDriver driver){
        this.driver = driver;
        waitForPageToLoad();
    }

    public void enterUsername(String username){
        driver.findElement(nameField).sendKeys(username);
    }

    public void clearUsername(){
        driver.findElement(nameField).clear();
    }

    public void enterLogin(String login){
        driver.findElement(loginField).sendKeys(login);
    }

    public void clearLogin(){
        driver.findElement(loginField).clear();
    }

    public void enterEmail(String email){
        driver.findElement(emailField).sendKeys(email);
    }

    public void clearEmail(){
        driver.findElement(emailField).clear();
    }

    public void enterPassword(String password){
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clearPassword(){
        driver.findElement(passwordField).clear();
    }

    public void enterPasswordRepeat(String passwordRepeat){
        driver.findElement(passwordRepeatField).sendKeys(passwordRepeat);
    }

    public void clearPasswordRepeat(){
        driver.findElement(passwordField).clear();
    }

    public void clickRegistrationLogin(){
        driver.findElement(RegistrationLoginButton).click();
    }

    public void clickAccountHasLink(){
        driver.findElement(accountHasLink).click();
    }

    /*
    Поиск ошибок и запись в List
     */
    public List<String> getAllErrorText(){
        List<String> errors = new ArrayList<>();
        List<WebElement> fieldErrors = driver.findElements(By.xpath(
                "//div[contains(@ng-repeat, 'getFormErrorMessages')] | " +
                        "//div[contains(@class, 'header__text__error') and not(contains(@class, 'ng-hide'))]"

        ));

        for (WebElement error : fieldErrors) {
            if (error.isDisplayed()) {
                errors.add(error.getText());
            }
        }

        return errors;

    }

    public void registration(String username, String login, String email, String password, String passwordRepeat){
        enterUsername(username);
        enterLogin(login);
        enterEmail(email);
        enterPassword(password);
        enterPasswordRepeat(passwordRepeat);
    }

    public void waitForPageToLoad(){
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(nameField));
    }

}

