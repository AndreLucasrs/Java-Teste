package br.com.caelum.leilao.servico;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;
import br.com.caelum.leilao.infra.relogio.Relogio;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Calendar;

public class GeradorDePagamentoTest {

	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Avaliador avaliador = mock(Avaliador.class);
		
		Leilao leilao = new CriadorDeLeilao().para("Notebook")
				.lance(new Usuario("André"), 2000.0)
				.lance(new Usuario("Lucas"), 2500.0)
				.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		when(avaliador.getMaiorLance()).thenReturn(2500.0);
		
		GeradorDePagamento geradorDePagamento = new GeradorDePagamento(leiloes, pagamentos, avaliador);
		geradorDePagamento.gera();
		
		//Temos que usar o ArgumentCaptor devido dentro desse metodo o Pagamento se estanciado
		//ArgumentCaptor possibilita capturar a instância que foi passada para o Mock
		//ArgumentCaptor capturar um Pagamento
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		//ele vai verifica se foi invocado e vai capturar o Pagamento que foi passado para o metodo salvar
		verify(pagamentos).salva(argumento.capture());
		
		//argumento.getValue(), ele pega o objeto que foi recebido pelo mock
		//Pegamos a instacia do pagamento que foi criado
		Pagamento pagamentoGerado = argumento.getValue();
		
		//e verificamos o valor
		assertEquals(2500.0, pagamentoGerado.getValor(),0.00001);
	}
	
	//ArgumentCaptor nos permite acessar objetos criados pela classe testada
	
	@Test
	public void deveEmpurrarParaOProximoDiaUtil() {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		Leilao leilao = new CriadorDeLeilao().para("Notebook")
				.lance(new Usuario("André"), 2000.0)
				.lance(new Usuario("Lucas"), 2500.0)
				.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		
		Calendar sabado = Calendar.getInstance();
		sabado.set(2018,Calendar.JULY, 21);
		
		when(relogio.hoje()).thenReturn(sabado);
		
		GeradorDePagamento geradorDePagamento = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(),relogio);
		geradorDePagamento.gera();
		
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());
		
		Pagamento pagamentoGerado = argumento.getValue();
		
		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		assertEquals(23, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}
}
