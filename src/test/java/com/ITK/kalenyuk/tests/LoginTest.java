package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.pages.HomePage;
import com.ITK.kalenyuk.pages.LoginPage;
import com.ITK.kalenyuk.utils.ConfigLoader;
import com.ITK.kalenyuk.utils.ExcelDataProvider;
import com.ITK.kalenyuk.utils.ScreenshotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

public class LoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        switch (browser.toLowerCase()) {
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
        }

        driver.manage().window().maximize();
        driver.get(ConfigLoader.getProperty("loginUrl"));
        loginPage = new LoginPage(driver);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtils.takeScreenshot(driver, result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    @DataProvider(name = "excelLoginData")
    public Iterator<Object[]> provideLoginData() {
        return ExcelDataProvider.provideLoginData(ConfigLoader.getProperty("excelPath"));
    }

    @Test(dataProvider = "excelLoginData")
    public void parameterizedLoginTest(String username, String password, boolean expectedResult) {
        System.out.println("Тестирование для пользователя: " + username + "/" + password);

        try {
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickLoginButton();

            if (expectedResult) {
                HomePage homePage = new HomePage(driver);
                Assert.assertTrue(homePage.isDashboardDisplayed(),
                        "Панель инструментов не отображается для пользователя: " + username);
            } else {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

                if (username.isEmpty()) {
                    wait.until(d -> loginPage.isUsernameFieldHighlighted());
                }
                if (password.isEmpty()) {
                    wait.until(d -> loginPage.isPasswordFieldHighlighted());
                }
                if (!username.isEmpty() && !password.isEmpty()) {
                    wait.until(d -> !loginPage.getAllErrorText().isEmpty());
                    List<String> errors = loginPage.getAllErrorText();
                    Assert.assertTrue(errors.contains("Некорректное имя пользователя или пароль."),
                            "Сообщение об ошибке не найдено");
                }
            }
        } catch (Exception e) {
            ScreenshotUtils.takeScreenshot(driver, "error_" + username + "_" + password);
            throw e;
        }
    }

    @Test
    public void logoutTest() {
        loginPage.enterUsername(ConfigLoader.getProperty("validUsername"));
        loginPage.enterPassword(ConfigLoader.getProperty("validPassword"));
        loginPage.clickLoginButton();
        HomePage homePage = new HomePage(driver);

        homePage.performLogout();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Not returned to login page");
    }
}