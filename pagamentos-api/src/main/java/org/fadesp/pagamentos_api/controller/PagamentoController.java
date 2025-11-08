package org.fadesp.pagamentos_api.controller;

import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    public Pagamento criarPagamento(@RequestBody Pagamento pagamento) {
        return service.criarPagamento(pagamento);
    }

    @GetMapping
    public List<Pagamento> listarPagamentos() {
        return service.listarPagamentos();
    }

    @PatchMapping("/{id}/status")
    public Pagamento atualizarStatusPagamento(
            @PathVariable Long id,
            @RequestParam("novoStatus") StatusPagamento novoStatus) {
        return service.atualizarStatusPagamento(id, novoStatus);
    }
}