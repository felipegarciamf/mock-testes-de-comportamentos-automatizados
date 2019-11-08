package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.email.EnviadorDeEmail;
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
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daofalso, carteiroFalso);
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
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);

		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		
		verify(daoFalso, never()).atualiza(leilao1);
		verify(daoFalso, never()).encerrados();
		encerradorDeLeilao.encerra();

	}
	
	@Test 
	public void deveRetornarListaVaziaCasoNaoHajaNenhumLeilao() {
		
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 9, 18);
		
		RepositorioLeiloes daoFalso = mock(RepositorioLeiloes.class);
		when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(daoFalso, carteiroFalso);
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
		
		Leilao leilao = new CriadorDeLeilao().para("TV DE PLASMA").naData(antiga).constroi();
		RepositorioLeiloes daoFalso = mock(LeilaoDao.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao));
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		

		verify(daoFalso, times(1)).atualiza(leilao);
		
	}
	
	
	@Test
	public void deveContinuarExecucaoMesmoQuandoDaoFalha() {
		
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV DE PLASMA").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("GELADEIRA").naData(antiga).constroi();
		
		
		RepositorioLeiloes daoFalso = mock(RepositorioLeiloes.class);
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		verify(daoFalso).atualiza(leilao2);
		verify(carteiroFalso).envia(leilao2);
		
		verify(carteiroFalso, times(0)).envia(leilao1);
		
	}

}
