package org.fadesp.pagamentos_api.service;

import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.repository.PagamentoRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;

    public PagamentoService(PagamentoRepository repository) {
        this.repository = repository;
    }

    public Pagamento criarPagamento(Pagamento pagamento) {
        if (pagamento.getStatus() == null) {
            pagamento.setStatus(StatusPagamento.PENDENTE);
        }
        if (pagamento.getAtivo() == null) {
            pagamento.setAtivo(true);
        }
        return repository.save(pagamento);
    }

    public List<Pagamento> listarPagamentos() {
        return repository.findAll();
    }

    public Pagamento atualizarStatusPagamento(Long id, StatusPagamento novoStatus) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if(pagamento.getStatus() == StatusPagamento.PROCESSADO_SUCESSO) {
            throw new RuntimeException("Pagamento processado com sucesso. Não pode ser alterado.");
        }

        if (pagamento.getStatus() == StatusPagamento.PROCESSADO_FALHA
                && novoStatus != StatusPagamento.PENDENTE) {
            throw new RuntimeException("Pagamento com falha só pode voltar para PENDENTE.");
        }

        pagamento.setStatus(novoStatus);
        return repository.save(pagamento);
    }

    public List<Pagamento> filtrarPagamentos(Integer codigoDebito, String cpfCnpj, StatusPagamento status) {

        Specification<Pagamento> spec = Specification.anyOf();

        if (codigoDebito != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("codigoDebito"), codigoDebito));
        }

        if (cpfCnpj != null && !cpfCnpj.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("cpfCnpj"), cpfCnpj));
        }

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        return repository.findAll(spec);
    }

    public Pagamento excluirPagamento(Long id) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
            throw new RuntimeException("Somente pagamentos pendentes podem ser inativados.");
        }

        pagamento.setAtivo(false);
        return repository.save(pagamento);
    }
}