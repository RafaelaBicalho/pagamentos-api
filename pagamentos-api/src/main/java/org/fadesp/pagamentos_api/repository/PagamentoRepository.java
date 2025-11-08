package org.fadesp.pagamentos_api.repository;

import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByCodigoDebito(Integer codigoDebito);
    List<Pagamento> findByCpfCnpj(String cpfCnpj);
    List<Pagamento> findByStatus(StatusPagamento status);
}