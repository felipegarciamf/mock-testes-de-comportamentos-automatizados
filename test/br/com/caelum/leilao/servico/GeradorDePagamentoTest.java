package br.com.caelum.leilao.servico;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.service.RepositorioDeLeiloes;
import br.com.caelum.leilao.service.RepositorioDePagamentos;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;

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
}
