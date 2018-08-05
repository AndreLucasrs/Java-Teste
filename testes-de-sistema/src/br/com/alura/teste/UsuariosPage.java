package br.com.alura.teste;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

//Padrão de Projeto - Page Object
public class UsuariosPage {

	private WebDriver driver;

	public UsuariosPage(WebDriver driver) {
		this.driver = driver;

	}

	public void paginaInicial() {
		// Pagina inicial
		driver.get("http://localhost:8080");
	}

	public void visita() {
		// clica em Usuarios
		driver.findElement(By.linkText("Usuários")).click();
	}

	public NovoUsuarioPage novo() {
		// clica em novo usuarios
		driver.findElement(By.linkText("Novo Usuário")).click();
		return new NovoUsuarioPage(driver);
	}

	public boolean existeNaListagem(String nome, String email) {
		// verifica se tem esses valores na pagina
		return driver.getPageSource().contains(nome) && driver.getPageSource().contains(email);
	}

	public boolean existeErroAoTentaCadastraComApenasUmaInformacao() {
		// verifica se aparece a mensagem de erro
		return driver.getPageSource().contains("Nome obrigatorio!");
	}

	public boolean mostraMensagemDeCamposObrigatorio() {
		// verifica se mostra as mensagens de campo obrigatorio
		return driver.getPageSource().contains("Nome obrigatorio!")
				&& driver.getPageSource().contains("E-mail obrigatorio!");
	}

	public void deletaUsuarioNaPosicao(int posicao) {
		driver.findElements(By.tagName("button")).get(posicao - 1).click();
		// pega o alert que está aberto
		Alert alert = driver.switchTo().alert();
		// confirma
		alert.accept();
	}

	public AlteraUsuarioPage altera(int posicao) {
		driver.findElements(By.linkText("editar")).get(posicao - 1).click();
		return new AlteraUsuarioPage(driver);
	}
}
