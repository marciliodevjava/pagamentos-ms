package br.com.alura.food.pagamentos.http;

import br.com.alura.food.pagamentos.dto.ItensPedido;
import br.com.alura.food.pagamentos.dto.Pedido;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("pedidos-ms")
public interface PedidoClients{

    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/pago")
    void atualizaPagamento(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/{id}")
    Pedido itensDopedido(@PathVariable Long id);
}
