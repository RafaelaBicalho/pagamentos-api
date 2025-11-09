package org.fadesp.pagamentos_api.controller;

import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.service.PagamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@Tag(name = "Pagamentos", description = "Gerenciamento de pagamentos da API")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    // --- Endpoint: POST /pagamentos (Criar) ---
    @Operation(summary = "Criar um novo pagamento",
            description = "Recebe um objeto Pagamento no corpo da requisição e cria um novo registro no sistema com status PENDENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pagamento.class))),

            @ApiResponse(responseCode = "400", description = "Dados do pagamento inválidos ou JSON malformado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class),
                            examples = @ExampleObject(
                                    name = "Erro Padrão 400",

                                    value = "{\n" +
                                            "  \"timestamp\": \"2025-11-09T19:31:30.247+00:00 (Data e Hora Atual)\",\n" +
                                            "  \"status\": 400,\n" +
                                            "  \"error\": \"Bad Request\",\n" +
                                            "  \"path\": \"/pagamentos\"\n" +
                                            "}"
                            )
                    ))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pagamento criarPagamento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do pagamento",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "ExemploPagamento",
                                    value = "{\n" +
                                            "  \"codigoDebito\": 12345,\n" +
                                            "  \"cpfCnpj\": \"12345678901\",\n" +
                                            "  \"metodoPagamento\": \"CARTAO_CREDITO\",\n" +
                                            "  \"numeroCartao\": \"4111111111111111\",\n" +
                                            "  \"valor\": 150.75\n" +
                                            "}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody Pagamento pagamento
    ) {
        return service.criarPagamento(pagamento);
    }

    // --- Endpoint: GET /pagamentos (Listar todos) ---
    @Operation(summary = "Listar todos os pagamentos",
            description = "Retorna a lista completa de pagamentos cadastrados no sistema.")
    @GetMapping
    public List<Pagamento> listarPagamentos() {
        return service.listarPagamentos();
    }

    // --- Endpoint: PATCH /pagamentos/{id}/status (Atualizar Status via URL Parameter) ---
    @Operation(summary = "Atualizar status de um pagamento",
            description = "Atualiza o status de um pagamento existente pelo seu ID, passando o novo status na URL como parâmetro de consulta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Pagamento.class))),

            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ou regra de negócio violada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class),
                            examples = @ExampleObject(
                                    name = "Erro Padrão 500",

                                    value = "{\n" +
                                            "  \"timestamp\": \"2025-11-09T19:31:30.247+00:00 (Data e Hora Atual)\",\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"error\": \"Internal Server Error\",\n" +
                                            "  \"path\": \"/pagamentos/{id}/status\"\n" +
                                            "}"
                            )
                    )),
    })
    @PatchMapping("/{id}/status")
    public Pagamento atualizarStatusPagamento(
            @PathVariable Long id,
            @Parameter(description = "Novo status do pagamento.", required = true, schema = @Schema(implementation = StatusPagamento.class))
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

    // --- Endpoint: GET /pagamentos/filtro (Filtrar) ---
    @Operation(summary = "Filtrar pagamentos",
            description = "Filtra os pagamentos de acordo com os parâmetros opcionais: ID, código do débito, CPF/CNPJ e status.")
    @GetMapping("/filtro")
    public List<Pagamento> filtrarPagamentos(
            // 🆕 NOVO FILTRO: ID
            @Parameter(description = "ID do pagamento (opcional)", example = "10")
            @RequestParam(required = false) Long id,

            @Parameter(description = "Código do débito (opcional)", example = "12345")
            @RequestParam(required = false) Integer codigoDebito,
            @Parameter(description = "CPF ou CNPJ do pagador (opcional)", example = "12345678901")
            @RequestParam(required = false) String cpfCnpj,
            @Parameter(description = "Status do pagamento (opcional)", example = "PENDENTE")
            @RequestParam(required = false) StatusPagamento status) {

        // 🎯 O MÉTODO DO SERVICE PRECISA SER ATUALIZADO PARA ACEITAR O PARÂMETRO 'id'
        return service.filtrarPagamentos(id, codigoDebito, cpfCnpj, status);
    }

    // --- Endpoint: DELETE /pagamentos/{id} (Excluir/Inativar) ---
    @Operation(summary = "Excluir (inativar) pagamento",
            description = "Marca o pagamento como inativo (ativo = false) se o pagamento estiver PENDENTE. Retorna o objeto atualizado (inativado).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento inativado com sucesso",

                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pagamento.class),
                            examples = @ExampleObject(
                                    name = "Pagamento Inativado",
                                    value = "{\n" +
                                            "  \"id\": 230,\n" +
                                            "  \"codigoDebito\": 12345,\n" +
                                            "  \"cpfCnpj\": \"12345678901\",\n" +
                                            "  \"metodoPagamento\": \"CARTAO_CREDITO\",\n" +
                                            "  \"numeroCartao\": \"4111111111111111\",\n" +
                                            "  \"valor\": 150.75,\n" +
                                            "  \"status\": \"PENDENTE\",\n" +
                                            "  \"ativo\": false\n" +
                                            "}"
                            )
                    )),

            @ApiResponse(responseCode = "500", description = "Erro interno do servidor ou regra de negócio violada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class),
                            examples = @ExampleObject(
                                    name = "Erro Padrão 500",
                                    value = "{\n" +
                                            "  \"timestamp\": \"2025-11-09T19:31:30.247+00:00 (Data e Hora Atual)\",\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"error\": \"Internal Server Error\",\n" +
                                            "  \"path\": \"/pagamentos/{id}\"\n" +
                                            "}"
                            )
                    )),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Pagamento excluirPagamento(@PathVariable Long id) {
        return service.excluirPagamento(id);
    }
}