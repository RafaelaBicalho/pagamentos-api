package org.fadesp.pagamentos_api.controller;

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
}