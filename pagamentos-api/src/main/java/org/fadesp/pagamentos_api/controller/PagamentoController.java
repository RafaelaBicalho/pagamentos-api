package org.fadesp.pagamentos_api.controller;

import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.service.PagamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo pagamento",
            description = "Recebe um objeto Pagamento no corpo da requisição e cria um novo registro no sistema com status PENDENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do pagamento inválidos")
    })
    @PostMapping
    public Pagamento criarPagamento(@RequestBody Pagamento pagamento) {
        return service.criarPagamento(pagamento);
    }

    @Operation(summary = "Listar todos os pagamentos",
            description = "Retorna a lista completa de pagamentos cadastrados no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos retornada com sucesso")
    })
    @GetMapping
    public List<Pagamento> listarPagamentos() {
        return service.listarPagamentos();
    }

    @Operation(summary = "Atualizar status de um pagamento",
            description = "Atualiza o status de um pagamento existente pelo seu ID.\n" +
                    "O novo status deve ser passado como query parameter 'novoStatus'.\n" +
                    "Exemplo: ?novoStatus=PROCESSADO-SUCESSO\n" +
                    "Regras:\n" +
                    "- Se o pagamento já estiver em PROCESSADO_SUCESSO, não pode ser alterado.\n" +
                    "- Se estiver em PROCESSADO_FALHA, só pode voltar para PENDENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Alteração de status inválida")
    })
    @PatchMapping("/{id}/status")
    public Pagamento atualizarStatusPagamento(
            @PathVariable Long id,
            @Parameter(description = "Novo status do pagamento, ex: PROCESSADO-SUCESSO", required = true)
            @RequestParam("novoStatus") String novoStatusStr) {

        String enumFormat = novoStatusStr.replace("-", "_").toUpperCase();
        StatusPagamento novoStatus;
        try {
            novoStatus = StatusPagamento.valueOf(enumFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido: " + novoStatusStr);
        }

        return service.atualizarStatusPagamento(id, novoStatus);
    }

    @Operation(summary = "Filtrar pagamentos",
            description = "Filtra os pagamentos de acordo com os parâmetros opcionais: código do débito, CPF/CNPJ e status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamentos filtrados retornados com sucesso")
    })
    @GetMapping("/filtro")
    public List<Pagamento> filtrarPagamentos(
            @RequestParam(required = false) Integer codigoDebito,
            @RequestParam(required = false) String cpfCnpj,
            @RequestParam(required = false) StatusPagamento status) {
        return service.filtrarPagamentos(codigoDebito, cpfCnpj, status);
    }

    @Operation(summary = "Excluir (inativar) pagamento",
            description = "Marca o pagamento como inativo (ativo = false) se o pagamento estiver PENDENTE. " +
                    "Pagamentos em outro status não podem ser inativados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento marcado como inativo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Pagamento não pode ser inativado")
    })
    @DeleteMapping("/{id}")
    public Pagamento excluirPagamento(@PathVariable Long id) {
        return service.excluirPagamento(id);
    }
}
