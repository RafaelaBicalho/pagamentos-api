package org.fadesp.pagamentos_api.service;

import org.fadesp.pagamentos_api.enums.StatusPagamento;
import org.fadesp.pagamentos_api.model.Pagamento;
import org.fadesp.pagamentos_api.repository.PagamentoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository repository;

    @InjectMocks
    private PagamentoService service;

    private Pagamento pagamentoPendente;
    private final Long ID_PAGAMENTO = 1L;
    private final String CPF_VALIDO = "12345678901";
    private final String CNPJ_VALIDO = "12345678901234";

    @BeforeEach
    void setUp() {
        pagamentoPendente = new Pagamento();
        pagamentoPendente.setId(ID_PAGAMENTO);
        pagamentoPendente.setStatus(StatusPagamento.PENDENTE);
        pagamentoPendente.setAtivo(true);
        pagamentoPendente.setValor(new BigDecimal("100.00"));
        pagamentoPendente.setCpfCnpj(CPF_VALIDO);
    }

    // --- Testes para criarPagamento (POST) ---

    @Test
    void deveCriarPagamentoComStatusEPadraoAtivo() {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setValor(new BigDecimal("250.50"));
        novoPagamento.setCpfCnpj(CNPJ_VALIDO);

        when(repository.save(any(Pagamento.class))).thenAnswer(invocation -> {
            Pagamento p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        Pagamento resultado = service.criarPagamento(novoPagamento);

        assertNotNull(resultado.getId());
        assertEquals(StatusPagamento.PENDENTE, resultado.getStatus());
        assertTrue(resultado.getAtivo());
        verify(repository, times(1)).save(novoPagamento);
    }

    @Test
    void deveLancarExcecao_AoCriarComCpfCnpjNulo() {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setCpfCnpj(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.criarPagamento(novoPagamento);
        });

        assertEquals("CPF ou CNPJ não pode ser vazio.", exception.getMessage());
        verify(repository, never()).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecao_AoCriarComCpfCnpjComTamanhoInvalido() {
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setCpfCnpj("12345");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.criarPagamento(novoPagamento);
        });

        assertTrue(exception.getMessage().contains("CPF/CNPJ inválido. Deve conter 11 (CPF) ou 14 (CNPJ) dígitos numéricos."));
        verify(repository, never()).save(any(Pagamento.class));
    }

    // --- Testes para listarPagamentos (GET) ---

    @Test
    void deveRetornarTodosOsPagamentos() {
        Pagamento pag2 = new Pagamento();
        pag2.setId(2L);
        pag2.setCpfCnpj(CNPJ_VALIDO);
        pag2.setStatus(StatusPagamento.PROCESSADO_SUCESSO);

        List<Pagamento> listaEsperada = Arrays.asList(pagamentoPendente, pag2);

        when(repository.findAll()).thenReturn(listaEsperada);

        List<Pagamento> resultado = service.listarPagamentos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    // --- Testes para filtrarPagamentos (GET /filtro) ---

    @Test
    void deveChamarFindAllComSpecification_QuandoFiltroIDFornecido() {
        Long filtroId = 1L;

        when(repository.findAll(any(Specification.class))).thenReturn(Arrays.asList(pagamentoPendente));

        service.filtrarPagamentos(filtroId, null, null, null);

        verify(repository, times(1)).findAll(any(Specification.class));
        verify(repository, never()).findAll();
    }

    @Test
    void deveChamarFindAllComSpecification_QuandoFiltroCpfCnpjValidoFornecido() {
        String filtroCpf = CPF_VALIDO;

        when(repository.findAll(any(Specification.class))).thenReturn(Arrays.asList(pagamentoPendente));

        service.filtrarPagamentos(null, null, filtroCpf, null);

        verify(repository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void deveLancarExcecao_QuandoFiltroCpfCnpjInvalidoFornecido() {
        String filtroCpfInvalido = "123";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.filtrarPagamentos(null, null, filtroCpfInvalido, null);
        });

        assertTrue(exception.getMessage().contains("CPF/CNPJ inválido."));
        verify(repository, never()).findAll(any(Specification.class));
    }


    // --- Testes para atualizarStatusPagamento (PATCH) ---

    @Test
    void deveAtualizarStatusComSucesso_DePendenteParaSucesso() {
        StatusPagamento novoStatus = StatusPagamento.PROCESSADO_SUCESSO;
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.of(pagamentoPendente));
        when(repository.save(any(Pagamento.class))).thenAnswer(i -> i.getArguments()[0]);

        Pagamento resultado = service.atualizarStatusPagamento(ID_PAGAMENTO, novoStatus);

        assertEquals(novoStatus, resultado.getStatus());
        verify(repository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecao_AoTentarAlterarParaOMesmoStatus() {
        StatusPagamento statusAtual = StatusPagamento.PENDENTE;
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.of(pagamentoPendente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.atualizarStatusPagamento(ID_PAGAMENTO, statusAtual);
        });

        assertTrue(exception.getMessage().contains("Não é permitida a alteração para o mesmo status."));
        verify(repository, never()).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecao_AoTentarAlterarPagamentoProcessadoSucesso() {
        pagamentoPendente.setStatus(StatusPagamento.PROCESSADO_SUCESSO);
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.of(pagamentoPendente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.atualizarStatusPagamento(ID_PAGAMENTO, StatusPagamento.PROCESSADO_FALHA);
        });

        assertEquals("Pagamento processado com sucesso. Não pode ser alterado.", exception.getMessage());
        verify(repository, never()).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecao_QuandoPagamentoNaoEncontradoNoPatch() {
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.atualizarStatusPagamento(ID_PAGAMENTO, StatusPagamento.PROCESSADO_SUCESSO);
        });

        assertEquals("Pagamento não encontrado", exception.getMessage());
        verify(repository, never()).save(any(Pagamento.class));
    }

    // --- Testes para excluirPagamento (DELETE/Inativação) ---

    @Test
    void deveExcluirPagamentoComSucesso_QuandoPendente() {
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.of(pagamentoPendente));
        when(repository.save(any(Pagamento.class))).thenAnswer(i -> i.getArguments()[0]);

        Pagamento resultado = service.excluirPagamento(ID_PAGAMENTO);

        assertFalse(resultado.getAtivo());
        verify(repository, times(1)).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecao_AoTentarExcluirPagamentoNaoPendente() {
        pagamentoPendente.setStatus(StatusPagamento.PROCESSADO_FALHA);
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.of(pagamentoPendente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.excluirPagamento(ID_PAGAMENTO);
        });

        assertEquals("Somente pagamentos pendentes podem ser inativados.", exception.getMessage());
        verify(repository, never()).save(any(Pagamento.class));
    }

    @Test
    void deveLancarExcecao_QuandoPagamentoNaoEncontradoNoDelete() {
        when(repository.findById(ID_PAGAMENTO)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.excluirPagamento(ID_PAGAMENTO);
        });

        assertEquals("Pagamento não encontrado", exception.getMessage());
        verify(repository, never()).save(any(Pagamento.class));
    }
}