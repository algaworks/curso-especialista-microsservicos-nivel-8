-- Sem chave estrangeira para "order": pedidos e itens chegam por tópicos diferentes,
-- e a ordem de chegada entre eles não é garantida.
create table order_item (
    id bigint not null,
    order_id bigint not null,
    product_id uuid,
    product_name varchar(255),
    price numeric(38, 2),
    quantity integer,
    total_amount numeric(38, 2),
    constraint pk_order_item primary key (id)
);

create index idx_order_item_order_id on order_item (order_id);
create index idx_order_item_product_id on order_item (product_id);
