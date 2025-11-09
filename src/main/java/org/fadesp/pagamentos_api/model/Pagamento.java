package org.fadesp.pagamentos_api.model;

import org.fadesp.pagamentos_api.enums.MetodoPagamento;
import org.fadesp.pagamentos_api.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do pagamento", example = "1")
    private Long id;

    @Schema(description = "Código do débito", example = "12345")
    private Integer codigoDebito;

    @Schema(description = "CPF ou CNPJ do pagador", example = "12345678901")
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Método de pagamento", example = "CARTAO_CREDITO")
    private MetodoPagamento metodoPagamento;

    @Schema(description = "Número do cartão", example = "4111111111111111")
    private String numeroCartao;

    @Schema(description = "Valor do pagamento", example = "150.75")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status do pagamento", example = "PENDENTE")
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Schema(description = "Registro ativo", example = "true")
    private Boolean ativo = true;
}
