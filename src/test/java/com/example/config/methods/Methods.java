package com.example.config.methods;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.config.utils.PrintOutText;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;

/**
 * Superclase abstracta de utilitarios para la manipulación de elementos del DOM.
 * Proporciona una capa de abstracción sobre Selenium para interacciones robustas.
 */
public abstract class Methods {
    protected final int MAX_TIME_WAIT;
    protected final int MAX_TIME_WAIT_MODAL;
    protected final int MIN_TIME_WAIT;
    protected final int MAX_TIME_LOAD_PAGE_TIMEOUT;
    protected final int DEFAULT_SECONDS_WAIT_A;

    protected WebDriver driver;
    protected AllureLifecycle allureLifecycle;

    private static PrintOutText printOutText = new PrintOutText();

    public Methods(WebDriver driver) {
        // Carga de tiempos de espera desde propiedades del sistema con valores por defecto
        this.MAX_TIME_WAIT = getIntProperty("MAX_TIME_WAIT", 120);
        this.MAX_TIME_WAIT_MODAL = getIntProperty("MAX_TIME_WAIT_MODAL", 60);
        this.MIN_TIME_WAIT = getIntProperty("MIN_TIME_WAIT", 25);
        this.MAX_TIME_LOAD_PAGE_TIMEOUT = getIntProperty("MAX_TIME_LOAD_PAGE_TIMEOUT", 60);
        this.DEFAULT_SECONDS_WAIT_A = getIntProperty("DEFAULT_SECONDS_WAIT_A", 2);
        this.driver = driver;
        
        // Inicialización de elementos con PageFactory
        PageFactory.initElements(driver, this);
        
        // Verificación inicial de que la página está desplegada
        isDisplayed();
    }

    /**
     * Helper para obtener propiedades numéricas del sistema.
     */
    private int getIntProperty(String key, int defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Método abstracto que debe implementar cada Page Object para validar su visualización.
     */
    protected abstract boolean isDisplayed();

    protected WebDriver driver() {
        return this.driver;
    }

    /**
     * Busca un elemento en el DOM usando un selector By.
     */
    protected WebElement el(By by){
        return driver.findElement(by);
    }

    /**
     * Busca una lista de elementos en el DOM.
     */
    protected List<WebElement> els(By by){
        return driver.findElements(by);
    }

    /**
     * Espera a que un elemento (By) sea interactuable o esté presente.
     */
    protected void waitForElement(By by) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(MAX_TIME_WAIT));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.elementToBeClickable(by),
                ExpectedConditions.presenceOfElementLocated(by), 
                ExpectedConditions.elementToBeSelected(by)
        ));
    }

    /**
     * Espera a que un WebElement sea visible o clickeable.
     */
    protected void waitForElement(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(MAX_TIME_WAIT));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(element),
                ExpectedConditions.elementToBeClickable(element)
        ));
    }

    /**
     * Realiza una espera corta predefinida.
     */
    protected void waitElement(){
        wait(DEFAULT_SECONDS_WAIT_A);
    }

    /**
     * Pausa la ejecución por una cantidad determinada de segundos.
     */
    protected void wait(int seconds){
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Desplaza la vista hasta un elemento (By).
     */
    protected void moveForElement(By by) {
        moverForElement(el(by));
    }

    /**
     * Desplaza la vista hasta un WebElement.
     */
    protected void moverForElement(WebElement element) {
        new Actions(driver()).moveToElement(element).perform();
    }

    /**
     * Limpia el contenido de un campo (By).
     */
    protected void clearForElement(By by) {
        clearForElement(el(by));
    }

    /**
     * Limpia el contenido de un WebElement.
     */
    protected void clearForElement(WebElement element){
        element.clear();
    }

    /**
     * Escribe texto en un elemento (By).
     */
    protected void sendKeysForElements(By by, String str){
        sendKeysForElements(el(by), str);
    }

    /**
     * Escribe texto en un WebElement, asegurando limpieza previa.
     */
    protected void sendKeysForElements(WebElement element, String str){
        waitForElement(element);
        element.clear();
        element.sendKeys(str);
    }

    /**
     * Obtiene el texto de un elemento (By).
     */
    protected String sendKeysForElement(By by) {
        return sendKeysForElement(el(by));
    }

    /**
     * Obtiene el texto de un WebElement.
     */
    protected String sendKeysForElement(WebElement element) {
        return element.getText();
    }
    
    /**
     * Realiza un clic en un elemento (By).
     */
    protected void clickForElement(By by) {
        clickForElement(el(by));
    }

    /**
     * Realiza un clic en un WebElement esperando disponibilidad.
     */
    protected void clickForElement(WebElement element){
        waitForElement(element);
        element.click();
    }

    /**
     * Imprime un mensaje en consola con un color específico.
     */
    protected void printTextOut(String str, String color){
        printOutText.getOutPrintColors(str, color);
    }

    /**
     * Fábrica de electores By dinámicos.
     */
    protected By createBy(String typeSelector, String query) {
        By byObject = null;
        try {
            if (typeSelector == null || query == null) return null;
            
            switch (typeSelector.toLowerCase()) {
                case "cssselector": byObject = By.cssSelector(query); break;
                case "xpath": byObject = By.xpath(query); break;
                case "id": byObject = By.id(query); break;
                case "classname": byObject = By.className(query); break;
                case "name": byObject = By.name(query); break;
                case "linktext": byObject = By.linkText(query); break;
                case "partiallinktext": byObject = By.partialLinkText(query); break;
                case "tagname": byObject = By.tagName(query); break;
                default: throw new IllegalArgumentException("Selector type not supported: " + typeSelector);
            }
        } catch (Exception e) {
            Allure.addAttachment("Error creando selector", "Tipo: " + typeSelector + ", Query: " + query);
            printTextOut("Error creando selector: " + query, "red");
        }
        return byObject;
    }
}
