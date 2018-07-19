package br.com.caelum.leilao.servico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.management.RuntimeErrorException;

import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.dominio.Leilao;

public class Avaliador {
	
	//Double.Negative_infinity, ela guarda o menor numero que cabe dentro do double
	private double maiorDetodos = Double.NEGATIVE_INFINITY;
	//Double.Positive_Infinity, todo mundo vai ser menor que esse numero, e ele ira se substituido a primeira vez
	private double menorDeTodos = Double.POSITIVE_INFINITY;
	private double media = 0;
	private List<Lance> maiores;

	public void avalia(Leilao leilao) {
		
		if(leilao.getLances().size() == 0) {
			throw new RuntimeException("Não é possivel avaliar um leilão sem lances");
		}
		
		double total = 0;
		for (Lance lance : leilao.getLances()) {
			
			total += lance.getValor();
			if(lance.getValor() > maiorDetodos )  maiorDetodos = lance.getValor();
			if(lance.getValor() < menorDeTodos) menorDeTodos = lance.getValor();
			
		}
		
		pegaOsMaiores(leilao);
		
		calculaMedia(leilao, total);
	}

	private void calculaMedia(Leilao leilao, double total) {
		if(total == 0) {
			media =0;
			return;
		}
		media = total/leilao.getLances().size();
	}

	private void pegaOsMaiores(Leilao leilao) {
		maiores = new ArrayList<Lance>(leilao.getLances());
		Collections.sort(maiores, new Comparator<Lance>() {
			
			public int compare(Lance o1, Lance o2) {
				if(o1.getValor() < o2.getValor()) {
					return 1;
				}
				if(o1.getValor() > o2.getValor()) {
					return -1;
				}
				return 0;
			}
		});
		
		maiores = maiores.subList(0, maiores.size() > 3 ? 3 : maiores.size());
	}
	
	public List<Lance> getTresMaiores() {
		return maiores;
	}

	public double getMediaLance() {
		return media;
	}
	
	public double getMaiorLance() {
		return maiorDetodos;
	}

	public double getMenorLance() {
		return menorDeTodos;
	}
}
