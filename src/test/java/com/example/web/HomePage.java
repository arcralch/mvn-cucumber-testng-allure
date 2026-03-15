package com.example.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.example.config.methods.Methods;

public class HomePage extends Methods {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected boolean isDisplayed() {
        return false;
    }

    public HomePage init(){

        return this;
    }

    public HomePage getUrl(String url){
        driver.navigate().to(url);
        return this;
    }

    public HomePage getUserPassword(String user, String password){
        sendKeysForElements(By.name("username"),user);
        sendKeysForElements(By.name("password"),password);
        return this;
    }

    public HomePage getVerifyLogin(){
        String homePageHeading = stringForElement(By.xpath("//*[@class='oxd-topbar-header-breadcrumb']/h6"));
        
        //Verify new page - HomePage
        switch (System.getProperty("BROWSER").toUpperCase()) {
            case "CHROME": 
            case "FIREFOX":
                Assert.assertEquals(homePageHeading, "Dashboard");
                break;
            default:
                Assert.assertEquals(homePageHeading, "Negativo");
                break;
        }
        
        return this;
    }

    public String actualErrorMessage(){
        return el(By.xpath("//*[@class='orangehrm-login-error']/div[1]/div[1]/p")).getText();
    }
    
}
