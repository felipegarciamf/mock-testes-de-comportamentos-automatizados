package br.com.caelum.leilao.service;

import java.util.List;

import br.com.caelum.leilao.dominio.Leilao;

public interface RepositorioLeiloes {

	void salva(Leilao leilao);
	List<Leilao> encerrados();
	List<Leilao> correntes();
	void atualiza(Leilao leilao);
	
}
