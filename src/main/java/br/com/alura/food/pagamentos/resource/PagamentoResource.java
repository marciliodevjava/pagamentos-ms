package br.com.alura.food.pagamentos.resource;

import br.com.alura.food.pagamentos.dto.PagamentoDto;
import br.com.alura.food.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@RequestMapping("pagamentos")
public class PagamentoResource {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<Page<PagamentoDto>> obterTodos(@PageableDefault(size = 10, page = 0, sort = "nome")Pageable paginacao){
        return ResponseEntity.ok(pagamentoService.obterTodos(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDto> obterPorId(@PathVariable @NotNull Long id){
        PagamentoDto dto = pagamentoService.obterPorID(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto dto, UriComponentsBuilder uri){
        PagamentoDto pagamento = pagamentoService.criarPagamento(dto);
        URI endereco = uri.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

        return ResponseEntity.created(endereco).body(pagamento);
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<PagamentoDto> atualizar(@PathVariable @NotNull Long id, @RequestBody PagamentoDto dto){
        PagamentoDto pagamentoDto = pagamentoService.atualizarPagamento(id, dto);

        return ResponseEntity.ok(pagamentoDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoDto> deletar(@PathVariable @NotNull Long id){
        pagamentoService.excluirPagamento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/port")
    public String retornaPort(@Value("${local.server.port}")String port){
        return String.format("Requisição respondida pela instancia na porta %s", port);
    }

   @PatchMapping("/{id}/confirmar")
   @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoSemIntegracao")
    public void confirmaPagamento(@PathVariable Long id){
        pagamentoService.confirmaPagamento(id);
    }

    public void pagamentoAutorizadoSemIntegracao(@PathVariable Long id, Exception ex){
        pagamentoService.alteraStatus(id);
    }
}
