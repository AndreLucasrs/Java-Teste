package br.com.alura.teste;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class LeiloesSystemTest {
	
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
		leiloes = new LeiloesPage(driver);
		leiloes.visita();

	}

	@After
	public void finaliza() {

		driver.close();
	}
	
	@Test
	public void deveCadastrarUmLeilao() {
		
		NovoLeilaoPage novoLeilao = leiloes.novo();
		novoLeilao.preenche("Notebook", 123, "Andre", true); 
		
		assertTrue(leiloes.existe("Notebook", 123, "Andre", true));
	}
	
	@Test
	public void erroCadastraSemNomeESemValorInicial() {
		
		NovoLeilaoPage novoLeilao = leiloes.novo();
		novoLeilao.validacaoDeProdutoApareceu();
		novoLeilao.validacaoDeValorApareceu();
		
	}
}
