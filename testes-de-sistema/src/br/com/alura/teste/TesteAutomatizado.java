package br.com.alura.teste;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TesteAutomatizado {

    public static void main(String[] args) {
    	
    	//tutorial para configurar o geckodriver
    	//http://taketest.take.net/2016/10/18/instalacao-geckodriver-driver-para-abrir-o-firefox-no-selenium/
    	//Teste com firefox
    	//System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");
    	//Teste com chrome
    	System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        // abre firefox
        //WebDriver driver = new FirefoxDriver();
        // abre chrome
        WebDriver driver = new ChromeDriver();
        // acessa o site do google
        driver.get("http://www.google.com.br/");

        // digita no campo com nome "q" do google
        WebElement campoDeTexto = driver.findElement(By.name("q"));
        campoDeTexto.sendKeys("Caelum");

        // submete o form
        campoDeTexto.submit();

    }
}