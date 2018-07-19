package br.com.caelum.leilao.dominio;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import junit.framework.Assert;

public class LeilaTeste {
	
	@Test
	public void deveReceberUmLance() {
		
		Leilao leilao = new Leilao("Dell gamer");
		assertEquals(0, leilao.getLances().size());
		
		leilao.propoe(new Lance(new Usuario("André"),3000));
		
		assertEquals(1, leilao.getLances().size());
		assertEquals(3000.0, leilao.getLances().get(0).getValor(),0.0001);
		
	}
	
	@Test
	public void deveReceberVariosLance() {
		
		Leilao leilao = new Leilao("Dell gamer");
		leilao.propoe(new Lance(new Usuario("André"),3000));
		leilao.propoe(new Lance(new Usuario("Lucas"),4000));
		
		assertEquals(2, leilao.getLances().size());
		assertEquals(3000.0, leilao.getLances().get(0).getValor(),0.0001);
		assertEquals(4000.0, leilao.getLances().get(1).getValor(),0.0001);
	}
	
	@Test
	public void naoDeveAceitarDoisLeiloesSeguidosDosMesmosUsuarios() {
		
		Leilao leilao = new Leilao("Dell gamer");
		Usuario andre = new Usuario("André");
		
		leilao.propoe(new Lance(andre,3000));
		leilao.propoe(new Lance(andre,4000));
		
		assertEquals(1, leilao.getLances().size());
		assertEquals(3000, leilao.getLances().get(0).getValor(),0.0001);
	}
	
	@Test
	public void naoDeveAceitarMaisDoQueCincoLancesDeUmMesmoUsuario() {
		
		Leilao leilao = new Leilao("Dell gamer");
		Usuario andre = new Usuario("André");
		Usuario lucas = new Usuario("Lucas");
		
		leilao.propoe(new Lance(andre,3000));
		leilao.propoe(new Lance(lucas,4000));
		
		leilao.propoe(new Lance(andre,5000));
		leilao.propoe(new Lance(lucas,6000));
		
		leilao.propoe(new Lance(andre,7000));
		leilao.propoe(new Lance(lucas,8000));
		
		leilao.propoe(new Lance(andre,9000));
		leilao.propoe(new Lance(lucas,10000));
		
		leilao.propoe(new Lance(andre,11000));
		leilao.propoe(new Lance(lucas,12000));
		
		//deve ignorar
		leilao.propoe(new Lance(andre,13000));
		
		assertEquals(10, leilao.getLances().size());
		assertEquals(12000, leilao.getLances().get(leilao.getLances().size()-1).getValor(),0.0001);
	}

}
