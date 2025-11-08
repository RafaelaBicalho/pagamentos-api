package org.fadesp.pagamentos_api.model;

import org.fadesp.pagamentos_api.enums.MetodoPagamento;
import org.fadesp.pagamentos_api.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer codigoDebito;

    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    private String numeroCartao;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    private Boolean ativo = true;
}
