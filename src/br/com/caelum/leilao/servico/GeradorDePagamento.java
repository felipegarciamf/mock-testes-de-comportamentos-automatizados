package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.relogio.Relogio;
import br.com.caelum.leilao.infra.relogio.RelogioDoSistema;
import br.com.caelum.leilao.service.RepositorioDeLeiloes;
import br.com.caelum.leilao.service.RepositorioDePagamentos;

public class GeradorDePagamento {

	
	private RepositorioDeLeiloes leiloes;
	private Avaliador avaliador;
	private RepositorioDePagamentos pagamentos;
	private Relogio relogio;

	public GeradorDePagamento(RepositorioDeLeiloes leiloes, Avaliador avaliador, RepositorioDePagamentos pagamentos
			,Relogio relogio) {
		this.leiloes = leiloes;
		this.avaliador = avaliador;
		this.pagamentos = pagamentos;
		this.relogio = relogio;
	}
	
	public GeradorDePagamento(RepositorioDeLeiloes leiloes, Avaliador avaliador, RepositorioDePagamentos pagamentos) {
		this(leiloes, avaliador, pagamentos, new RelogioDoSistema());
	}
	
	public void gera() {
		List<Leilao> leiloesEncerrados = this.leiloes.encerrados();
		
		for (Leilao leilao : leiloesEncerrados) {
			this.avaliador.avalia(leilao);
			
			Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
			this.pagamentos.salva(novoPagamento);
		}
	}

	private Calendar primeiroDiaUtil() {
		Calendar data = relogio.hoje();
		int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);
		if (diaDaSemana == Calendar.SATURDAY) data.add(Calendar.DAY_OF_MONTH, 2);
		else if (diaDaSemana == Calendar.SUNDAY) data.add(Calendar.DAY_OF_MONTH, 1);
		return data;
	}
	
	
	
}
