package br.com.caelum.leilao.servico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.email.EnviadorDeEmail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Matchers.any;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		// Classe CriadorDeLeiao � uma classe Data Builder, que auxilia na hora do teste
		Leilao leilao1 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();

		Leilao leilao2 = new CriadorDeLeilao().para("VideoGame").naData(antiga).constroi();

		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		// Objetos que simulam os comportamentos dos objetos reais � o que chamamos de
		// mock objects
		// criando um mock do objeto que queremos simular.
		LeilaoDao daoFalso = mock(LeilaoDao.class);

		// when() recebe o m�todo que queremos simular,thenReturn() recebe o que o
		// m�todo falso deve devolver
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerradorDeLeilao.encerra();

		List<Leilao> encerrados = daoFalso.encerrados();

		assertEquals(2, encerradorDeLeilao.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}

	@Test
	public void deveAtualizarLeiloesEncerrados() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);

		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerradorDeLeilao.encerra();
		// verify(). Nele, indicaremos qual m�todo que queremos verificar se foi
		// invocado
		// ele ira ver se esse metodo foi invocado
		// times(), esta definido o numero de vezes que foi invocado
		verify(daoFalso, times(1)).atualiza(leilao1);

	}

	@Test
	public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {

		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);

		Leilao leilao1 = new CriadorDeLeilao().para("Notebook").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("VideoGame").naData(ontem).constroi();

		LeilaoDao daoFalso = mock(LeilaoDao.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		assertEquals(0, encerrador.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());

		// never(), vai verificar se o metodo n�o foi invocado nenhuma vez
		verify(daoFalso, never()).atualiza(leilao1);

		// outros casos
		// atLeastOnce() vai garantir que o m�todo foi invocado no m�nimo uma vez
		// atLeast(numero) funciona de forma an�loga ao m�todo acima, com a diferen�a de
		// que passamos para ele o n�mero m�nimo de invoca��es que um m�todo deve ter.
		// atMost(numero) nos garante que um m�todo foi executado at� no m�ximo N vezes.
		// Ou seja, se o m�todo tiver mais invoca��es do que o que foi passado para o
		// atMost, o teste falha.
	}

	@Test
	public void naoDeveEncerrarLeiloesCasoNaoHajaNenhum() {

		LeilaoDao daoFalso = mock(LeilaoDao.class);
		when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		assertEquals(0, encerrador.getTotalEncerrados());
	}

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("VideoGame").naData(antiga).constroi();

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}

	@Test
	public void deveEnviarEmailAposPersistirLeilaoEncerrado() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);

		encerrador.encerra();

		// InOrder, quer dizer que os testes ser�o verificados nesta ordem especifica
		// primeiro o DAO, depois o envio do e-mail
		// passamos os mocks que serao verificados
		InOrder inOrder = inOrder(daoFalso, carteiroFalso);
		// a primeira invoca��o
		inOrder.verify(daoFalso, times(1)).atualiza(leilao1);
		// a segunda invoca��o
		inOrder.verify(carteiroFalso, times(1)).envia(leilao1);
	}

	@Test
	public void deveContinuarAExecucaoMesmoQuandoDaoFalha() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);

		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		// queremos a exece��o, quando daoFalso atualiza
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();

		verify(daoFalso).atualiza(leilao2);
		verify(carteiroFalso).envia(leilao2);
	}

	@Test
	public void deveDesistirSeDaoFalhaPraSempre() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("VideoGame").naData(antiga).constroi();

		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		//when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		//Perceba que, para esses testes, n�o importa se � leilao1 ou leilao2. A exce��o ser� lan�ada para qualquer leil�o. Podemos escrever isso com apenas uma linha
		doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao2);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);

		encerrador.encerra();

//		verify(carteiroFalso, never()).envia(leilao1);
//		verify(carteiroFalso, never()).envia(leilao2);
		verify(carteiroFalso, never()).envia(any(Leilao.class));
	}
	
	//A classe Matchers possui diversos m�todos que podem ser usados para especificarmos que argumentos nosso mock pode receber numa chamada de m�todo
	//Podemos, por exemplo, garantir que um mock vai ser chamado com uma String come�ando com "Importante:"
	//verify(meuMock).imprimeMensagem(startsWith("Importante:"));

}