package br.com.alura.teste;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class LanceSystemTest {
	
	private ChromeDriver driver;
	private UsuariosPage usuarios;
	private LeiloesPage leiloes;

	@Before
	public void inicializa() {

		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		driver = new ChromeDriver();
		//Limpa o banco
		driver.get("http://localhost:8080/apenas-teste/limpa");
		usuarios = new UsuariosPage(driver);
		usuarios.paginaInicial();
		usuarios.visita();
		usuarios.novo().cadastra("Andre", "andre@gmail.com");
		usuarios.novo().cadastra("Lucas", "lucas@gmail.com");
		leiloes = new LeiloesPage(driver);
		leiloes.visita();
		leiloes.novo().preenche("Notebook", 100, "Andre", false);

	}

	@After
	public void finaliza() {

		driver.close();
	}

	
	@Test
	public void deveFazerUmLance() {
		
		DetalhesDoLeilaoPage lances = leiloes.detalhes(1);
		
		lances.lance("Andre", 150);
		
		assertTrue(lances.existeLance("Andre", 150));
	}

}
