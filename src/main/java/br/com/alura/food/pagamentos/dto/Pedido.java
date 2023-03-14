package br.com.alura.food.pagamentos.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Pedido{

    private Long id;
    private List<ItensPedido> itens;
}
