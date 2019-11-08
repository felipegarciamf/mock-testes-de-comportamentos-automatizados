package br.com.caelum.leilao.service;

import br.com.caelum.leilao.dominio.Pagamento;

public interface RepositorioDePagamentos {

	void salva(Pagamento pagamento);
	
}
