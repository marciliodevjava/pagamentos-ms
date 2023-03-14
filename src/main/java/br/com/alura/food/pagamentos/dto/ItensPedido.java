package br.com.alura.food.pagamentos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItensPedido {

    private Long id;
    private Integer quantidade;
    private String descricao;
}
