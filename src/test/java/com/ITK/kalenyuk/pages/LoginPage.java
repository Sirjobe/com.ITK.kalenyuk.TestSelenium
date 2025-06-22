package com.ITK.kalenyuk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class LoginPage {
    private WebDriver driver;
    //Локаторы элементов страницы
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.xpath("//span[text()='Войти']/..");
    private By registrationButton = By.xpath("//span[text()='Регистрация']/..");


    public LoginPage (WebDriver driver){
        this.driver = driver;
        waitForPageToLoad();
    }

    public void enterUsername(String username){
        driver.findElement(usernameField).sendKeys(username);
    }

    public void clearUsername(){
        driver.findElement(usernameField).clear();
    }

    public void enterPassword(String password){
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clearPassword(){
        driver.findElement(passwordField).clear();
    }

    public void clickLoginButton(){
        driver.findElement(loginButton).click();
    }

    public void clickRegistration(){
        driver.findElement(registrationButton).click();
    }
    /*
    Поиск ошибок и запись в List
     */
    public List<String> getAllErrorText(){
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

    public void login(String username, String password){
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public void waitForPageToLoad(){
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }




}
