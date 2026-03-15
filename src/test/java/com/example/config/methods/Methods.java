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
        this.MAX_TIME_WAIT = Integer.parseInt(System.getProperty("MAX_TIME_WAIT"));
        this.MAX_TIME_WAIT_MODAL =  Integer.parseInt(System.getProperty("MAX_TIME_WAIT_MODAL"));
        this.MIN_TIME_WAIT =  Integer.parseInt(System.getProperty("MIN_TIME_WAIT"));
        this.MAX_TIME_LOAD_PAGE_TIMEOUT =  Integer.parseInt(System.getProperty("MAX_TIME_LOAD_PAGE_TIMEOUT"));
        this.DEFAULT_SECONDS_WAIT_A =  Integer.parseInt(System.getProperty("DEFAULT_SECONDS_WAIT_A"));
        this.driver = driver;
        PageFactory.initElements(driver, this);
        isDisplayed();
    }

    protected abstract boolean isDisplayed();

    protected WebDriver driver() {
        return this.driver;
    }

    //Identificador de un WebElement
    protected WebElement el(By by){
        return driver.findElement(by);
    }

    //Identificador de una lista del mismo WebElement
    protected List<WebElement> els(By by){
        return driver.findElements(by);
    }

    //Espera de carga de un WebElement
    protected void waitForElement(By by) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(MAX_TIME_WAIT));
        wait.until(ExpectedConditions.or(ExpectedConditions.elementToBeClickable(by),
                ExpectedConditions.presenceOfElementLocated(by), ExpectedConditions.elementToBeSelected(by)));
    }

    protected void waitForElement(WebElement element) {
        
    }

    //Moverse WebElement
    protected void moveForElement(By by) {
        moverForElement(el(by));
    }

    protected void moverForElement(WebElement element) {
        new Actions(driver()).moveToElement(element).perform();
    }

    //Limpiar el campo de WebElement
    protected void clearForElement(By by) {
        clearForElement(el(by));
    }

    protected void clearForElement(WebElement element){
        element.clear();
        element.submit();
    }

    //Escribir el campos de WebElement
    protected void sendKeysForElements(By by, String  str){
        sendKeysForElements(el(by), str);
    }

    protected void sendKeysForElements(WebElement element, String str){
        clickForElement(element);
        clearForElement(element);
        element.sendKeys(str);
    }

    //Obtener el texto de WebElement
    protected String stringForElement(By by) {
        return stringForElement(el(by));
    }

    protected String stringForElement(WebElement element) {
        return element.getText();
    }
    
    //Click WebElement
    protected void clickForElement(By by) {
        clickForElement(el(by));
    }

    protected void clickForElement(WebElement element){
        waitForElement(element);
        element.click();
    }

    //Metodo para imprimir texto consola
    protected void printTextOut(String str, String color){
        printOutText.getOutPrintColors(str, color);
    }

    //Permite convertir todos los WebElements en el tipo que requiera ser entendido por Selenium
    protected By createBy(String typeSelector, String query){
        By byObject = null;
        try{
            switch (typeSelector) {
                case "cssSelector":
                    byObject = new By.ByCssSelector(query);
                    break;
                case "xpath":
                    byObject = new By.ByXPath(query);
                    break;
                case "id":
                    byObject = new By.ById(query);
                    break;
                case "className":
                    byObject = new By.ByClassName(query);
                    break;
                case "name":
                    byObject = new By.ByName(query);
                    break;
                case "linkText":
                    byObject = new By.ByLinkText(query);
                    break;
                case "partialLinkText":
                    byObject = new By.ByPartialLinkText(query);
                    break;
                case "tagName":
                    byObject = new By.ByTagName(query);
                    break;
                default:
                    throw new Exception("Option is not available");
            }
        }catch(Exception e){
            Allure.addAttachment("Error elemento", query);
            printTextOut("Error elemento: "+query, "red");
        }
        return byObject;
    }
}
