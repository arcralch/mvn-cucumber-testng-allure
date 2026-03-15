package com.example.config.driver;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.example.config.methods.Methods;
import com.example.config.utils.PropertiesFile;
import com.example.config.utils.ScreenshotUtils;
import com.example.web.HomePage;


import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.AllureLifecycle;

public class Driver {
    
    private static WebDriver driver;
    private static AllureLifecycle allureLifecycle;
    private Methods page;
    
    private static Properties user = new Properties();
    private static Properties data = new Properties();
    private static Properties selector = new Properties();
    private static Properties update = new Properties();
    private static ScreenshotUtils screenshotUtils= new ScreenshotUtils();

    public WebDriver getDriver() throws IOException{
        readProperties();
        allureLifecycle = io.qameta.allure.Allure.getLifecycle();
        if(driver==null){
            new Driver();
        }
        return driver;
    }

    public Driver() throws MalformedURLException {
        String browser = System.getProperty("BROWSER");
        String browserRemote = System.getProperty("URLREMOTE");
        boolean headless = Boolean.parseBoolean(System.getProperty("HEADLESS"));
        boolean remote = Boolean.parseBoolean(System.getProperty("REMOTE"));

        if (browser == null || browser.isEmpty()) {
            throw new RuntimeException("No se especificó el navegador. Usa la propiedad 'BROWSER'.");
        }

        driver = createWebDriver(browser.toUpperCase(), browserRemote, headless, remote);

        // Configuraciones comunes para todos los navegadores
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().deleteAllCookies();
    }

    private WebDriver createWebDriver(String browser, String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        switch (browser) {
            case "CHROME":
                return setupChromeDriver(browserRemote, headless, remote);
            case "FIREFOX":
                return setupFirefoxDriver(browserRemote, headless, remote);
            case "EDGE":
                return setupEdgeDriver(browserRemote, headless, remote);
            case "SAFARI":
                return setupSafariDriver(browserRemote, remote);
            default:
                throw new RuntimeException("No existe WebDriver para el navegador: " + browser);
        }
    }

    @SuppressWarnings("deprecation")
    private WebDriver setupChromeDriver(String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        chromeOptions.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");

        if (headless) {
            chromeOptions.addArguments("--no-sandbox", "--headless", "window-size=1200,600", "--disable-dev-shm-usage");
        }else{
            chromeOptions.addArguments("--start-maximized", "--incognito", "--disable-dev-shm-usage");
        }

        return remote 
                ? new RemoteWebDriver(new URL(browserRemote), chromeOptions)
                : createLocalDriver(new ChromeDriver(chromeOptions));
    }

    @SuppressWarnings("deprecation")
    private WebDriver setupFirefoxDriver(String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        if (headless) {
            firefoxOptions.addArguments("--no-sandbox", "--headless", "window-size=1200,600", "--disable-dev-shm-usage");
        }else{
            firefoxOptions.addArguments("--start-maximized", "--incognito", "--disable-dev-shm-usage");
        }

        return remote 
                ? new RemoteWebDriver(new URL(browserRemote), firefoxOptions)
                : createLocalDriver(new FirefoxDriver(firefoxOptions));
    }

    @SuppressWarnings("deprecation")
    private WebDriver setupEdgeDriver(String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        WebDriverManager.edgedriver().setup();
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        if (headless) {
            edgeOptions.addArguments("--no-sandbox", "--headless", "window-size=1200,600", "--disable-dev-shm-usage");
        }else{
            edgeOptions.addArguments("--start-maximized", "--incognito", "--disable-dev-shm-usage");
        }

        return remote 
                ? new RemoteWebDriver(new URL(browserRemote), edgeOptions)
                : createLocalDriver(new EdgeDriver(edgeOptions));
    }

    @SuppressWarnings("deprecation")
    private WebDriver setupSafariDriver(String browserRemote, boolean remote) throws MalformedURLException {
        WebDriverManager.safaridriver().setup();
        SafariOptions safariOptions = new SafariOptions();
        safariOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        return remote 
                ? new RemoteWebDriver(new URL(browserRemote), safariOptions)
                : createLocalDriver(new SafariDriver(safariOptions));
    }

    private WebDriver createLocalDriver(WebDriver localDriver) {
        localDriver.manage().window().maximize();
        return localDriver;
    }


    public void tearDown(Scenario scenario){
        if(scenario.isFailed()){
            takeScreenshot();
        }
        quitDriver();
    }


    public void quitDriver(){
        if(driver!=null){
            driver.quit();
            driver = null;
        }
    }

     //properties file reader
    private static void readProperties() throws IOException{
        user.load(new FileReader("src/test/resources/enviroment/"+System.getProperty("ENV").toLowerCase()+"/user.properties"));
        selector.load(new FileReader("src/test/resources/enviroment/"+System.getProperty("ENV").toLowerCase()+"/selector.properties"));
        data.load(new FileReader("src/test/resources/enviroment/"+System.getProperty("ENV").toLowerCase()+"/data.properties"));
        update.load(new FileReader("src/test/resources/enviroment/"+System.getProperty("ENV").toLowerCase()+"/update.properties"));
        PropertiesFile propertiesFile = new PropertiesFile();
        propertiesFile.readProperties(user);
        propertiesFile.readProperties(selector);
        propertiesFile.readProperties(data);
        propertiesFile.readProperties(update);
    }

    public void takeScreenshot(){
        screenshotUtils.captureScreenshot(allureLifecycle, driver);
    }

    public void tearDown() {
        driver.close();
    }

    public void setPage(Methods page) {
        this.page = page;
    }

    public <T extends Methods> T getPage(Class<T> class1) {
        return class1.cast(page);
    }

    public HomePage getHomePage() {
        return new HomePage(driver);
    }

}
