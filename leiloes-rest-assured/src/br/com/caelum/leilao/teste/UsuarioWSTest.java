package br.com.caelum.leilao.teste;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;

import br.com.caelum.leilao.modelo.Leilao;
import br.com.caelum.leilao.modelo.Usuario;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;


////Para o teste, rode a aplicação antes
//pelo terminal entre na pasta leiloes-ws e rode o seguinte comando
//ant jetty.run
//e depois pode rodar essas classes de teste
public class UsuarioWSTest {

	private Usuario esperado1;
	private Usuario esperado2;

	@Before
	public void setUp() {

		esperado1 = new Usuario(1L, "Mauricio Aniche", "mauricio.aniche@caelum.com.br");
		esperado2 = new Usuario(2L, "Guilherme Silveira", "guilherme.silveira@caelum.com.br");

	}

	@Test
	public void deveRetornarListaDeUsuarios() {

		XmlPath path = given().header("Accept", "application/xml").get("/usuarios").andReturn().xmlPath();

		// Usuario usuario1 = path.getObject("list.usuario[0]", Usuario.class);
		// Usuario usuario2 = path.getObject("list.usuario[1]", Usuario.class);
		List<Usuario> usuarios = path.getList("list.usuario", Usuario.class);

		assertEquals(esperado1, usuarios.get(0));
		assertEquals(esperado2, usuarios.get(1));

	}

	@Test
	public void deveRetornarUsuarioPorId() {

		JsonPath path = given().header("Accept", "application/json").parameter("usuario.id", 1).get("/usuarios/show")
				.andReturn().jsonPath();

		Usuario usuario = path.getObject("usuario", Usuario.class);

		assertEquals(esperado1, usuario);

	}

	@Test
	public void deveRetornarUmLeilao() {
		JsonPath path = given().parameter("leilao.id", 1).header("Accept", "application/json").get("/leiloes/show")
				.andReturn().jsonPath();

		Leilao leilao = path.getObject("leilao", Leilao.class);

		Usuario mauricio = new Usuario(1l, "Mauricio Aniche", "mauricio.aniche@caelum.com.br");
		Leilao esperado = new Leilao(1l, "Geladeira", 800.0, mauricio, false);

		assertEquals(esperado, leilao);
	}

	@Test
	public void deveRetornarQuantidadeDeLeiloes() {
		XmlPath path = given().header("Accept", "application/xml").get("/leiloes/total").andReturn().xmlPath();

		int total = path.getInt("int");

		assertEquals(2, total);
	}

	@Test
	public void deveAdicionarUmUsuario() {

		Usuario andre = new Usuario("Andre Lucas", "andre@gmail.com");

		XmlPath path = given().header("Accept", "application/xml").contentType("application/xml").body(andre).when()
				.post("/usuarios").andReturn().xmlPath();

		// XmlPath path = given().header("Accept",
		// "application/xml").contentType("application/xml").body(andre).expect()
		// .statusCode(200).when().post("/usuarios").andReturn().xmlPath();

		Usuario resposta = path.getObject("usuario", Usuario.class);

		assertEquals(andre.getNome(), resposta.getNome());
		assertEquals(andre.getEmail(), resposta.getEmail());

		// deletando aqui
		given().contentType("application/xml").body(resposta).expect().statusCode(200).when().delete("/usuarios/deleta")
				.andReturn().asString();
	}

	@Test
	public void deveInserirLeiloes() {
		Usuario mauricio = new Usuario(1l, "Mauricio Aniche", "mauricio.aniche@caelum.com.br");
		Leilao leiloes = new Leilao(1l, "Geladeira", 800.0, mauricio, false);

		XmlPath retorno = given().header("Accept", "application/xml").contentType("application/xml").body(leiloes)
				.expect().statusCode(200).when().post("/leiloes").andReturn().xmlPath();

		Leilao resposta = retorno.getObject("leilao", Leilao.class);

		assertEquals("Geladeira", resposta.getNome());

		// deletando aqui
		given().contentType("application/xml").body(resposta).expect().statusCode(200).when().delete("/leiloes/deletar")
				.andReturn().asString();
	}
}
