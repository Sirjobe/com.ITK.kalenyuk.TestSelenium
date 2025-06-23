package com.ITK.kalenyuk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {
    private final WebDriver driver;

    private final By dashboardHeader = By.xpath("//*[@id='menu-react-root']");
    private final By profile = By.xpath("//span[contains(@class, 'ring-ui-manager-avatarWrapper_d243')]");
    private final By logout = By.xpath("//div[contains(@id, 'Выйти')]//button");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        waitForPageToLoad();
    }

    private void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
    }

    public boolean isDashboardDisplayed() {
        return driver.findElement(dashboardHeader).isDisplayed();
    }

    public void profile() {
         driver.findElement(profile).click();
    }

    public void logout() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(logout)).click();
    }

    public void performLogout() {
        profile();
        logout();
    }
}