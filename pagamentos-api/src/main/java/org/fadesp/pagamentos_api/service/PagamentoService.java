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

    public Pagamento atualizarStatusPagamento(Long id, StatusPagamento novoStatus) {
        Pagamento pagamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if(pagamento.getStatus() ==  StatusPagamento.PROCESSADO_SUCESSO) {
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
        List<Pagamento> pagamentos = repository.findAll();

        if (codigoDebito != null) {
            pagamentos = pagamentos.stream()
                    .filter(p -> p.getCodigoDebito().equals(codigoDebito))
                    .toList();
        }

        if (cpfCnpj != null && !cpfCnpj.isBlank()) {
            pagamentos = pagamentos.stream()
                    .filter(p -> p.getCpfCnpj().equalsIgnoreCase(cpfCnpj))
                    .toList();
        }

        if (status != null) {
            pagamentos = pagamentos.stream()
                    .filter(p -> p.getStatus() == status)
                    .toList();
        }
        return pagamentos;
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