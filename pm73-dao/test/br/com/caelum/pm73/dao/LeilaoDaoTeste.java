package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

//OBS: como esse caso esta sendo feito com banco de dados em memoria estamos enserindo no momento do teste um novo usuario
//o correto seria apenas trazer alguem ja esperado
public class LeilaoDaoTeste {

	private Session session;
	private UsuarioDao usuarioDao;
	private LeilaoDao leilaoDao;

	@Before
	public void antes() {

		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		leilaoDao = new LeilaoDao(session);

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
	public void deveContarLeiloesNaoEncerrados() {

		Usuario maryana = new Usuario("maryana", "maryana@teste.com.br");

		Leilao ativo = new Leilao("Notebook", 1500.0, maryana, false);
		Leilao encerrado = new Leilao("videogamer", 700.0, maryana, false);
		encerrado.encerra();

		usuarioDao.salvar(maryana);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		long total = leilaoDao.total();

		assertEquals(1l, total);
	}

	@Test
	public void deveRetornarZeroSeNaoHaLeiloesNovos() {
		Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");

		Leilao encerrado = new Leilao("notebook", 700.0, marinho, false);
		Leilao tambemEncerrado = new Leilao("videogamer", 1500.0, marinho, false);
		encerrado.encerra();
		tambemEncerrado.encerra();

		usuarioDao.salvar(marinho);
		leilaoDao.salvar(encerrado);
		leilaoDao.salvar(tambemEncerrado);

		long total = leilaoDao.total();

		assertEquals(0L, total);
	}

	@Test
	public void deveRetornarLeiloesDeProdutosNovos() {
		Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");

		Leilao produtoNovo = new Leilao("notebook", 700.0, marinho, false);
		Leilao produtoUsado = new Leilao("videogamer", 1500.0, marinho, true);

		usuarioDao.salvar(marinho);
		leilaoDao.salvar(produtoNovo);
		leilaoDao.salvar(produtoUsado);

		List<Leilao> novos = leilaoDao.novos();

		assertEquals(1, novos.size());
		assertEquals("notebook", novos.get(0).getNome());
	}

	@Test
	public void deveTrazerSomenteLeiloesAntigos() {
		Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");

		Leilao recente = new Leilao("notebook", 700.0, marinho, false);
		Leilao antigo = new Leilao("videogame", 1500.0, marinho, true);

		Calendar dataRecente = Calendar.getInstance();
		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -10);

		recente.setDataAbertura(dataRecente);
		antigo.setDataAbertura(dataAntiga);

		usuarioDao.salvar(marinho);
		leilaoDao.salvar(recente);
		leilaoDao.salvar(antigo);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(1, antigos.size());
		assertEquals("videogame", antigos.get(0).getNome());
	}

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {

		// criando as datas
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();

		Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");

		// criando os leiloes, cada um com uma data
		Leilao leilao1 = new Leilao("notebook", 700.0, marinho, false);
		Calendar dataLeilao1 = Calendar.getInstance();
		dataLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		leilao1.setDataAbertura(dataLeilao1);

		Leilao leilao2 = new Leilao("videgame", 1700.0, marinho, false);
		Calendar dataLeilao2 = Calendar.getInstance();
		dataLeilao2.add(Calendar.DAY_OF_MONTH, -20);
		leilao2.setDataAbertura(dataLeilao2);

		// persistindo os objetos no banco
		usuarioDao.salvar(marinho);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);

		// invocando o metodo para testar
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		// garantindo que a query funcionou
		assertEquals(1, leiloes.size());
		assertEquals("notebook", leiloes.get(0).getNome());
	}

	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {

		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();

		Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");

		Leilao leilao1 = new Leilao("notebook", 700.0, marinho, false);
		Calendar dataLeilao1 = Calendar.getInstance();
		dataLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		leilao1.setDataAbertura(dataLeilao1);
		leilao1.encerra();

		usuarioDao.salvar(marinho);
		leilaoDao.salvar(leilao1);

		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		assertEquals(0, leiloes.size());
	}

	@Test
	public void deveDeleterUmUsuario() {

		Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");

		usuarioDao.salvar(marinho);

		usuarioDao.deletar(marinho);

		// como o hibernate coloca muita coisa em cache para garatir que o insert e o
		// delete funciono
		// usamos o flush que manda a ordem para o banco
		// e o clear apaga o cache
		session.flush();
		session.clear();

		Usuario deletado = usuarioDao.porNomeEEmail("Marinho", "marinho@teste.com.br");

		assertNull(deletado);
	}

	@Test
	public void deveAlterarUmUsuario() {
		Usuario usuario = new Usuario("Marinho", "marinho@teste.com.br");

		usuarioDao.salvar(usuario);

		usuario.setNome("Andre");
		usuario.setEmail("andre@teste.com.br");

		usuarioDao.atualizar(usuario);

		session.flush();

		Usuario novoUsuario = usuarioDao.porNomeEEmail("Andre", "andre@teste.com.br");
		assertNotNull(novoUsuario);
		System.out.println(novoUsuario);

		Usuario usuarioInexistente = usuarioDao.porNomeEEmail("Marinho", "marinho@teste.com.br");
		assertNull(usuarioInexistente);

	}

	// @Test
	// public void deveTrazerSomenteLeiloesAntigosHaMaisDe7Dias() {
	// Usuario marinho = new Usuario("Marinho", "marinho@teste.com.br");
	//
	// Leilao noLimite = new Leilao("notebook", 700.0, marinho, false);
	//
	// Calendar dataAntiga = Calendar.getInstance();
	// dataAntiga.add(Calendar.DAY_OF_MONTH, -7);
	//
	// noLimite.setDataAbertura(dataAntiga);
	//
	// usuarioDao.salvar(marinho);
	// leilaoDao.salvar(noLimite);
	//
	// List<Leilao> antigos = leilaoDao.antigos();
	//
	// assertEquals(1, antigos.size());
	// }

}
