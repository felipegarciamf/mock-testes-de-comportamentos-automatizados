package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.LeilaoDaoFalso;
import br.com.caelum.leilao.service.RepositorioLeiloes;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV ANTIGA").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();
		
		RepositorioLeiloes daofalso = mock(LeilaoDao.class);
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
	
		
		when(daofalso.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daofalso);
		encerrador.encerra();
		
	
		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}
	
	@Test
	public void naoDeveLeiloesQueComecaramOntem() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(2019, 9, 18);
		
		Leilao leilao1 = new CriadorDeLeilao().naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().naData(antiga).constroi();
		
		RepositorioLeiloes daoFalso = mock(RepositorioLeiloes.class);
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso);
		encerradorDeLeilao.encerra();
		
		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
	}
	
	@Test 
	public void deveRetornarListaVaziaCasoNaoHajaNenhumLeilao() {
		
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 9, 18);
		
		RepositorioLeiloes daoFalso = mock(RepositorioLeiloes.class);
		when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso);
		encerradorDeLeilao.encerra();
		
		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		
	}
	
	@Test
	public void teste() {	
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		when(daoFalso.teste()).thenReturn("TESTE");
		assertEquals("TESTE", daoFalso.teste());
	}
	
	@Test
	public void deveAtualizarLeiloesEncerrados() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 9, 18);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV DE PLASMA").naData(antiga).constroi();
		RepositorioLeiloes daoFalso = mock(LeilaoDao.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();
		
		verify(daoFalso).atualiza(leilao1);
		
	}

}
