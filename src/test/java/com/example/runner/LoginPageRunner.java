package com.example.runner;

import java.util.Properties;

import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import com.example.config.utils.PropertiesFile;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(
    tags="@ValidCredentials or @InvalidCredentials",
    features = "src/test/resources/features",
    glue = {"com.example.steps"},
    plugin = {"pretty",
        "html:target/cucumber-reports/cucumber-pretty",
        "json:target/cucumber-reports/CucumberTestReport.json",
        "rerun:target/cucumber-reports/rerun.txt",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"}
)
    
public class LoginPageRunner extends AbstractTestNGCucumberTests{
    
    @BeforeTest
    public void LoginPageRunnerClass(ITestContext context){
        Properties properties = new Properties();
        String browser = context.getCurrentXmlTest().getParameter("browser");
        String headless = context.getCurrentXmlTest().getParameter("headless");
        String urlremote = context.getCurrentXmlTest().getParameter("urlremote");
        String remote = context.getCurrentXmlTest().getParameter("remote");
        String env = context.getCurrentXmlTest().getParameter("env");


        //Obtiene los valor del archivo testng.xml
        System.setProperty("BROWSER", browser);
        System.setProperty("HEADLESS", headless);
        System.setProperty("URLREMOTE", urlremote);
        System.setProperty("REMOTE", remote);
        System.setProperty("ENV", env);

        //Crear el archivo environment Allure
        properties.setProperty("Headless", headless);
        properties.setProperty("Ambiente", env);
        PropertiesFile.writeEnvironmentAllure(properties);

    }

}
