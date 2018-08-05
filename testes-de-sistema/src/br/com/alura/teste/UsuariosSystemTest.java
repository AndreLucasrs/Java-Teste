package br.com.alura.teste;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
	private UsuariosPage usuarios;

	@Before
	public void inicializa() {

		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		driver = new ChromeDriver();
		//Limpa o Banco
		driver.get("http://localhost:8080/apenas-teste/limpa");
		usuarios = new UsuariosPage(driver);
		usuarios.paginaInicial();
		usuarios.visita();

	}

	@After
	public void finaliza() {

		driver.close();
	}

	@Test
	public void deveAdicionarUmUsuario() {

		usuarios.novo().cadastra("Lucas", "lucas@gmail.com");

		assertTrue(usuarios.existeNaListagem("Lucas", "lucas@gmail.com"));
	}

	@Test
	public void naoDeveAdicionarUmUsuarioSemNome() {

		usuarios.novo().tentaCadastraComUmCampoPreenchido("maryana@gmail.com");

		assertTrue(usuarios.existeErroAoTentaCadastraComApenasUmaInformacao());

	}

	@Test
	public void naoDeveAdicionarUmUsuarioSemNomeOuEmail() {

		usuarios.novo().tentaCadastraSemCampoPreenchido();

		assertTrue(usuarios.mostraMensagemDeCamposObrigatorio());
	}

	@Test
	public void deveDeletarUmUsuario() {

		usuarios.novo().cadastra("ze", "ze@gmail.com");
		assertTrue(usuarios.existeNaListagem("ze", "ze@gmail.com"));

		usuarios.deletaUsuarioNaPosicao(1);

		assertFalse(usuarios.existeNaListagem("ze", "ze@gmail.com"));
	}

	@Test
	public void deveAlterarUmUsuario() {

		usuarios.novo().cadastra("Marvin", "marvin@gmail.com");
		usuarios.altera(1).para("Bender", "bender@gmail.com");

		assertFalse(usuarios.existeNaListagem("Marvin", "marvin@gmail.com"));
		assertTrue(usuarios.existeNaListagem("Bender", "bender@gmail.com"));
	}

}
