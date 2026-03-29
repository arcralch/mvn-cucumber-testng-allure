package com.example.config.driver;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
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

/**
 * Clase encargada de la gestión del WebDriver y la configuración del entorno de pruebas.
 * Implementa ThreadLocal para soportar ejecuciones paralelas seguras.
 */
public class Driver {
    
    // Almacenamiento seguro por hilo para el WebDriver y metadatos de la sesión
    private static ThreadLocal<WebDriver> driverTL = new ThreadLocal<>();
    private static ThreadLocal<String> browserTL = new ThreadLocal<>();
    private static ThreadLocal<String> envTL = new ThreadLocal<>();
    private static ThreadLocal<Scenario> scenarioTL = new ThreadLocal<>();
    
    private static AllureLifecycle allureLifecycle;
    private Methods page;
    
    // Control de carga única de propiedades globales
    private static boolean propertiesLoaded = false;
    private static final Object lock = new Object();

    // Contenedores de propiedades de configuración
    private static Properties user = new Properties();
    private static Properties selector = new Properties();
    private static Properties data = new Properties();
    private static Properties update = new Properties();
    private static ScreenshotUtils screenshotUtils = new ScreenshotUtils();

    /**
     * Obtiene el WebDriver asociado al hilo actual. Si no existe, lo inicializa.
     * @return WebDriver activo
     * @throws IOException Si ocurre un error cargando las propiedades
     */
    public WebDriver getDriver() throws IOException{
        ensurePropertiesLoaded();
        allureLifecycle = io.qameta.allure.Allure.getLifecycle();
        
        if (driverTL.get() == null) {
            initDriver();
        }
        return driverTL.get();
    }

    /**
     * Inicializa el WebDriver basado en las propiedades del sistema (BROWSER, REMOTE, etc.).
     * Configura tiempos de espera y maximiza la ventana.
     */
    private void initDriver() throws MalformedURLException {
        String browser = System.getProperty("BROWSER");
        String browserRemote = System.getProperty("URLREMOTE");
        boolean headless = Boolean.parseBoolean(System.getProperty("HEADLESS"));
        boolean remote = Boolean.parseBoolean(System.getProperty("REMOTE"));

        if (browser == null || browser.isEmpty()) {
            browser = "CHROME";
        }
        
        browserTL.set(browser);

        WebDriver driver = createWebDriver(browser.toUpperCase(), browserRemote, headless, remote);
        
        driverTL.set(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().deleteAllCookies();
        
        if (!headless) {
            driver.manage().window().maximize();
        }
    }

    /**
     * Factoría para crear la instancia específica de WebDriver elegida.
     */
    private WebDriver createWebDriver(String browser, String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        switch (browser) {
            case "CHROME": return setupChromeDriver(browserRemote, headless, remote);
            case "FIREFOX": return setupFirefoxDriver(browserRemote, headless, remote);
            case "EDGE": return setupEdgeDriver(browserRemote, headless, remote);
            case "SAFARI": return setupSafariDriver(browserRemote, remote);
            default: throw new RuntimeException("No existe WebDriver para el navegador: " + browser);
        }
    }

    /**
     * Configuración específica para Google Chrome.
     */
    private WebDriver setupChromeDriver(String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        chromeOptions.addArguments("--remote-allow-origins=*", "ignore-certificate-errors");
        if (headless) {
            chromeOptions.addArguments("--no-sandbox", "--headless=new", "--window-size=1280,720", "--disable-dev-shm-usage", "--disable-gpu");
        } else {
            chromeOptions.addArguments("--start-maximized", "--incognito", "--disable-dev-shm-usage");
        }
        return remote ? new RemoteWebDriver(URI.create(browserRemote).toURL(), chromeOptions) : new ChromeDriver(chromeOptions);
    }

    /**
     * Configuración específica para Mozilla Firefox.
     */
    private WebDriver setupFirefoxDriver(String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        if (headless) {
            firefoxOptions.addArguments("--no-sandbox", "--headless", "--window-size=1280,720", "--disable-dev-shm-usage", "--disable-gpu");
        } else {
            firefoxOptions.addArguments("--start-maximized", "--incognito", "--disable-dev-shm-usage");
        }
        return remote ? new RemoteWebDriver(URI.create(browserRemote).toURL(), firefoxOptions) : new FirefoxDriver(firefoxOptions);
    }

    /**
     * Configuración específica para Microsoft Edge.
     */
    private WebDriver setupEdgeDriver(String browserRemote, boolean headless, boolean remote) throws MalformedURLException {
        WebDriverManager.edgedriver().setup();
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        if (headless) {
            edgeOptions.addArguments("--no-sandbox", "--headless=new", "--window-size=1280,720", "--disable-dev-shm-usage", "--disable-gpu");
        } else {
            edgeOptions.addArguments("--start-maximized", "--incognito", "--disable-dev-shm-usage");
        }
        return remote ? new RemoteWebDriver(URI.create(browserRemote).toURL(), edgeOptions) : new EdgeDriver(edgeOptions);
    }

    /**
     * Configuración específica para Apple Safari.
     */
    private WebDriver setupSafariDriver(String browserRemote, boolean remote) throws MalformedURLException {
        WebDriverManager.safaridriver().setup();
        SafariOptions safariOptions = new SafariOptions();
        safariOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        return remote ? new RemoteWebDriver(URI.create(browserRemote).toURL(), safariOptions) : new SafariDriver(safariOptions);
    }

    /**
     * Realiza capturas de pantalla si el escenario falló.
     * @param scenario Escenario de Cucumber actual
     */
    public void tearDown(Scenario scenario){
        if(scenario.isFailed()){
            takeScreenshot();
        }
    }

    /**
     * Cierra el navegador y limpia las variables de ThreadLocal para liberar memoria.
     */
    public void quitDriver(){
        WebDriver driver = driverTL.get();
        if(driver != null){
            driver.quit();
            driverTL.remove();
            browserTL.remove();
            envTL.remove();
            scenarioTL.remove();
        }
    }

    /**
     * Garantiza que las propiedades del entorno se carguen solo una vez de forma segura.
     */
    private static void ensurePropertiesLoaded() throws IOException {
        if (!propertiesLoaded) {
            synchronized (lock) {
                if (!propertiesLoaded) {
                    loadAllProperties();
                    propertiesLoaded = true;
                }
            }
        }
    }

    /**
     * Lee los archivos .properties del entorno seleccionado (qa por defecto) y los inyecta en el sistema.
     */
    private static void loadAllProperties() throws IOException {
        String env = System.getProperty("ENV");
        if (env == null || env.isEmpty()) {
            env = "qa";
        }
        
        user.load(new FileReader("src/test/resources/enviroment/"+env.toLowerCase()+"/user.properties"));
        selector.load(new FileReader("src/test/resources/enviroment/"+env.toLowerCase()+"/selector.properties"));
        data.load(new FileReader("src/test/resources/enviroment/"+env.toLowerCase()+"/data.properties"));
        update.load(new FileReader("src/test/resources/enviroment/"+env.toLowerCase()+"/update.properties"));
        
        PropertiesFile propertiesFile = new PropertiesFile();
        propertiesFile.readProperties(user);
        propertiesFile.readProperties(selector);
        propertiesFile.readProperties(data);
        propertiesFile.readProperties(update);
    }

    /**
     * Captura una instantánea del estado actual del navegador para Allure.
     */
    public void takeScreenshot(){
        screenshotUtils.captureScreenshot(allureLifecycle, driverTL.get());
    }

    /**
     * Asigna la página actual en ejecución.
     */
    public void setPage(Methods page) {
        this.page = page;
    }

    /**
     * Obtiene una instancia tipada de la página actual.
     */
    public <T extends Methods> T getPage(Class<T> class1) {
        return class1.cast(page);
    }

    /**
     * Retorna una nueva instancia de la página de inicio.
     */
    public HomePage getHomePage() {
        return new HomePage(driverTL.get());
    }

    /**
     * Obtiene el nombre del navegador que está usando el hilo actual.
     */
    public static String getBrowserCurrentThread() {
        return browserTL.get() != null ? browserTL.get() : System.getProperty("BROWSER");
    }

    /**
     * Almacena el objeto Scenario de Cucumber en el hilo actual.
     */
    public void setScenario(Scenario scenario) {
        scenarioTL.set(scenario);
    }

    /**
     * Recupera el objeto Scenario de Cucumber para el hilo actual.
     */
    public Scenario getScenario() {
        return scenarioTL.get();
    }

}
