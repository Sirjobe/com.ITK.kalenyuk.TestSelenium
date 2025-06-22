package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.pages.LoginPage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private Properties config;

    private void loadConfig() {
        config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Не найден config.properties");
            }
            config.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки config.properties", e);
        }
    }

    @Before
    public void setUp() {
        loadConfig();
        driver = new ChromeDriver();
        driver.get(config.getProperty("loginUrl"));
        loginPage = new LoginPage(driver);
        assertTrue(driver.getCurrentUrl().contains("login"));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void successfulLogin(){
        loginPage.login(config.getProperty("validUsername"), config.getProperty("validPassword"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Assert.assertEquals(driver.getCurrentUrl(), config.getProperty("loginUrl") + "/issues");
    }

    @Test
    public void invalidLoginTest(){
        loginPage.enterUsername(config.getProperty("validUsername"));
        loginPage.enterPassword(config.getProperty("invalidPassword"));
        loginPage.clickLoginButton();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        List<String> errors = loginPage.getAllErrorText();
        assertTrue(errors.contains("Некорректное имя пользователя или пароль."));
    }

    @Test
    public void invalidUsernameTest(){
        loginPage.enterUsername(config.getProperty("invalidUsername"));
        loginPage.enterPassword(config.getProperty("invalidPassword"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        loginPage.clearPassword();
        List<String> errors=loginPage.getAllErrorText();
        assertTrue(errors.contains("Необходимо указать значение"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    }

}
