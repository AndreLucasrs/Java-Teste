package br.com.alura.teste;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

//Para o teste, rode a aplicação antes
//pelo terminal entre na pasta leiloes e rode o seguinte comando
//ant jetty.run
//tutorial para adicionar o ant
//https://loiane.com/2015/03/instalando-o-apache-ant-e-configurando-a-variavel-de-ambiente-ant_home-no-windows/
//abra a aplicação no http://localhost:8080/
//e agora pode roda os teste
public class UsuariosSystemTest {
	
	private ChromeDriver driver;
	
	@Before
	public void inicializa(){
		
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.get("http://localhost:8080");
		driver.findElement(By.linkText("Usuários")).click();
		driver.findElement(By.linkText("Novo Usuário")).click();
	}
	
	@After
	public void finaliza() {
		
		driver.close();
	}

	@Test
	public void deveAdicionarUmUsuario() {
		
		//Buscar o name que tem no input
		WebElement nome = driver.findElement(By.name("usuario.nome"));
		WebElement email = driver.findElement(By.name("usuario.email"));
		//seta o nome no input
        nome.sendKeys("Lucas");
        email.sendKeys("lucas@gmail.com");
        
        //1 Opcao
        //busca o id que tem no input
        WebElement botaoSalvar = driver.findElement(By.id("btnSalvar"));
        //clica no botao salvar
        botaoSalvar.click();
        
        //2 Opcao
        //ele ira da o submit atraves do proprio input
        //nome.submit();
        //email.submit();
        
        //esse getPageSource devolve o codigo fonta que esta sendo exibida pelo navegador
        //pega o codigo fonte e verifica se contem o Lucas
        boolean achouNome = driver.getPageSource().contains("Lucas");
        boolean achouEmail = driver.getPageSource().contains("lucas@gmail.com");
        
        assertTrue(achouNome);
        assertTrue(achouEmail);
	}
	
	@Test
	public void naoDeveAdicionarUmUsuarioSemNome() {
		
		driver.get("http://localhost:8080/usuarios/new");
		WebElement email = driver.findElement(By.name("usuario.email"));
		email.sendKeys("maryana@gmail.com");
		
		email.submit();
		
		assertTrue(driver.getPageSource().contains("Nome obrigatorio!"));
		
	}
	
	@Test
	public void naoDeveAdicionarUmUsuarioSemNomeOuEmail() {
		
		driver.get("http://localhost:8080/usuarios/new");
		
		WebElement botaoSalvar = driver.findElement(By.id("btnSalvar"));
		botaoSalvar.click();
		
		assertTrue(driver.getPageSource().contains("Nome obrigatorio!"));
		assertTrue(driver.getPageSource().contains("E-mail obrigatorio!"));
	}
}
