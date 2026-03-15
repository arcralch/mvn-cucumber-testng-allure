package com.example.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;

import org.testng.Assert;

import com.example.config.driver.Driver;
import com.example.web.HomePage;

 
public class LoginPageSteps extends Steps {

    public LoginPageSteps(Driver driver) {
        super(driver);
    }

    public HomePage web;

    @Before
    public void setUp(Scenario scenario) throws IOException{
        driver.getDriver();
    }


    @Given("User is on HRMLogin page")
    public void loginTest() {
        this.web = driver.getHomePage().getUrl(System.getProperty("WEB_URL"));
        driver.takeScreenshot();
    }

    @When("User enters username as {string} and password as {string}")
    public void goToHomePage(String userName, String passWord) {
        this.web.getUserPassword(userName, passWord);
        driver.takeScreenshot();
    }
 
    @Then("User should be able to login successfully and new page open")
    public void verifyLogin() {
        this.web.getVerifyLogin();
        driver.takeScreenshot();
    }
 
    @Then("User should be able to see error message {string}")
    public void verifyErrorMessage(String expectedErrorMessage){
        // Verify Error Message
        Assert.assertEquals(this.web.actualErrorMessage(), expectedErrorMessage);
        driver.takeScreenshot();
    }

    @After
    public void tearDown(Scenario scenario) {
        driver.tearDown(scenario);
    }
 
}
