package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.pages.LoginPage;
import com.ITK.kalenyuk.pages.RegistrationPage;
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

public class RegistrationTest {
    private WebDriver driver;
    private RegistrationPage registrationPage;
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
        driver.get(config.getProperty("regUrl"));
        registrationPage = new RegistrationPage(driver);
        assertTrue(driver.getCurrentUrl().contains("register"));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void successfulRegistration(){
        registrationPage.registration(config.getProperty("createUsername"), config.getProperty("createLogin")
                , config.getProperty("createMail"), config.getProperty("createPassword")
                , config.getProperty("createPasswordRecord"));
        registrationPage.clickRegistrationLogin();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        Assert.assertEquals(driver.getCurrentUrl(), config.getProperty("regUrl") + "/issues");
    }

    @Test
    public void invalidRegistration(){

        registrationPage.registration(config.getProperty("createUsername"), config.getProperty("createLogin")
                , config.getProperty("createMail"), config.getProperty("createPassword")
                , config.getProperty("createPasswordRecord"));
        registrationPage.clearUsername();
        registrationPage.clearLogin();
        registrationPage.clearEmail();
        registrationPage.clearPassword();

        List<String> errors = registrationPage.getAllErrorText();
        for(String error : errors){
            if(error.contains("Необходимо указать значение")){
                assertTrue(error.contains("Необходимо указать значение"));
            }else {
                assertTrue(error.contains("Не совпадает"));
            }

        }


        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void invalidRegistrationAdmin(){
        driver.get("http://193.233.193.42:9091/hub/auth/register");
        registrationPage = new RegistrationPage(driver);
        registrationPage.registration(config.getProperty("createUsername"), config.getProperty("createLogin")
                , config.getProperty("createMail"), config.getProperty("createPassword")
                , config.getProperty("createPasswordRecord"));
        registrationPage.clickRegistrationLogin();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        List<String> errors = registrationPage.getAllErrorText();
        assertTrue(errors.contains("Некорректный запрос. Повторите попытку или обратитесь к администратору."));
    }

}
