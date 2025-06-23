package com.ITK.kalenyuk.tests;

import com.ITK.kalenyuk.pages.HomePage;
import com.ITK.kalenyuk.pages.LoginPage;
import com.ITK.kalenyuk.utils.ScreenshotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class LoginTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private static Properties config;

    static {
        config = new Properties();
        try (InputStream input = LoginTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new IOException("Не найден config.properties");
            config.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки config.properties", e);
        }
    }

//    private void loadConfig() {
//        config = new Properties();
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//            if (input == null) throw new IOException("Не найден config.properties");
//            config.load(input);
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка загрузки config.properties", e);
//        }
//    }

    private String getExcelPath() {
        return config.getProperty("excelPath");
    }

    @BeforeMethod
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
//        loadConfig();

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
        driver.get(config.getProperty("loginUrl"));
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
        List<Object[]> testCases = new ArrayList<>();
        String excelPath = getExcelPath();
        File file = new File(excelPath);

        if (!file.exists()) {
            throw new RuntimeException("Excel file not found: " + excelPath);
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // Skip header

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String username = getCellValue(row.getCell(0));
                String password = getCellValue(row.getCell(1));
                boolean expectedResult = Boolean.parseBoolean(getCellValue(row.getCell(2)));

                testCases.add(new Object[]{username, password, expectedResult});
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel file: " + excelPath, e);
        }
        return testCases.iterator();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
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
                // Универсальная проверка для всех негативных сценариев
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
        loginPage.enterUsername(config.getProperty("validUsername"));
        loginPage.enterPassword(config.getProperty("validPassword"));
        loginPage.clickLoginButton();
        HomePage homePage = new HomePage(driver);

        homePage.performLogout();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Not returned to login page");
    }
}