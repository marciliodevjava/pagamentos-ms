package br.com.alura.food.pagamentos.service;

import br.com.alura.food.pagamentos.domain.Pagamento;
import br.com.alura.food.pagamentos.domain.enuns.Status;
import br.com.alura.food.pagamentos.dto.ItensPedido;
import br.com.alura.food.pagamentos.dto.PagamentoDto;
import br.com.alura.food.pagamentos.dto.Pedido;
import br.com.alura.food.pagamentos.http.PedidoClients;
import br.com.alura.food.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {


    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PedidoClients pedidoClients;

    public Page<PagamentoDto> obterTodos(Pageable paginacao) {
        return pagamentoRepository.findAll(paginacao).map(p -> modelMapper.map(p, PagamentoDto.class));
    }

    public PagamentoDto obterPorID(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());

        Pedido intens = pedidoClients.itensDopedido(pagamento.getPedidoId());

        PagamentoDto pagamentoDto = new PagamentoDto();
        pagamentoDto.setId(pagamento.getId());
        pagamentoDto.setValor(pagamento.getValor());
        pagamentoDto.setNome(pagamento.getNome());
        pagamentoDto.setExpiracao(pagamento.getExpiracao());
        pagamentoDto.setCodigo(pagamento.getCodigo());
        pagamentoDto.setStatus(pagamento.getStatus());
        pagamentoDto.setFormaDePagamentoId(pagamento.getFormaDePagamentoId());
        pagamentoDto.setPedidoId(pagamento.getPedidoId());
        pagamentoDto.setPedido(intens);


        return pagamentoDto;
    }

    public PagamentoDto criarPagamento(PagamentoDto dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluirPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }

    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        Optional<Pagamento> buscarBanco = pagamentoRepository.findById(id);
        if (buscarBanco.isPresent()) {
            Pagamento retorno = pagamentoRepository.save(this.atualizaDados(pagamento, buscarBanco));

            return modelMapper.map(retorno, PagamentoDto.class);
        }

        return null;
    }

    public void confirmaPagamento(Long id) {
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento.get());
        pedidoClients.atualizaPagamento(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRepository.save(pagamento.get());
    }

    private Pagamento atualizaDados(Pagamento dados, Optional<Pagamento> retornoBanco) {

        Pagamento atualizar = retornoBanco.get();
        Pagamento retorno = new Pagamento();


        retorno.setId(dados.getId() != null ? dados.getId() : atualizar.getId());
        retorno.setValor(dados.getValor() != null ? dados.getValor() : atualizar.getValor());
        retorno.setNome(dados.getNome() != null ? dados.getNome() : atualizar.getNome());
        retorno.setNumero(dados.getNumero() != null ? dados.getNumero() : atualizar.getNumero());
        retorno.setExpiracao(dados.getExpiracao() != null ? dados.getExpiracao() : atualizar.getExpiracao());
        retorno.setCodigo(dados.getCodigo() != null ? dados.getCodigo() : atualizar.getCodigo());
        retorno.setStatus(dados.getStatus() != null ? dados.getStatus() : atualizar.getStatus());
        retorno.setPedidoId(dados.getPedidoId() != null ? dados.getPedidoId() : atualizar.getPedidoId());
        retorno.setFormaDePagamentoId(dados.getFormaDePagamentoId() != null ? dados.getFormaDePagamentoId() : atualizar.getFormaDePagamentoId());

        return retorno;
    }
}
