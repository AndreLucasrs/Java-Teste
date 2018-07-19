package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.junit.MatcherAssume;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.servico.Avaliador;
import junit.framework.Assert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matcher.*;
import static org.hamcrest.CoreMatchers.*;

public class AvaliadorTest {

	private Avaliador leiloeiro;
	private Usuario andre;
	private Usuario lucas;
	private Usuario maryana;
	
	//com essa anotation ele ira criarAvaliador(), e depois faz o teste no metodo, nesta classe de teste, 
	//como a 7 metodos usando, antes de ele começar o teste do metodo ele criaAvaliador()
	@Before
	public void criaAvaliador() {
		leiloeiro = new Avaliador();
		andre = new Usuario("André");
		lucas = new Usuario("Lucas");
		maryana = new Usuario("Maryana");
	}
	
	@Test(expected=RuntimeException.class)
	public void naoDeveAvaliarLeiloesSemNenhumLanceDado() {
			Leilao leilao = new CriadorDeLeilao().para("Playstation 3 novo").constroi();
			leiloeiro.avalia(leilao);
	}
	
	@Test
	public void deveEntenderLancesEmOrdemCrescente() {

		// Parte 1: cenario
		Leilao leilao = new CriadorDeLeilao().para("Playstation 3 novo")
							.lance(andre, 250.0)
							.lance(lucas, 300.0)
							.lance(maryana, 400.0)
							.constroi();

		// Parte 2: acao
		leiloeiro.avalia(leilao);

		// Parte 3: validacao

		// Como o doub le tem problema de arredondamento, o junit vai aceitar
		// essa pequena diferenca 0.00001, isso é o delta
		assertThat(leiloeiro.getMaiorLance(), equalTo(400.0));
		assertThat(leiloeiro.getMenorLance(), equalTo(250.0));

	}

	@Test
	public void deveEntenderLeilaoComApenasUmLance() {
		
		Leilao leilao = new CriadorDeLeilao().para("Playstation 3 novo")
				.lance(andre, 1000.0)
				.constroi();

		leiloeiro.avalia(leilao);

		assertEquals(1000.0, leiloeiro.getMaiorLance(), 0.00001);
		assertEquals(1000.0, leiloeiro.getMenorLance(), 0.00001);
	}

	@Test
	public void deveEncontrarOsTresMaioresLances() {

		Leilao leilao =  new CriadorDeLeilao().para("Playstation 3 novo")
				.lance(andre,100.0)
				.lance(lucas,200.0)
				.lance(andre,300.0)
				.lance(lucas,400.0)
				.constroi();

		leiloeiro.avalia(leilao);

		List<Lance> maiores = leiloeiro.getTresMaiores();

		assertEquals(3, maiores.size());
		
		assertThat(maiores, hasItems(
				new Lance(lucas, 400),
				new Lance(andre, 300),
				new Lance(lucas, 200)
		));

	}

	@Test
	public void deveCalcularAMedia() {

		Leilao leilao = new CriadorDeLeilao().para("Playstation 3 novo")
				.lance(andre, 300.0)
				.lance(lucas, 400.0)
				.lance(maryana, 500.0)
				.constroi();

		// executando a acao
		leiloeiro.avalia(leilao);

		// comparando a saida com o esperado
		assertEquals(400, leiloeiro.getMediaLance(), 0.0001);
	}

	@Test(expected=RuntimeException.class)
	public void testaMediaDeZeroLance() {
		
		// acao
		Leilao leilao = new Leilao("Iphone 7");

		leiloeiro.avalia(leilao);

		// validacao
		assertEquals(0, leiloeiro.getMediaLance(), 0.0001);

	}

	@Test
	public void deveEntenderLeilaoComLancesEmOrdemRandomica() {
		
		Leilao leilao = new CriadorDeLeilao().para("Playstation 3 novo")
				.lance(andre, 200.0)
				.lance(lucas, 450.0)
				.lance(andre, 120.0)
				.lance(lucas, 700.0)
				.lance(andre, 630.0)
				.lance(lucas, 230.0)
				.constroi();

		leiloeiro.avalia(leilao);

		assertEquals(700.0, leiloeiro.getMaiorLance(), 0.0001);
		assertEquals(120.0, leiloeiro.getMenorLance(), 0.0001);
	}
	
	@Test
    public void deveEntenderLeilaoComLancesEmOrdemDecrescente() {
		
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 novo")
        		.lance(andre, 400.0)
        		.lance(lucas, 300.0)
        		.lance(andre, 200.0)
        		.lance(lucas, 100.0)
        		.constroi();

        leiloeiro.avalia(leilao);

        assertEquals(400.0, leiloeiro.getMaiorLance(), 0.0001);
        assertEquals(100.0, leiloeiro.getMenorLance(), 0.0001);
    }
}
