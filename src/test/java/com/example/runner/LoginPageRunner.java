package com.example.runner;

import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    tags="@ValidCredentials or @InvalidCredentials",
    features = "src/test/resources/features",
    glue = {"com.example.steps", "com.example.config.hooks"},
    plugin = {"pretty",
        "html:target/cucumber-reports/cucumber-pretty",
        "json:target/cucumber-reports/CucumberTestReport.json",
        "rerun:target/cucumber-reports/rerun.txt",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"}
)
    
public class LoginPageRunner extends AbstractTestNGCucumberTests{
    
    @BeforeTest
    public void LoginPageRunnerClass(ITestContext context){
        String browser = context.getCurrentXmlTest().getParameter("browser");
        String headless = context.getCurrentXmlTest().getParameter("headless");
        String urlremote = context.getCurrentXmlTest().getParameter("urlremote");
        String remote = context.getCurrentXmlTest().getParameter("remote");
        String env = context.getCurrentXmlTest().getParameter("env");
        String video = context.getCurrentXmlTest().getParameter("video");

        // Obtiene los valores del archivo testng.xml y los asigna a System properties
        System.setProperty("BROWSER", browser != null ? browser : "chrome");
        System.setProperty("HEADLESS", headless != null ? headless : "false");
        System.setProperty("URLREMOTE", urlremote != null ? urlremote : "");
        System.setProperty("REMOTE", remote != null ? remote : "false");
        System.setProperty("ENV", env != null ? env : "qa");
        System.setProperty("VIDEO", video != null ? video : "false");
    }

}
