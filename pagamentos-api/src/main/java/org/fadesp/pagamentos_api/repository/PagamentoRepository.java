package org.fadesp.pagamentos_api.repository;

import org.fadesp.pagamentos_api.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PagamentoRepository extends
        JpaRepository<Pagamento, Long>,
        JpaSpecificationExecutor<Pagamento> {}