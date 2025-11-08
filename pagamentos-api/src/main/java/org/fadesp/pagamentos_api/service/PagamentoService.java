package org.fadesp.pagamentos_api.service;

import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    public PagamentoService(PagamentoRepository repository) {
        this.repository = repository;
    }

    public Pagamento criarPagamento(Pagamento pagamento) {
        pagamento.setStatus(StatusPagamento.PENDENTE);
        return repository.save(pagamento);
    }

    public List<Pagamento> listarPagamentos() {
        return repository.findAll();
    }

}