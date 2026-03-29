package com.example.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.example.config.methods.Methods;

public class HomePage extends Methods {

    By txt_username, txt_password, btn_login, lbl_dashboard, lbl_mensaje_error;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected boolean isDisplayed() {
        return false;
    }

    public HomePage init(){
        txt_username = createBy(System.getProperty("BY_NAME"), System.getProperty("USERNAME_TXT"));
        txt_password = createBy(System.getProperty("BY_NAME"), System.getProperty("PASSWORD_TXT"));
        btn_login = createBy(System.getProperty("BY_XPATH"), System.getProperty("LOGIN_BTN"));
        lbl_dashboard = createBy(System.getProperty("BY_XPATH"), System.getProperty("DASHBOARD_LBL"));
        lbl_mensaje_error = createBy(System.getProperty("BY_XPATH"), System.getProperty("MENSAJE_ERROR_LBL"));
        return this;
    }

    public HomePage getUrl(String url){
        driver.navigate().to(url);
        waitElement();
        return this;
    }

    public HomePage getUserPassword(String user, String password){
        sendKeysForElements(txt_username,user);
        sendKeysForElements(txt_password,password);
        return this;
    }

    public HomePage clickLogin(){
        clickForElement(btn_login);
        waitElement();
        return this;
    }

    public HomePage getVerifyLogin(){
        String homePageHeading = sendKeysForElement(lbl_dashboard);
        
        //Verify new page - HomePage
        String browser = com.example.config.driver.Driver.getBrowserCurrentThread();
        switch (browser.toUpperCase()) {
            case "CHROME": 
            case "FIREFOX":
                waitForElement(lbl_dashboard);
                Assert.assertEquals(homePageHeading, "Dashboard");
                break;
            default:
                Assert.assertEquals(homePageHeading, "Negativo");
                break;
        }
        waitElement();
        return this;
    }

    public String actualErrorMessage(){
        return el(lbl_mensaje_error).getText();
    }
    
}
