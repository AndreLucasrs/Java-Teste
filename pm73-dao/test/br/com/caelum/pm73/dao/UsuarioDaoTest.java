package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Usuario;

//Rode primeiro a classe criar tabelas depois 
//OBS: como esse caso esta sendo feito com banco de dados em memoria estamos enserindo no momento do teste um novo usuario
//o correto seria apenas trazer alguem ja esperado
public class UsuarioDaoTest {

	private Session session;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {

		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);

		// Com isso conseguimos resolver o problema citado no OBS
		// podemos criar o dados
		session.beginTransaction();
	}

	@After
	public void depois() {

		// e no final do teste ele vai da rollback no dado criado apenas para teste
		// com isso os dados que estão no banco não ficaram "sujos" com dados de teste
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEEmailMockado() {

		Usuario novoUsuario = new Usuario("Andre", "andre@teste.com.br");
		usuarioDao.salvar(novoUsuario);

		// Não devemos mockar devido querer testar a consulta e o resultado que ela traz
		Usuario usuario = usuarioDao.porNomeEEmail("Andre", "andre@teste.com.br");

		// garantindo que o objeto é o esperado
		assertEquals("Andre", usuario.getNome());
		assertEquals("andre@teste.com.br", usuario.getEmail());
	}

	@Test
	public void deveRetornarNuloSeNaoEncontrarUsuario() {

		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("Lucas", "lucas@teste.com.br");

		// garantido que volta null
		assertNull(usuarioDoBanco);
	}
}
