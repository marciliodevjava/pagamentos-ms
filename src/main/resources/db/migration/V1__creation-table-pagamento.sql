create table pagamentos(
    id bigint(20) not null AUTO_INCREMENT,
    valor decimal(19,2) not null,
    nome varchar(150) default null,
    numero varchar(19) default null,
    expiracao varchar(7) not null,
    codigo varchar(3) not null,
    status varchar(255) not null,
    forma_de_pagamento_id bigint(20) not null,
    pedido_id bigint(20) not null,
    primary key (id)
);