package br.com.alura.food.pagamentos;

import br.com.alura.food.pagamentos.dto.PagamentoDto;
import br.com.alura.food.pagamentos.resource.PagamentoResource;
import br.com.alura.food.pagamentos.service.PagamentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoResourceTest {

	@Mock
	private PagamentoService pagamentoService;

	@InjectMocks
	private PagamentoResource pagamentoResource;

	@Test
	void obterTodos_deveRetornarListaDePagamentos() {
		Pageable paginacao = Pageable.ofSize(10).withPage(0).withPage(1);
		Page<PagamentoDto> page = mock(Page.class);
		when(pagamentoService.obterTodos(paginacao)).thenReturn(page);

		ResponseEntity<Page<PagamentoDto>> response = pagamentoResource.obterTodos(paginacao);

		assertEquals(page, response.getBody());
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	void obterPorId_deveRetornarPagamentoPorId() {
		Long id = 1L;
		PagamentoDto dto = mock(PagamentoDto.class);
		when(pagamentoService.obterPorID(id)).thenReturn(dto);

		ResponseEntity<PagamentoDto> response = pagamentoResource.obterPorId(id);

		assertEquals(dto, response.getBody());
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	void cadastrar_deveRetornarPagamentoCriado() {
		PagamentoDto dto = mock(PagamentoDto.class);
		PagamentoDto pagamento = mock(PagamentoDto.class);
		UriComponentsBuilder uri = mock(UriComponentsBuilder.class);
		URI endereco = URI.create("http://localhost:8080/pagamentos/1");
		when(pagamentoService.criarPagamento(dto)).thenReturn(pagamento);
		when(uri.path("/pagamentos/{id}")).thenReturn(uri);
		when(uri.buildAndExpand(pagamento.getId())).thenReturn(uri.build());
		when(uri.toUriString()).thenReturn(String.valueOf(endereco));

		ResponseEntity<PagamentoDto> response = pagamentoResource.cadastrar(dto, uri);

		assertEquals(pagamento, response.getBody());
		assertEquals(201, response.getStatusCodeValue());
		assertEquals(endereco, response.getHeaders().getLocation());
	}

	@Test
	void atualizar_deveRetornarPagamentoAtualizado() {
		Long id = 1L;
		PagamentoDto dto = mock(PagamentoDto.class);
		PagamentoDto pagamentoDto = mock(PagamentoDto.class);
		when(pagamentoService.atualizarPagamento(id, dto)).thenReturn(pagamentoDto);

		ResponseEntity<PagamentoDto> response = pagamentoResource.atualizar(id, dto);

		assertEquals(pagamentoDto, response.getBody());
		assertEquals(200, response.getStatusCodeValue());
	}

	@Test
	void deletar_deveRetornarNoContent() {
		Long id = 1L;

		ResponseEntity<PagamentoDto> response = pagamentoResource.deletar(id);

		assertEquals(204, response.getStatusCodeValue());
		verify(pagamentoService, times(1)).excluirPagamento(id);
	}

	@Test
	void confirmaPagamento_deveChamarPagamentoService() {
		Long id = 1L;

		pagamentoResource.confirmaPagamento(id);

		verify(pagamentoService, times(1)).confirmaPagamento(id);
	}

	@Test
	void pagamentoAutorizadoSemIntegracao_deveChamarPagamentoService() {
		Long id = 1L;
		Exception ex = mock(Exception.class);

		pagamentoResource.pagamentoAutorizadoSemIntegracao(id, ex);

		verify(pagamentoService, times(1)).alteraStatus(id);
	}
}
