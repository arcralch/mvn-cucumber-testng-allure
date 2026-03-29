package com.example.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import com.example.config.driver.Driver;
import com.example.web.HomePage;

public class Steps {
    
    private final Driver driver;
    private HomePage web;

    public Steps(Driver driver) {
        this.driver = driver;
    }

    @Given("User is on HRMLogin page")
    public void loginTest() throws java.io.IOException {
        this.web = driver.getHomePage().init().getUrl(System.getProperty("WEB_URL"));
        driver.takeScreenshot();
    }

    @When("User enters username as {string} and password as {string}")
    public void goToHomePage(String userName, String passWord) {
        this.web.getUserPassword(userName, passWord);
        driver.takeScreenshot();
        this.web.clickLogin();
    }

    @Then("User should be able to login successfully and new page open")
    public void verifyLogin() {
        this.web.getVerifyLogin();
        driver.takeScreenshot();
    }

    @Then("User should be able to see error message {string}")
    public void verifyErrorMessage(String expectedErrorMessage){
        Assert.assertEquals(this.web.actualErrorMessage(), expectedErrorMessage);
        driver.takeScreenshot();
    }
}
