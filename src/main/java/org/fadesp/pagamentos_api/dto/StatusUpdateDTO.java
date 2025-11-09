package org.fadesp.pagamentos_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.fadesp.pagamentos_api.enums.StatusPagamento;

@Schema(description = "DTO para atualização do status de um pagamento.")
public class StatusUpdateDTO {

    @Schema(description = "O novo status desejado para o pagamento.", example = "APROVADO")
    private StatusPagamento novoStatus;

    public StatusUpdateDTO() {
    }

    public StatusPagamento getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(StatusPagamento novoStatus) {
        this.novoStatus = novoStatus;
    }
}