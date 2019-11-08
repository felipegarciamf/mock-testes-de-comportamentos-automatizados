package br.com.caelum.leilao.servico;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.relogio.Relogio;
import br.com.caelum.leilao.service.RepositorioDeLeiloes;
import br.com.caelum.leilao.service.RepositorioDePagamentos;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;

public class GeradorDePagamentoTest {


	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {
		
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Avaliador avaliador = new Avaliador();
		
		Leilao leilao = new CriadorDeLeilao().para("Playstation")
				.lance(new Usuario("Maria"), 20)
				.lance(new Usuario("Roberto"), 300)
				.lance(new Usuario("Maasd"), 2500.0)
				.constroi();
		
		avaliador.avalia(leilao);
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, avaliador, pagamentos);
		gerador.gera();
		
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		
		verify(pagamentos).salva(argumento.capture());
		
		Pagamento pagamentoGerado = argumento.getValue();
	
		assertEquals(2500.0, pagamentoGerado.getValor(), 0.0001);
	
	}
	
	
	@Test
	public void deveEmpurrarParaProximoDiaUtil() {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		Leilao leilao = new CriadorDeLeilao().para("Playstation")
		.lance(new Usuario("Jose da Silva"), 2000)
		.lance(new Usuario("Maria Pereira"), 2500)
		.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		
		Calendar sabado = Calendar.getInstance();
		sabado.set(2012, Calendar.APRIL, 7);
		
		when(relogio.hoje()).thenReturn(sabado);
		
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, new Avaliador(), pagamentos, relogio);
		gerador.gera();
		
		
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());
		
		Pagamento pagamentoGerado = argumento.getValue();
		
		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
		
		
		
				
	}
	
}
